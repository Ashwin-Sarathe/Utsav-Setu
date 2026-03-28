package com.college.eventmanagement.controller;

import com.college.eventmanagement.dto.UserProfileDTO;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.UserRepository;
import com.college.eventmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user")
@Tag(name="User APIs")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PutMapping("/profile/update")
    @Operation(summary = "User Profile Update")
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileDTO updateDTO, Principal principal) {
        try {
            // principal.getName() gets the username of the currently logged-in user from the JWT!
            userService.updateUserProfile(principal.getName(), updateDTO);
            return ResponseEntity.ok().body("{\"message\": \"Profile updated successfully!\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        try {
            // Find the user in the database using the username from the token
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Pack their details into the DTO to send to React
            UserProfileDTO profileDTO = new UserProfileDTO();
            profileDTO.setEmail(user.getEmail());
            profileDTO.setName(user.getName());
            profileDTO.setBranch(user.getBranch());
            profileDTO.setSem(user.getSem());
            profileDTO.setYear(user.getYear());

            return ResponseEntity.ok(profileDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }
}
