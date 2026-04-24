package com.passport.creditpassport.auth.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginRequest {

    private String userName;
    private String userPassword;
}
