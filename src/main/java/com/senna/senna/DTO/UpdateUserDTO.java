package com.senna.senna.DTO;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private String name;
    private String last_name;

    // Solo si es PSYCHOLOGIST y se permite editar estos datos
    private String qualification;
    private String specialty;
    private String location;
    private String document;
}