package com.senna.senna.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.DTO.UpdateUserDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Entity.PsychologistProfile;
import com.senna.senna.Entity.Review;
import com.senna.senna.Entity.Role;
import com.senna.senna.Entity.User;
import com.senna.senna.Entity.ProfileStatus;
import com.senna.senna.Mapper.UserMapper;
import com.senna.senna.Repository.PsychologistProfileRepository;
import com.senna.senna.Repository.ReviewRepository;
import com.senna.senna.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PsychologistProfileRepository profileRepository;

    /**
     * Crea un nuevo usuario y lo guarda en la base de datos.
     */
    @Override
    public UserResponseDTO createUser(CreateUserDTO dto) {
        User user = UserMapper.fromDTO(dto);

        if (user.getRole() == Role.PATIENT) {
            user.setActive(true);
        }

        User saved = userRepository.save(user);
        return UserMapper.toResponseDTO(saved);
    }

    /**
     * Obtiene todos los usuarios con estadísticas de reseñas si son psicólogos.
     */
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUserToDTOWithReviewStats)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los psicólogos incluyendo sus estadísticas de valoración.
     */
    public List<UserResponseDTO> getAllPsychologists() {
        return userRepository.findByRole(Role.PSYCHOLOGIST)
                .stream()
                .map(this::mapUserToDTOWithReviewStats)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por su ID.
     */
    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
        return mapUserToDTOWithReviewStats(user);
    }

    /**
     * Obtiene un usuario por su email
     */
    @Override
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapUserToDTOWithReviewStats);
    }

    /**
     * Devuelve todos los pacientes registrados.
     */
    @Override
    public List<UserResponseDTO> getAllPatients() {
        return userRepository.findByRole(Role.PATIENT)
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Asigna un paciente a un psicólogo.
     */
    @Override
    public void assignPatientToPsychologist(Long psychologistId, Long patientId) {
        User psy = userRepository.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + psychologistId));
        User pat = userRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado: " + patientId));
        if (psy.getRole() != Role.PSYCHOLOGIST || pat.getRole() != Role.PATIENT) {
            throw new IllegalArgumentException("Roles incorrectos para asignación");
        }
        psy.getPatients().add(pat);
        pat.getPsychologists().add(psy);
        userRepository.save(psy);
        userRepository.save(pat);
    }

    /**
     * Actualiza los datos de un usuario.
     */
    @Override
    public UserResponseDTO updateUser(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
        UserMapper.updateUserFromDTO(user, dto);
        User updated = userRepository.save(user);
        return UserMapper.toResponseDTO(updated);
    }

    /**
     * Elimina un usuario por ID.
     */
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
        userRepository.delete(user);
    }

    /**
     * Verifica si un email ya está registrado.
     */
    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Obtiene psicólogos filtrados por especialidad (ignore case).
     */
    public List<UserResponseDTO> getPsychologistsBySpecialty(String specialty) {
        return userRepository.findByRoleAndProfileSpecialtyContainingIgnoreCase(Role.PSYCHOLOGIST, specialty).stream()
                .map(this::mapUserToDTOWithReviewStats)
                .collect(Collectors.toList());
    }

    private static final String IMGBB_API_KEY = "T24a141e99bcbb243bbfab395507de11e";
    private static final String BOUNDARY = "SennaUploadBoundary";

    /**
     * Sube una imagen a ImgBB y devuelve la URL.
     */
    public String uploadImageToImgBB(MultipartFile image) {
        try {
            String apiKey = "24a141e99bcbb243bbfab395507de11e";
            String uploadUrl = "https://api.imgbb.com/1/upload?key=" + apiKey;

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofByteArray(image.getBytes());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .header("Content-Type", "multipart/form-data;boundary=----SennaBoundary")
                    .POST(ofMimeMultipartData(image, "image"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.body());

            return json.get("data").get("url").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen a ImgBB", e);
        }
    }

    /**
     * Construye el cuerpo multipart para enviar la imagen.
     */
    private static HttpRequest.BodyPublisher ofMimeMultipartData(MultipartFile image, String fieldName) throws IOException {
        String boundary = "----SennaBoundary";
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var writer = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8), true);

        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"")
                .append(fieldName).append("\"; filename=\"")
                .append(image.getOriginalFilename()).append("\"\r\n");
        writer.append("Content-Type: ").append(image.getContentType()).append("\r\n\r\n");
        writer.flush();

        byteArrayOutputStream.write(image.getBytes());
        byteArrayOutputStream.flush();

        writer.append("\r\n").flush();
        writer.append("--").append(boundary).append("--").append("\r\n");
        writer.close();

        return HttpRequest.BodyPublishers.ofByteArray(byteArrayOutputStream.toByteArray());
    }

    /**
     * Actualiza un usuario buscado por email.
     */
    public void updateUserByEmail(String email, UpdateUserDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + email));

        UserMapper.updateUserFromDTO(user, dto);
        userRepository.save(user);
    }

    /**
     * Agrega estadísticas de reseñas a un usuario si es psicólogo.
     */
    private UserResponseDTO mapUserToDTOWithReviewStats(User user) {
        UserResponseDTO dto = UserMapper.toResponseDTO(user);

        if (user.getRole() == Role.PSYCHOLOGIST) {
            List<Review> reviews = reviewRepository.findByPsychologist_Id(user.getId());
            int total = reviews.size();
            double average = total == 0 ? 0 : reviews.stream().mapToInt(Review::getRating).average().orElse(0);

            dto.setAverageRating(average);
            dto.setTotalReviews(total);
        }

        return dto;
    }

    /**
     * Devuelve los psicólogos pendientes de aprobación.
     */
    @Override
    public List<UserResponseDTO> findPendingPsychologists() {
        return userRepository.findByRoleAndProfile_Status(Role.PSYCHOLOGIST, ProfileStatus.PENDING)
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Aprueba el perfil de un psicólogo y activa su cuenta.
     */
    @Override
    public void approvePsychologist(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (user.getProfile() == null) throw new EntityNotFoundException("No tiene perfil profesional");

        user.getProfile().setStatus(ProfileStatus.APPROVED);
        user.setActive(true);

        profileRepository.save(user.getProfile());
        userRepository.save(user);
    }

    /**
     * Rechaza el perfil de un psicólogo (no elimina la cuenta).
     */
    @Override
    public void rejectPsychologist(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        if (user.getProfile() == null) throw new EntityNotFoundException("No tiene perfil profesional");
        user.getProfile().setStatus(ProfileStatus.REJECTED);
        profileRepository.save(user.getProfile());
    }

    @Override
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Desactiva (banea) un usuario.
     */
    public void acceptTerms(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + email));
        user.setTermsAccepted(true);
        userRepository.save(user);
    }
}
