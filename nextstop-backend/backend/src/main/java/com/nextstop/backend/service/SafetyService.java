package com.nextstop.backend.service;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.nextstop.backend.dto.LocationRequest;
import com.nextstop.backend.model.JourneyLog;
import com.nextstop.backend.model.SafetyAlert;
import com.nextstop.backend.repository.JourneyRepository;
import com.nextstop.backend.repository.SafetyAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SafetyService {

    @Autowired
    private JourneyRepository journeyRepository;

    @Autowired
    private SafetyAlertRepository safetyAlertRepository;

    @Autowired
    private EmergencyContactService emergencyContactService; // 👈 Fixed name and added @Autowired

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PYTHON_API_URL = "http://127.0.0.1:8001/verify-safety";

    public String getSafetyDecision(LocationRequest request) {
        Map<String, Double> payload = new HashMap<>();
        payload.put("curr_lat", request.getLat());
        payload.put("curr_lon", request.getLon());
        payload.put("prev_lat", request.getPrevLat());
        payload.put("prev_lon", request.getPrevLon());

        try {
            // 1. Get Decision from Python AI
            Map<String, Object> response = restTemplate.postForObject(PYTHON_API_URL, payload, Map.class);
            String status = (String) response.get("status");

            // 2. Save EVERYTHING to Journey Logs
            saveToJourneyLogs(request, status);

            // 3. If it's an ANOMALY, start the safety protocol
            if ("anomaly".equals(status)) {
                saveToSafetyAlerts(request, status);
            }

            return status;
        } catch (Exception e) {
            System.err.println("Error calling Python ML: " + e.getMessage());
            return "service_unavailable";
        }
    }

    private void saveToJourneyLogs(LocationRequest request, String status) {
        JourneyLog log = new JourneyLog();
        log.setUserId(request.getUserId());
        log.setLat(request.getLat());
        log.setLon(request.getLon());
        log.setStatus(status);
        journeyRepository.save(log);
    }

    private void saveToSafetyAlerts(LocationRequest request, String status) {
        SafetyAlert alert = new SafetyAlert();
        alert.setUserId(request.getUserId());
        alert.setLat(request.getLat());
        alert.setLon(request.getLon());
        alert.setStatus(status);
        SafetyAlert savedAlert = safetyAlertRepository.save(alert);

        // Start the background timer thread
        new Thread(() -> {
            try {
                System.out.println(">>> TIMER STARTED: Waiting 10 seconds for user confirmation...");
                Thread.sleep(10000);

                SafetyAlert updatedAlert = safetyAlertRepository.findById(savedAlert.getId()).orElse(null);
                if (updatedAlert != null && !updatedAlert.isUserConfirmedSafe()) {
                    triggerEmergencyAlert(request.getUserId(), request.getLat(), request.getLon());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void triggerEmergencyAlert(String userId, double lat, double lon) {
        String mapUrl = "https://www.google.com/maps?q=" + lat + "," + lon;
        String alertMessage = "ALERT: User " + userId + " has deviated from the path. Live Location: " + mapUrl;
        emergencyContactService.notify(userId, alertMessage);
    }

    public boolean markUserAsSafe(String userId) {
        List<SafetyAlert> alerts = safetyAlertRepository.findAll();
        for (SafetyAlert alert : alerts) {
            if (alert.getUserId().equals(userId) && !alert.isUserConfirmedSafe()) {
                alert.setUserConfirmedSafe(true);
                safetyAlertRepository.save(alert);
                System.out.println(">>> SUCCESS: User " + userId + " confirmed safe. Database updated.");
                return true;
            }
        }
        return false;
    }
}