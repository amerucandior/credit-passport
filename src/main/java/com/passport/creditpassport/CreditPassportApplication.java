package com.passport.creditpassport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // @Configuration + @EnableAutoConfiguration + @ComponentScan
public class CreditPassportApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditPassportApplication.class, args);
    }

}
