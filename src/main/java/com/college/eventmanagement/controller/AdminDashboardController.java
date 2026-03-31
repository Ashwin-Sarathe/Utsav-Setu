package com.college.eventmanagement.controller;


import com.college.eventmanagement.dto.DashboardResponseDTO;
import com.college.eventmanagement.dto.EventStatsDTO;
import com.college.eventmanagement.exception.ResourceNotFoundException;
import com.college.eventmanagement.model.Role;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.UserRepository;
import com.college.eventmanagement.service.DashboardService;
import com.college.eventmanagement.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@Tag(name="Admin Dashboard APIs")
public class AdminDashboardController {

    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExportService exportService;

    @GetMapping("/event-stats")
    @Operation(summary = "Get All Events Stats")
    public ResponseEntity<List<EventStatsDTO>> getEventStats(Authentication authentication){

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        String adminId = user.getId();
        return new ResponseEntity<>(dashboardService.getEventStats(adminId, username), HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponseDTO> getStats(Authentication authentication) {
        // Extract info from your security context (e.g., JWT)
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        String adminId = user.getId();

        DashboardResponseDTO stats = dashboardService.getDashboardStats(username, adminId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/events/{eventId}/export-registrations")
    public void exportRegistrationsToCSV(@PathVariable String eventId, HttpServletResponse response) throws IOException {
        // 1. Set the content type and headers so the browser knows to download a file
        response.setContentType("text/csv");
        String filename = "registrations_event_" + eventId + ".csv";
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        // 2. Delegate the actual data writing to the service layer
        exportService.writeRegistrationsCsv(eventId, response.getWriter());
    }
}
