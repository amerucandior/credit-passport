package com.passport.creditpassport.auth.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginRequest {

    @NotNull(message = "Enter details")
    private String userName;
    private String userPassword;
}
