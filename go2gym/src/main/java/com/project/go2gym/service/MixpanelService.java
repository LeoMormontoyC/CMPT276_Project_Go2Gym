package com.project.go2gym.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class MixpanelService {

    @Value("${mixpanel.api.secret}")
    private String apiSecret;

    private final RestTemplate restTemplate;

    public MixpanelService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCheckInEventData(String from, String to) {
        String apiUrl = "https://data.mixpanel.com/api/2.0/export";
        String eventFilter = "[\"Check-In\"]"; // Adjust if your event name is different
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("from_date", from)
                .queryParam("to_date", to)
                .queryParam("event", eventFilter);

        HttpHeaders headers = createHeaders();

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);

        return response.getBody();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = Base64.getEncoder().encodeToString((apiSecret + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + auth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Encoding", "gzip");
        return headers;
    }
}