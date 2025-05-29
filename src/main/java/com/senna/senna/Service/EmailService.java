package com.senna.senna.Service;

import com.senna.senna.DTO.EmailDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Envía un correo electrónico usando la plantilla 'email.html'.
     *
     * @param emailDTO datos del correo (destinatario, asunto, cuerpo, etc.)
     * @param url      enlace de acción (por ejemplo, restablecer contraseña)
     */
    public void enviarCorreo(EmailDTO emailDTO, String url) {
        try {
            Context context = new Context();
            context.setVariable("titulo", emailDTO.getTitulo());
            context.setVariable("cuerpo", emailDTO.getCuerpo());
            context.setVariable("finalMensaje", emailDTO.getFinalMensaje());
            context.setVariable("motivo", emailDTO.getMotivo());

            if (url != null && !url.isEmpty()) {
                context.setVariable("url", url);
            }

            String cuerpoHTML = templateEngine.process("email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailDTO.getDestinatario());
            helper.setSubject(emailDTO.getAsunto());
            helper.setText(cuerpoHTML, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error al enviar el correo a {}", emailDTO.getDestinatario(), e);
            throw new IllegalStateException("No se pudo enviar el correo electrónico.");
        }
    }
}