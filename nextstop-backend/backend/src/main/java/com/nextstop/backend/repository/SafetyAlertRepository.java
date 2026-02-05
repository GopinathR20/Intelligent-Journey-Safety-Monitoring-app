package com.nextstop.backend.repository;

import com.nextstop.backend.model.SafetyAlert;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SafetyAlertRepository extends MongoRepository<SafetyAlert, String> {
}