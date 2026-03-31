package com.college.eventmanagement.service;

import com.college.eventmanagement.model.Registration;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.RegistrationRepository;
import com.college.eventmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ExportService {

    private final RegistrationRepository registrationRepository;
    // 1. Inject the UserRepository to fetch user details
    private final UserRepository userRepository;

    public ExportService(RegistrationRepository registrationRepository, UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    public void writeRegistrationsCsv(String eventId, PrintWriter writer) {
        List<Registration> registrations = registrationRepository.findByEventId(eventId);

        // Define how you want the LocalDateTime to look in the CSV
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        writer.println("Registration ID,User Name,Email,Status,Registration Date");

        for (Registration reg : registrations) {
            // 2. Fetch the user details using the userId stored in the registration
            // Adjust "getUserId()" to whatever the getter is actually named in your Registration model
            Optional<User> userOpt = userRepository.findById(reg.getUserId());

            String userName = "Unknown User";
            String userEmail = "N/A";

            if (userOpt.isPresent()) {
                userName = escapeSpecialCharacters(userOpt.get().getName()); // or getUsername()
                userEmail = userOpt.get().getEmail();
            }

            // 3. Safely format the LocalDateTime
            String formattedDate = "N/A";
            if (reg.getRegisteredAt() != null) {
                formattedDate = reg.getRegisteredAt().format(formatter);
            }

            writer.println(String.format("%s,%s,%s,%s,%s",
                    reg.getId(),
                    userName,
                    userEmail,
                    reg.getStatus(),
                    formattedDate
            ));
        }
    }

    private String escapeSpecialCharacters(String data) {
        if (data == null) return "";
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
