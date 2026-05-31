package com.smarthospital.modules.auth.dto;

import com.smarthospital.modules.auth.domain.Role;
import com.smarthospital.modules.auth.domain.User;

import java.util.UUID;

public record UserResponse(
        UUID   id,
        String email,
        String firstName,
        String lastName,
        Role   role,
        String tenantId
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(), u.getEmail(),
                u.getFirstName(), u.getLastName(),
                u.getRole(), u.getTenantId()
        );
    }
}
