package com.nextstop.backend.repository;

import com.nextstop.backend.model.JourneyLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JourneyRepository extends MongoRepository<JourneyLog, String> {
}