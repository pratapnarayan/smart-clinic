package com.smartclinic.modules.auth.dto;

public record LoginResponse(
        TokenResponse tokens,
        UserResponse  user
) {}
