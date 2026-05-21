package com.college.eventmanagement.repository;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {

    // Prevent duplicate active events
    Optional<Event> findByTitleAndEventDateAndEventTimeAndVenueAndIsDeletedFalse(
            String title, LocalDate date, LocalTime time, String venue);

    // Fetch active events for pagination
    Page<Event> findByIsDeletedFalse(Pageable pageable);

    // Fetch a single active event
    Optional<Event> findByIdAndIsDeletedFalse(String id);

    // Fetch all active events (Useful for schedulers/super admin)
    List<Event> findByIsDeletedFalse();


    // For Club Admin: Get just the IDs of events they created
    @Query(value = "{ 'createdBy' : ?0 }", fields = "{ '_id' : 1 }")
    List<String> findEventIdsByCreatedBy(String adminId);

    // For Club Admin: Get IDs of their LIVE events
    @Query(value = "{ 'createdBy' : ?0, 'status' : 'LIVE' }", fields = "{ '_id' : 1 }")
    List<String> findLiveEventIdsByCreatedBy(String adminId);

    // For the Analytics Table
    List<Event> findByCreatedByAndIsDeletedFalse(String adminId);
}
