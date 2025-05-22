package com.senna.senna.DTO;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private String name;
    private String last_name;
    private String phone;
    private String photoUrl;
}