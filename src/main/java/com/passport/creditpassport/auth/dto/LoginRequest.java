package com.passport.creditpassport.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(
            description = "Email address or Kenyan national ID used to log in",
            example = "johndoe@example.com"
    )
    @NotBlank(message = "Enter your email address or national ID")
    private String identifier;

    @Schema(description = "Account password", example = "123@#$df")
    @NotBlank(message = "Enter your password")
    private String userPassword;

}
