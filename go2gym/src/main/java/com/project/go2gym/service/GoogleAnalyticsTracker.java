package com.project.go2gym.service;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleAnalyticsTracker {

    private final RestTemplate restTemplate;
    
    public GoogleAnalyticsTracker() {
        this.restTemplate = new RestTemplate();
    }

    public void trackEvent(String category, String action, String label, int userId) {
        // Construct the payload using the Measurement Protocol parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("v", "1"); // API Version.
        params.add("tid", "G-KRNG3F5JC2"); // Replace with your actual Measurement ID.
        // Client ID - Unique identifier for each user
        params.add("cid", generateClientIdForUser(userId)); // Replace with a method to generate or retrieve a unique client ID.
        params.add("t", "event"); // Event hit type
        params.add("ec", category); // Event Category
        params.add("ea", action); // Event Action
        params.add("el", label); // Event label
    
        // Asynchronously send the event data to Google Analytics
        restTemplate.postForEntity("https://www.google-analytics.com/collect", params, String.class);
    }
    
    private String generateClientIdForUser(int userId) {
        // Implement a method to generate a unique client ID for each user if you're not using UUID
        // For example, you could use a UUID string and associate it with the user in the database.
        // Return that UUID string here. For now, let's just convert the user ID to a string:
        return Integer.toString(userId);
    }
}
