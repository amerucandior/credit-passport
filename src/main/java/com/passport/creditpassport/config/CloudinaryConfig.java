package com.passport.creditpassport.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(
            @Value("${app.cloudinary.cloudname}")
            String cloudName,
            @Value("${app.cloudinary.api-key}")
            String apiKey,
            @Value("${app.cloudinary.api-secret}")
            String apiSecret,
            @Value("${app.cloudinary.api-env-variable}")
            String apiEnvVariable
    ) {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "api_env_variable", apiEnvVariable,
                "secure", true
        ));

    }
}
