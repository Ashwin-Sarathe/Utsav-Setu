package com.college.eventmanagement.dto;


import com.college.eventmanagement.model.EventStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull @Future
    private LocalDate eventDate;

    @NotNull
    private LocalTime eventTime;

    @NotBlank
    private String venue;

    @NotNull @Positive @Min(value=1)
    private Integer maxParticipants;

    @NotNull
    private EventStatus status;

    private List<String> imageUrls;
}
