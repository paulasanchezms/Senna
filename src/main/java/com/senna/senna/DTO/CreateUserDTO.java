package com.senna.senna.DTO;

import com.senna.senna.Entity.Role;
import lombok.Data;

@Data
public class CreateUserDTO {
    private String name;
    private String last_name;
    private String email;
    private String password;
    private Role role;
}