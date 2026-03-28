package com.college.eventmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String email;
    private String name;
    private String branch;
    private Integer sem;
    private Integer year;
}
