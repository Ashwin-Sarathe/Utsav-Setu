package com.college.eventmanagement.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Document(collection = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    private String id;

    private String title;

    private String description;

    private LocalDate eventDate;

    private LocalTime eventTime;

    private String venue;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private String createdBy;

    private LocalDateTime createdAt;

    private EventStatus status;
}
