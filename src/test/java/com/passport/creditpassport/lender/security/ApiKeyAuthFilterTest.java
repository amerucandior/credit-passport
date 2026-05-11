package com.passport.creditpassport.lender.security;

import com.passport.creditpassport.lender.lendermodel.Lender;
import com.passport.creditpassport.lender.repository.LenderRepository;
import com.passport.creditpassport.lender.util.ApiKeyUtil;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class ApiKeyAuthFilterTest {

    @Mock
    private LenderRepository lenderRepository;

    private ApiKeyAuthFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new ApiKeyAuthFilter(lenderRepository);
    }

    @Test
    void doFilter_skipsNonLenderPaths() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/profile/me");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void doFilter_returns401_whenApiKeyMissing_onProtectedLenderPath() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/lender/data");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).isEqualTo("{\"error\":\"Missing API key\"}");
    }

    @Test
    void doFilter_returns401_whenApiKeyInvalid() throws ServletException, IOException {
        String rawKey = "invalid-key";
        String hashedKey = ApiKeyUtil.hash(rawKey);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/lender/data");
        request.addHeader("X-API-KEY", rawKey);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        given(lenderRepository.findByApiKey(hashedKey)).willReturn(Optional.empty());

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).isEqualTo("{\"error\":\"Invalid API key\"}");
    }

    @Test
    void doFilter_setsAuthenticatedLenderAndContinues_whenApiKeyValid() throws ServletException, IOException {
        String rawKey = "valid-key";
        String hashedKey = ApiKeyUtil.hash(rawKey);
        Lender lender = new Lender();
        lender.setCbkLicenseNo("CBK-555");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/lender/data");
        request.addHeader("X-API-KEY", rawKey);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        given(lenderRepository.findByApiKey(hashedKey)).willReturn(Optional.of(lender));

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(request.getAttribute("authenticatedLender")).isSameAs(lender);
    }
}
