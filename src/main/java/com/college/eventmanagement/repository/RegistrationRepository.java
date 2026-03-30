package com.college.eventmanagement.repository;

import com.college.eventmanagement.model.Registration;
import com.college.eventmanagement.model.RegistrationStatus;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends MongoRepository<Registration, String> {

    Optional<Registration> findByUserIdAndEventId(String userId, String eventId);

    List<Registration> findByUserId(String userId);

    List<Registration> findByEventId(String eventId);

    long countByStatus(RegistrationStatus status);

    // Total registrations for a list of events
    long countByEventIdIn(List<String> eventIds);

    // Total UNIQUE users for a list of events (MongoDB Aggregation)
    @Aggregation(pipeline = {
            "{ '$match': { 'eventId' : { '$in' : ?0 } } }",
            "{ '$group': { '_id': '$userId' } }",
            "{ '$count': 'totalUsers' }"
    })
    Integer countDistinctUsersByEventIds(List<String> eventIds);

    Long countByEventIdInAndStatus(List<String> eventIds, RegistrationStatus status);

}
