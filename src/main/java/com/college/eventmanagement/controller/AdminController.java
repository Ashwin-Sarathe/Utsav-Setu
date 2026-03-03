package com.college.eventmanagement.controller;

import com.college.eventmanagement.dto.UserResponseDTO;
import com.college.eventmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name="Admin APIs")
public class AdminController {

    @Autowired
    private UserService userService;
    @PutMapping("/users/{id}/promote")
    public ResponseEntity<UserResponseDTO> promoteToAdmin(@PathVariable String id){
        return new ResponseEntity<>(userService.promoteToAdmin(id), HttpStatus.OK);
    }

    @GetMapping("/users")
    @Operation(summary = "Get All Users")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return new ResponseEntity<>(userService.getAllUsers(page,size),HttpStatus.OK);
    }

    @PutMapping("/users/{id}/demote")
    public ResponseEntity<UserResponseDTO> demoteToUser(@PathVariable String id){
        return new ResponseEntity<>(userService.demoteToUser(id),HttpStatus.OK);
    }
}
