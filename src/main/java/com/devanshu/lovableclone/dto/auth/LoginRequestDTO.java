package com.devanshu.lovableclone.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "Username is required") String username,
        @NotNull(message = "Password is required") @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters") String password) {
}
