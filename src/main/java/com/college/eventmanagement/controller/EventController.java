package com.college.eventmanagement.controller;

import com.college.eventmanagement.dto.EventRequestDTO;
import com.college.eventmanagement.dto.EventResponseDTO;
import com.college.eventmanagement.dto.UpdateEventRequestDTO;
import com.college.eventmanagement.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/create-event")
    public ResponseEntity<EventResponseDTO> createNewEvent(@Valid @RequestBody EventRequestDTO eventRequest){
        EventResponseDTO eventResponse = eventService.createEvent(eventRequest);
        return new ResponseEntity<>(eventResponse, HttpStatus.CREATED);
    }

    @GetMapping("/get-all-events")
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(Pageable pageable){
        return new ResponseEntity<>(eventService.getAllEvents(pageable),HttpStatus.OK);
    }

    @GetMapping("/get-event-by-id/{Id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable String Id){
        return new ResponseEntity<>(eventService.getEventById(Id),HttpStatus.OK);
    }

    @DeleteMapping("/delete-event-by-id/{Id}")
    public ResponseEntity<EventResponseDTO> deleteEventById(@PathVariable String Id){
        return new ResponseEntity<>(eventService.deleteEventById(Id),HttpStatus.OK);
    }

    @PutMapping("/update-event-by-id/{Id}")
    public ResponseEntity<EventResponseDTO> updateEventById(
            @PathVariable String Id,
            @Valid @RequestBody UpdateEventRequestDTO updateRequestDTO){
        return new ResponseEntity<>(eventService.updateEvent(Id,updateRequestDTO),HttpStatus.OK);
    }

}
