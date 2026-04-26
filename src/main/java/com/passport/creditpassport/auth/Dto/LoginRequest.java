package com.passport.creditpassport.auth.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginRequest {

    @NotBlank
    @Pattern(regexp = "\\d{7,8}", message = "Invalid Kenyan ID")
    private String userNationalId;

    @NotNull
    private String userPassword;
}
