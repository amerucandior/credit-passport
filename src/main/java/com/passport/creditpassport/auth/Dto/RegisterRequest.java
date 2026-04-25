package com.passport.creditpassport.auth.Dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import lombok.Setter;


@Setter
@Getter
@Data
public class RegisterRequest {

    @NotNull(message = "Enter details")
    private String userName;
    private String userNumber;
    private String userPassword;
    private String userNationalId;
}
