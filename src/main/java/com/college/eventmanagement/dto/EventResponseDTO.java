package com.college.eventmanagement.dto;

import com.college.eventmanagement.model.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {

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
