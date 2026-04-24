package com.passport.creditpassport.auth.Dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import lombok.Setter;


@Setter
@Getter
public class RegisterRequest {

    @NotNull(message = "Enter details")
    private String userName;
    private Integer userNumber;
    private String userPassword;
    private String userNationalId;
}
