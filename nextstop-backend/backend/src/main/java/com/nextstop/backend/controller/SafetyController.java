package com.nextstop.backend.controller;

import com.nextstop.backend.dto.LocationRequest;
import com.nextstop.backend.service.SafetyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Added this missing import
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/safety")
@CrossOrigin(origins = "*")
public class SafetyController {

    @Autowired
    private SafetyService safetyService;

    // Existing endpoint for checking location
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkSafety(@RequestBody LocationRequest request) {
        String status = safetyService.getSafetyDecision(request);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", request.getUserId());
        response.put("status", status);
        return ResponseEntity.ok(response);
    }

    // New endpoint for the "I am Safe" button
    @PostMapping("/confirm-safe")
    public ResponseEntity<String> confirmSafe(@RequestParam String userId) {
        boolean updated = safetyService.markUserAsSafe(userId);
        if (updated) {
            return ResponseEntity.ok("Safety confirmed. Emergency alerts cancelled.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No active anomaly found for this user.");
    }
}