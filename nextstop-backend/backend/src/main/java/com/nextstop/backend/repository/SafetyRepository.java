package com.nextstop.backend.repository;

import com.nextstop.backend.model.SafetyLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SafetyRepository extends MongoRepository<SafetyLog, String> {
    // You can add custom searches here later, like finding logs by userId
}