package com.nextstop.backend.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private String userId;
    private double lat;
    private double lon;
    private double prevLat;
    private double prevLon;
}