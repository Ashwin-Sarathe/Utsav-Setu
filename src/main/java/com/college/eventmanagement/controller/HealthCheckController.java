package com.college.eventmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
public class HealthCheckController {

    @GetMapping("/public/health")
    public Map<String, Object> healthCheck() {
        return Map.of(
                "status", "UP",
                "message", "Utsav Setu Backend is running smoothly! 🚀",
                "timestamp", LocalDateTime.now()
        );
    }
}