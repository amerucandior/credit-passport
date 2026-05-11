package com.passport.creditpassport.lender.lendercontroller;

import com.passport.creditpassport.creditpassport.CreditPassport;
import com.passport.creditpassport.creditpassport.CreditPassportQueryPort;
import com.passport.creditpassport.lender.lenderdto.LenderResponse;
import com.passport.creditpassport.lender.lendermodel.Lender;
import com.passport.creditpassport.lender.lenderservice.LenderService;
import com.passport.creditpassport.lender.security.ApiKeyAuthFilter;
import com.passport.creditpassport.config.JwtAuthenticationFilter;
import com.passport.creditpassport.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = LenderController.class,
        excludeFilters = {
                @org.springframework.context.annotation.ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @org.springframework.context.annotation.ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
                @org.springframework.context.annotation.ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE, classes = ApiKeyAuthFilter.class)
        })
class LenderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LenderService lenderService;

    @Autowired
    private CreditPassportQueryPort creditPassportQueryPort;

    @TestConfiguration
    static class MockConfig {
        @Bean
        LenderService lenderService() {
            return org.mockito.Mockito.mock(LenderService.class);
        }

        @Bean
        CreditPassportQueryPort creditPassportQueryPort() {
            return org.mockito.Mockito.mock(CreditPassportQueryPort.class);
        }
    }

    @Test
    @WithMockUser
    void register_returnsCreated_withApiKeyAndMessage() throws Exception {
        LenderResponse response = LenderResponse.builder()
                .apiKey("plain-api-key")
                .message("Lender registered")
                .build();

        given(lenderService.registerLender(any())).willReturn(response);

        String payload = """
                {
                  "lenderName": "Acme Finance",
                  "cbkLicenseNo": "CBK-12345",
                  "certificateOfIncorporation": "CERT-987",
                  "memorandumArticlesOfAssociation": "MOA-001",
                  "regBusinessAddress": "Nairobi"
                }
                """;

        mockMvc.perform(post("/api/lender/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.apiKey").value("plain-api-key"))
                .andExpect(jsonPath("$.message").value("Lender registered"));
    }

    @Test
    @WithMockUser
    void getCreditPassport_returnsUnauthorized_whenLicenseMismatchesAuthenticatedLender() throws Exception {
        Lender lender = new Lender();
        lender.setCbkLicenseNo("CBK-777");

        String payload = """
                {
                  "cbkLicenseNo": "CBK-OTHER",
                  "apiKey": "unused-in-controller",
                  "nationalId": "12345678"
                }
                """;

        mockMvc.perform(post("/api/lender/credit-passport")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("authenticatedLender", lender)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getCreditPassport_returnsPassport_whenLicenseMatchesAuthenticatedLender() throws Exception {
        Lender lender = new Lender();
        lender.setCbkLicenseNo("CBK-777");

        CreditPassport passport = new CreditPassport();
        passport.setNationalId("12345678");
        passport.setName("Jane Borrower");
        passport.setCreditScore(740);

        given(creditPassportQueryPort.getCreditPassport(eq("12345678"))).willReturn(passport);

        String payload = """
                {
                  "cbkLicenseNo": "CBK-777",
                  "apiKey": "unused-in-controller",
                  "nationalId": "12345678"
                }
                """;

        mockMvc.perform(post("/api/lender/credit-passport")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("authenticatedLender", lender)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nationalId").value("12345678"))
                .andExpect(jsonPath("$.name").value("Jane Borrower"))
                .andExpect(jsonPath("$.creditScore").value(740));
    }

    @Test
    @WithMockUser
    void health_returnsOk() throws Exception {
        mockMvc.perform(get("/api/lender/health"))
                .andExpect(status().isOk());
    }
}
