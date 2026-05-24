package com.passport.creditpassport.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiSecurityDocumentationTest {

    private static final String DEBUG_LOG_PATH = "/home/ian/code/credit-passport/.cursor/debug-1dcf2d.log";
    private static final String SESSION_ID = "1dcf2d";
    private static final String RUN_ID = "openapi-security-consistency";
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiDocs_shouldMatchPublicAndProtectedAuthRules() throws Exception {
        String body = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = JSON.readTree(body);

        boolean hasGlobalSecurity = root.has("security")
                && root.get("security").isArray()
                && root.get("security").size() > 0;
        // #region agent log
        debugLog("H1", "OpenApiSecurityDocumentationTest:globalSecurity", "Global OpenAPI security requirement present", Map.of("hasGlobalSecurity", hasGlobalSecurity));
        // #endregion
        assertThat(hasGlobalSecurity).isFalse();

        JsonNode schemes = root.path("components").path("securitySchemes");
        boolean hasBearerScheme = schemes.has("bearerAuth");
        boolean hasApiKeyScheme = schemes.has("apiKeyAuth");
        // #region agent log
        debugLog("H2", "OpenApiSecurityDocumentationTest:schemes", "Security schemes registered", Map.of("hasBearerScheme", hasBearerScheme, "hasApiKeyScheme", hasApiKeyScheme));
        // #endregion
        assertThat(hasBearerScheme).isTrue();
        assertThat(hasApiKeyScheme).isTrue();

        JsonNode authRegisterSecurity = operation(root, "/api/auth/register", "post").path("security");
        JsonNode lenderRegisterSecurity = operation(root, "/api/lender/register", "post").path("security");
        boolean authRegisterIsPublic = isPublic(authRegisterSecurity);
        boolean lenderRegisterIsPublic = isPublic(lenderRegisterSecurity);
        // #region agent log
        debugLog("H3", "OpenApiSecurityDocumentationTest:publicEndpoints", "Public endpoints security metadata", Map.of(
                "authRegisterIsPublic", authRegisterIsPublic,
                "lenderRegisterIsPublic", lenderRegisterIsPublic
        ));
        // #endregion
        assertThat(authRegisterIsPublic).isTrue();
        assertThat(lenderRegisterIsPublic).isTrue();

        JsonNode profileGetSecurity = operation(root, "/api/profile", "get").path("security");
        JsonNode lenderCreditSecurity = operation(root, "/api/lender/credit-passport", "post").path("security");
        JsonNode lenderHealthSecurity = operation(root, "/api/lender/health", "get").path("security");

        boolean profileHasBearer = hasScheme(profileGetSecurity, "bearerAuth");
        boolean lenderCreditHasBearer = hasScheme(lenderCreditSecurity, "bearerAuth");
        boolean lenderCreditHasApiKey = hasScheme(lenderCreditSecurity, "apiKeyAuth");
        boolean lenderHealthHasBearer = hasScheme(lenderHealthSecurity, "bearerAuth");
        boolean lenderHealthHasApiKey = hasScheme(lenderHealthSecurity, "apiKeyAuth");
        // #region agent log
        debugLog("H4", "OpenApiSecurityDocumentationTest:protectedEndpoints", "Protected endpoints security metadata", Map.of(
                "profileHasBearer", profileHasBearer,
                "lenderCreditHasBearer", lenderCreditHasBearer,
                "lenderCreditHasApiKey", lenderCreditHasApiKey,
                "lenderHealthHasBearer", lenderHealthHasBearer,
                "lenderHealthHasApiKey", lenderHealthHasApiKey
        ));
        // #endregion

        assertThat(profileHasBearer).isTrue();
        assertThat(lenderCreditHasBearer).isFalse();
        assertThat(lenderCreditHasApiKey).isTrue();
        assertThat(lenderHealthHasBearer).isFalse();
        assertThat(lenderHealthHasApiKey).isTrue();
    }

    private JsonNode operation(JsonNode root, String path, String method) {
        return root.path("paths").path(path).path(method);
    }

    private boolean isPublic(JsonNode securityNode) {
        return securityNode.isMissingNode()
                || securityNode.isNull()
                || (securityNode.isArray() && securityNode.isEmpty());
    }

    private boolean hasScheme(JsonNode securityNode, String schemeName) {
        if (!securityNode.isArray()) {
            return false;
        }
        for (JsonNode requirement : securityNode) {
            if (requirement.has(schemeName)) {
                return true;
            }
        }
        return false;
    }

    private void debugLog(String hypothesisId, String location, String message, Map<String, Object> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sessionId", SESSION_ID);
            payload.put("runId", RUN_ID);
            payload.put("hypothesisId", hypothesisId);
            payload.put("location", location);
            payload.put("message", message);
            payload.put("data", data);
            payload.put("timestamp", Instant.now().toEpochMilli());

            String line = JSON.writeValueAsString(payload) + System.lineSeparator();
            Files.writeString(Path.of(DEBUG_LOG_PATH), line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {
        }
    }
}
