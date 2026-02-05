package com.nextstop.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

// This annotation creates the "folder" (collection) named 'safety_alerts'
@Data
@Document(collection = "safety_alerts")
public class SafetyLog {
    @Id
    private String id;
    private String userId;
    private String status; // e.g., "anomaly"
    private double lat;
    private double lon;
    private LocalDateTime timestamp = LocalDateTime.now();
    private boolean resolved = false; // To track if the user clicked "I am safe"
}