package com.project.go2gym.controllers;

import com.project.go2gym.service.MixpanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics") // Adjust the mapping based on your API design
public class AnalyticsController {

    private final MixpanelService mixpanelService;

    @Autowired
    public AnalyticsController(MixpanelService mixpanelService) {
        this.mixpanelService = mixpanelService;
    }

    @GetMapping("/check-in-events")
    public ResponseEntity<?> getCheckInEvents(@RequestParam String from, @RequestParam String to) {
        try {
            String data = mixpanelService.getCheckInEventData(from, to);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            // Log and handle the exception properly
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error fetching event data: " + e.getMessage());
        }
    }
}

