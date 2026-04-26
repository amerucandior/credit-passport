package com.passport.creditpassport.auth.Dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

import lombok.Setter;
import org.aspectj.bridge.Message;


@Setter
@Getter
@Data
public class RegisterRequest {

    @NotBlank(message = "Enter a valid username") @Size(min = 3, max = 50)
    private String name;

    @NotBlank(message = "Enter a valid Phone Number")
    private String number;

    @NotNull @Size(min = 8, max = 100)
    private String userPassword;

    @NotBlank @Pattern(regexp = "\\d{7,8}", message = "Invalid Kenyan ID")
    private String userNationalId;
}
