package com.college.eventmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    private String id;
    private String message;
    private String submittedBy;
    private LocalDateTime createdAt = LocalDateTime.now();

}
