package com.passport.creditpassport.scoring.service;

import com.passport.creditpassport.exception.ScoringServiceException;
import com.passport.creditpassport.scoring.dto.ScoringRequest;
import com.passport.creditpassport.scoring.dto.ScoringResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ScoringServiceClient {
    private final RestClient restClient;

    public ScoringServiceClient(
            @Value("${app.scoring.base-url}")
            String baseUrl,

            @Value("${app.scoring.api-key}")
            String apiKey
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-API-KEY", apiKey)
                .build();
    }

    public ScoringResponse score(ScoringRequest request) {
        return restClient.post()
                .uri("/score")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, response) -> {
                    throw new ScoringServiceException("Scoring failed: " + response.getStatusCode());
                })
                .body(ScoringResponse.class);
    }
}