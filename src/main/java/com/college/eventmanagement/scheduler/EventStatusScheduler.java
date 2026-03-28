package com.college.eventmanagement.scheduler;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.EventStatus;
import com.college.eventmanagement.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EventStatusScheduler {

    @Autowired
    private EventRepository eventRepository;

    // This cron expression means: Run at 00:00:00 (Midnight) every single day
    @Scheduled(cron = "0 0 0 * * *")
    public void autoUpdatePastEvents() {
        System.out.println("Running midnight check for expired events...");

        List<Event> allEvents = eventRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Event event : allEvents) {
            if (event.getEventDate() != null) {
                try {
                    // Convert the String "2026-03-25" into a Java Date object
                    LocalDate eventDate = event.getEventDate();

                    // If the event date is before today, AND it's not already marked as PAST
                    if (eventDate.isBefore(today) && event.getStatus() != EventStatus.PAST) {
                        event.setStatus(EventStatus.PAST);
                        eventRepository.save(event);
                        System.out.println("Automatically moved event to PAST: " + event.getTitle());
                    }
                } catch (Exception e) {
                    System.out.println("Skipping event with invalid date format: " + event.getTitle());
                }
            }
        }
    }
}
