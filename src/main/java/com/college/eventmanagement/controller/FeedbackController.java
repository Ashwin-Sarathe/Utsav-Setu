package com.college.eventmanagement.controller;
import com.college.eventmanagement.model.Feedback;
import com.college.eventmanagement.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")

public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody Feedback feedback) {
        if (feedback.getMessage() == null || feedback.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }
        feedbackRepository.save(feedback);
        return ResponseEntity.ok().body("{\"message\": \"Feedback received!\"}");
    }

}
