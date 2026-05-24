package com.passport.creditpassport.userprofile.profileController;

import com.passport.creditpassport.auth.WithMockAppUser;
import com.passport.creditpassport.userprofile.service.ProfileService;
import com.passport.creditpassport.config.JwtAuthenticationFilter;
import com.passport.creditpassport.config.SecurityConfig;
import com.passport.creditpassport.exception.GlobalExceptionHandler;
import com.passport.creditpassport.exception.InvalidProfilePhotoUrlException;
import com.passport.creditpassport.exception.ProfileAlreadyExistsException;
import com.passport.creditpassport.lender.security.ApiKeyAuthFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProfileController.class,
        excludeFilters = {
                @org.springframework.context.annotation.ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @org.springframework.context.annotation.ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
                @org.springframework.context.annotation.ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE, classes = ApiKeyAuthFilter.class)
        })
@Import({GlobalExceptionHandler.class, ProfileControllerErrorHandlingTest.MockConfig.class})
class ProfileControllerErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileService profileService;

    @BeforeEach
    void resetMocks() {
        reset(profileService);
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        ProfileService profileService() {
            return org.mockito.Mockito.mock(ProfileService.class);
        }
    }

    @Test
    @WithMockAppUser
    void createProfile_returnsBadRequest_whenProfilePhotoUrlIsInvalid() throws Exception {
        String requestBody = """
                {
                  "profilePhotoUrl": "ftp://photo.example.com/avatar.png"
                }
                """;

        given(profileService.createProfile(any(), any(), any(), any(), any(), any()))
                .willThrow(new InvalidProfilePhotoUrlException());

        mockMvc.perform(post("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Profile photo URL is invalid or uses a disallowed scheme."));
    }

    @Test
    @WithMockAppUser
    void createProfile_returnsConflict_whenProfileAlreadyExists() throws Exception {
        String requestBody = "{}";

        given(profileService.createProfile(any(), any(), any(), any(), any(), any()))
                .willThrow(new ProfileAlreadyExistsException("user-123"));

        mockMvc.perform(post("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Profile already exists for user user-123"));
    }
}
