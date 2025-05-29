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

    // URLs para entorno local
    private String urlAdmin = "http://localhost:8100/admin";
    private String urlHome = "http://localhost:8100/home";
    private String urlRecuperarClave = "http://localhost:8100/change-password";
    private String urlLogin = "http://localhost:8100/login";

    // Datos personalizables para el contenido del correo
    private String destinatario;
    private String cuerpo;
    private String titulo;
    private String motivo;
    private String cambios;
}