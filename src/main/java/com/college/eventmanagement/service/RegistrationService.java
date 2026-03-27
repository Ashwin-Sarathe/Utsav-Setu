package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.RegistrationRequestDTO;
import com.college.eventmanagement.dto.RegistrationResponseDTO;
import com.college.eventmanagement.exception.ConflictException;
import com.college.eventmanagement.exception.ResourceNotFoundException;
import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.Registration;
import com.college.eventmanagement.model.RegistrationStatus;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.RegistrationRepository;
import com.college.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public RegistrationResponseDTO registerForEvent(RegistrationRequestDTO registrationRequestDTO){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User Not found"));
        String userId = user.getId();
        String eventId = registrationRequestDTO.getEventId();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event Not found"));

        Optional<Registration> existingRegistration = registrationRepository.findByUserIdAndEventId(userId,eventId);

        //check if user already registered
        if(existingRegistration.isPresent()){
            RegistrationStatus status = existingRegistration.get().getStatus();
            if(status == RegistrationStatus.REGISTERED)
                throw new ConflictException("User Already Registered for this event!!");
        }

        //capacity check
        Query query = new Query();
        query.addCriteria(
                Criteria.where("_id").is(event.getId())
                        .and("currentParticipants").lt(event.getMaxParticipants())
        );

        Update update = new Update().inc("currentParticipants", 1);

        Event updatedEvent = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Event.class
        );

        if (updatedEvent == null) {
            throw new ConflictException("Event is full");
        }

        Registration savedRegistration = new Registration();
        savedRegistration.setRegisteredAt(LocalDateTime.now());
        savedRegistration.setUserId(userId);
        savedRegistration.setEventId(eventId);
        savedRegistration.setStatus(RegistrationStatus.REGISTERED);

        Registration saved = registrationRepository.save(savedRegistration);

        return mapToRegResponse(saved);
    }
    private RegistrationResponseDTO mapToRegResponse(Registration savedRegistration){
        RegistrationResponseDTO registrationResponseDTO=new RegistrationResponseDTO();

        registrationResponseDTO.setUserId(savedRegistration.getUserId());
        registrationResponseDTO.setEventId(savedRegistration.getEventId());
        registrationResponseDTO.setId(savedRegistration.getId());
        registrationResponseDTO.setStatus(savedRegistration.getStatus());
        registrationResponseDTO.setRegisteredAt(savedRegistration.getRegisteredAt());

        userRepository.findById(savedRegistration.getUserId()).ifPresent(user -> {
            registrationResponseDTO.setName(user.getName());
            registrationResponseDTO.setUsername(user.getUsername());
        });

        // Enrich the DTO with Event Details
        eventRepository.findById(savedRegistration.getEventId()).ifPresent(event -> {
            registrationResponseDTO.setEventTitle(event.getTitle());
            registrationResponseDTO.setEventDate(event.getEventDate());
            registrationResponseDTO.setEventTime(event.getEventTime());
            registrationResponseDTO.setVenue(event.getVenue());
        });

        return registrationResponseDTO;
    }

    public RegistrationResponseDTO cancelRegistration(String regId){
        Registration registration = registrationRepository.findById(regId).orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        if(registration.getStatus() == RegistrationStatus.CANCELLED)
            throw new ConflictException("Registration already cancelled");

        registration.setStatus(RegistrationStatus.CANCELLED);
        Query query = new Query();
        query.addCriteria(
                Criteria.where("_id").is(registration.getEventId())
        );

        Update update = new Update().inc("currentParticipants", -1);

        mongoTemplate.updateFirst(query, update, Event.class);

        Registration saved = registrationRepository.save(registration);
        return mapToRegResponse(saved);
    }

    public List<RegistrationResponseDTO> findRegistrationByUserId(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String userId = user.getId();
        List<Registration> registrations = registrationRepository.findByUserId(userId);
        List<RegistrationResponseDTO> registrationResponseDTOS = new ArrayList<>();
        for (Registration r : registrations){
            registrationResponseDTOS.add(mapToRegResponse(r));
        }
        return registrationResponseDTOS;
    }

    public List<RegistrationResponseDTO> findRegistrationByEventId(String eventId){
        eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        List<Registration> registrations = registrationRepository.findByEventId(eventId);
        List<RegistrationResponseDTO> registrationResponseDTOS = new ArrayList<>();
        for (Registration r : registrations){
            registrationResponseDTOS.add(mapToRegResponse(r));

        }
        return registrationResponseDTOS;
    }
}
