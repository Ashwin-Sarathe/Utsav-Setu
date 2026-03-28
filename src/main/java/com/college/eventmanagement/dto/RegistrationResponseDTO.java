package com.college.eventmanagement.dto;

import com.college.eventmanagement.model.EventStatus;
import com.college.eventmanagement.model.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponseDTO {

    private String id;
    private String name;
    private String username;
    private String userId;
    private String eventId;
    private LocalDateTime registeredAt;
    private RegistrationStatus status;
    private String eventTitle;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String venue;
    private EventStatus eventStatus;
}
