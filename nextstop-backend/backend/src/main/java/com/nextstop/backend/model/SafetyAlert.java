package com.nextstop.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "safety_alerts") // This is your separate "folder"
public class SafetyAlert {
    @Id
    private String id;
    private String userId;
    private double lat;
    private double lon;
    private String status;
    private LocalDateTime timestamp = LocalDateTime.now();
    private boolean userConfirmedSafe = false;
}