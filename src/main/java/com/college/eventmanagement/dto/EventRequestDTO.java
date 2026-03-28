package com.college.eventmanagement.dto;


import com.college.eventmanagement.model.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private LocalDate eventDate;

    @NotNull
    private LocalTime eventTime;

    @NotBlank
    private String venue;

    @NotNull @Positive
    private Integer maxParticipants;

    @NotNull
    private EventStatus status;
}
