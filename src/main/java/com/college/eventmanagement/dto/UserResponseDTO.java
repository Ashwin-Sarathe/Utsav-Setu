package com.college.eventmanagement.dto;

import com.college.eventmanagement.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String id;
    private String username;
    private String email;
    private Role role;
    private String name;
    private String branch;
    private Integer sem;
}
