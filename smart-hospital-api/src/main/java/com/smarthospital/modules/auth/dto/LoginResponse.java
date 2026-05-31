package com.smarthospital.modules.auth.dto;

public record LoginResponse(
        TokenResponse tokens,
        UserResponse  user
) {}
