//dto is data transfer object
//used when

package com.college.eventmanagement.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "Username cannot be empty")
    private String username; //roll number

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one uppercase letter and one special character"
    )
    private String password;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Branch cannot be empty")
    private String branch;

    @NotNull(message = "Semester must not be empty")
    private Integer sem;

    @NotNull(message = "Year must not be empty")
    private Integer year;
}
