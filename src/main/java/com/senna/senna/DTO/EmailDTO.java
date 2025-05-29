package com.senna.senna.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailDTO {

    // Información base
    private String nombreApp = "Senna";
    private String asunto = "Tienes una notificación de Senna.";
    private String finalMensaje = "Atentamente, " + nombreApp;

    private String urlAdmin = "https://senna-frontend-production.up.railway.app/admin";
    private String urlHome = "https://senna-frontend-production.up.railway.app/home";
    private String urlRecuperarClave = "https://senna-frontend-production.up.railway.app/change-password";
    private String urlLogin = "https://senna-frontend-production.up.railway.app/login";

    // Datos personalizables para el contenido del correo
    private String destinatario;
    private String cuerpo;
    private String titulo;
    private String motivo;
    private String cambios;
}