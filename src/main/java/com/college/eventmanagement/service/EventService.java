package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.EventRequestDTO;
import com.college.eventmanagement.dto.EventResponseDTO;
import com.college.eventmanagement.dto.UpdateEventRequestDTO;
import com.college.eventmanagement.exception.ConflictException;
import com.college.eventmanagement.exception.ResourceNotFoundException;
import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public EventResponseDTO createEvent(EventRequestDTO eventRequest){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User admin = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<Event> optionalEvent= eventRepository.findByTitleAndEventDateAndEventTimeAndVenue
                (
                        eventRequest.getTitle(),
                        eventRequest.getEventDate(),
                        eventRequest.getEventTime(),
                        eventRequest.getVenue()
                );
        if(optionalEvent.isPresent()) throw new ConflictException("Event Already Exists!!");
        else {
            Event newEvent = new Event();
            newEvent.setTitle(eventRequest.getTitle());
            newEvent.setDescription(eventRequest.getDescription());
            newEvent.setEventDate(eventRequest.getEventDate());
            newEvent.setEventTime(eventRequest.getEventTime());
            newEvent.setVenue(eventRequest.getVenue());
            newEvent.setMaxParticipants(eventRequest.getMaxParticipants());
            newEvent.setCreatedBy(admin.getId());
            newEvent.setCurrentParticipants(0);
            newEvent.setCreatedAt(LocalDateTime.now());

            Event savedEvent = eventRepository.save(newEvent);

            return mapToResponse(savedEvent);
        }
    }


    private EventResponseDTO mapToResponse(Event savedEvent) {
        EventResponseDTO eventResponse = new EventResponseDTO();
        eventResponse.setTitle(savedEvent.getTitle());
        eventResponse.setDescription(savedEvent.getDescription());
        eventResponse.setEventDate(savedEvent.getEventDate());
        eventResponse.setEventTime(savedEvent.getEventTime());
        eventResponse.setVenue(savedEvent.getVenue());
        eventResponse.setCurrentParticipants(savedEvent.getCurrentParticipants());
        eventResponse.setMaxParticipants(savedEvent.getMaxParticipants());
        eventResponse.setCreatedBy(savedEvent.getCreatedBy());
        eventResponse.setId(savedEvent.getId());
        eventResponse.setCreatedAt(savedEvent.getCreatedAt());
        return eventResponse;
    }

    public Page<EventResponseDTO> getAllEvents(Pageable pageable){
        Page<Event> eventsPage = eventRepository.findAll(pageable);
        return eventsPage.map(this::mapToResponse);
    }

    public EventResponseDTO getEventById(String id){
        Event e = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event Not Found!!"));
        return mapToResponse(e);
    }

    public EventResponseDTO deleteEventById(String id){
        Event e = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event Not Found!"));
        eventRepository.deleteById(id);
        return mapToResponse(e);
    }

    public EventResponseDTO updateEvent(String id, UpdateEventRequestDTO dto){
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        if(dto.getMaxParticipants() < event.getCurrentParticipants()){
            throw new ConflictException("Cannot reduce capacity below current registrations");
        }
        event.setTitle(dto.getTitle());
        event.setVenue(dto.getVenue());
        event.setEventTime(dto.getEventTime());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setMaxParticipants(dto.getMaxParticipants());

        Event updatedEvent = eventRepository.save(event);
        return mapToResponse(updatedEvent);
    }

    public Page<EventResponseDTO> searchEvents(
            String title,
            String venue,
            LocalDate date,
            int page,
            int size
    ){
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        if(title != null && !title.isEmpty()){
            criteriaList.add(
                    Criteria.where("title").regex(title,"i")
                    // "i" makes it case-insensitive
                    // regex -> partial case-insensitive matching
            );
        }
        if(venue != null && !venue.isEmpty()){
            criteriaList.add(
                    Criteria.where("venue").regex(venue,"i")
            );
        }
        if(date!=null){
            criteriaList.add(Criteria.where("date").is(date));
        }
        if(!criteriaList.isEmpty()){
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        Pageable pageable = PageRequest.of(page,size);
        query.with(pageable);
        List<Event> events = mongoTemplate.find(query, Event.class);
        long total = mongoTemplate.count(query.skip(0).limit(0), Event.class);

        Page<Event> eventPage = new PageImpl<>(events, pageable, total);
        return eventPage.map(this::mapToResponse);
    }
}
