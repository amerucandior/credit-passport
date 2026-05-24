package com.passport.creditpassport.scoring.service;

import com.passport.creditpassport.exception.ScoringServiceException;
import com.passport.creditpassport.scoring.dto.ScoringRequest;
import com.passport.creditpassport.scoring.dto.ScoringResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class ScoringServiceClient {
    private final RestClient restClient;
    private final String apiKey;

    public ScoringServiceClient(
            @Value("${app.scoring.base-url}")
            String baseUrl,

            @Value("${app.scoring.api-key}")
            String apiKey
    ) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    log.info("Outgoing headers: {}", request.getHeaders());
                    return execution.execute(request, body);
                })
                .build();
    }

    public ScoringResponse score(ScoringRequest request) {
        return restClient.post()
                .uri("/score")
                .header("X-API-KEY", apiKey)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, response) -> {
                    throw new ScoringServiceException("Scoring failed: " + response.getStatusCode());
                })
                .body(ScoringResponse.class);
    }
}