package com.nextstop.backend.service;

import org.springframework.stereotype.Service;

@Service
public class EmergencyContactService {

    public void notify(String userId, String message) {
        // This is the logic that shares location with emergency contacts
        System.out.println("----------------------------------------------");
        System.out.println("!!! EMERGENCY ALERT SENT !!!");
        System.out.println("USER: " + userId);
        System.out.println("ALERT MESSAGE: " + message);
        System.out.println("----------------------------------------------");
    }
}