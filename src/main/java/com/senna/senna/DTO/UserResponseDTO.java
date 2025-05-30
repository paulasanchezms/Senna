package com.senna.senna.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.senna.senna.Entity.Role;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {
    private Long id_user;
    private String name;
    private String last_name;
    private String email;
    private Role role;
    private String phone;
    private String photoUrl;
    private PsychologistProfileDTO profile;
    private Double averageRating;
    private Integer totalReviews;
    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean termsAccepted;
}