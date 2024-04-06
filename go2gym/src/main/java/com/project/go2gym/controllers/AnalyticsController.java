// package com.project.go2gym.controllers;

// import org.springframework.web.bind.annotation.RestController;
// import java.util.List; // Example import statement
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpMethod;


// @RestController
// public class AnalyticsController {

//     @Value("${mixpanel.api.secret}")
//     private String apiSecret;

//     @Autowired
//     private RestTemplate restTemplate;

//     @GetMapping("/api/analytics/checkins")
//     public ResponseEntity<?> getCheckInData() {
//         String apiUrl = "https://mixpanel.com/api/2.0/..."; // Replace with the actual endpoint

//         HttpHeaders headers = new HttpHeaders();
//         headers.setBasicAuth(apiSecret, "");
//         headers.setContentType(MediaType.APPLICATION_JSON);

//         HttpEntity<String> entity = new HttpEntity<>(headers);

//         ResponseEntity<String> response = restTemplate.exchange(
//                 apiUrl,
//                 HttpMethod.GET,
//                 entity,
//                 String.class
//         );

//         // Process the response as needed and return

//         return ResponseEntity.ok(response.getBody());
//     }
// }
