package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.DashboardResponseDTO;
import com.college.eventmanagement.dto.EventStatsDTO;
import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.RegistrationStatus;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.RegistrationRepository;
import com.college.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    public DashboardResponseDTO getSummary(){
        DashboardResponseDTO dashboardResponseDTO = new DashboardResponseDTO();
        dashboardResponseDTO.setTotalUsers(userRepository.count());
        dashboardResponseDTO.setTotalEvents(eventRepository.count());
        dashboardResponseDTO.setTotalRegistrations(registrationRepository.count());
        dashboardResponseDTO.setTotalActiveRegistrations(registrationRepository.countByStatus(RegistrationStatus.REGISTERED));
        return dashboardResponseDTO;
    }
    public List<EventStatsDTO> getEventStats(String adminId, String username) {
        List<Event> events;

        // 1. Check the role and fetch the appropriate list of events
        // (Adjust "SUPER_ADMIN" to match whatever your actual Role enum/string is)
        if (username.equals("admin")) {
            events = eventRepository.findAll();
        } else {
            events = eventRepository.findByCreatedBy(adminId);
        }

        // 2. Map the fetched events to your DTO using your exact Stream logic
        return events.stream().map(e -> {
            EventStatsDTO stats = new EventStatsDTO();
            stats.setEventId(e.getId());
            stats.setTitle(e.getTitle());
            stats.setMaxParticipants(e.getMaxParticipants());

            int current = e.getCurrentParticipants() == null ? 0 : e.getCurrentParticipants();
            stats.setCurrentParticipants(current);
            stats.setRemainingSeats(e.getMaxParticipants() - current);

            return stats;
        }).collect(Collectors.toList());
    }

    public DashboardResponseDTO getDashboardStats(String currentUsername, String currentAdminId) {
        DashboardResponseDTO stats = new DashboardResponseDTO();

        if ("admin".equals(currentUsername)) {
            // --- SUPER ADMIN LOGIC ---
            stats.setTotalUsers(userRepository.count());
            stats.setTotalEvents(eventRepository.count());
            stats.setTotalRegistrations(registrationRepository.count());
            stats.setTotalActiveRegistrations(registrationRepository.countByStatus(RegistrationStatus.REGISTERED));


        } else {
            // --- CLUB ADMIN LOGIC ---

            // 1. Fetch the actual events (Foolproof way to avoid projection issues)
            // Make sure 'createdBy' matches the exact variable name in your Event model!
            List<Event> myEvents = eventRepository.findByCreatedBy(currentAdminId);

            if (myEvents.isEmpty()) {
                stats.setTotalEvents(0L);
                stats.setTotalRegistrations(0L);
                stats.setTotalUsers(0L);
                stats.setTotalActiveRegistrations(0L);
                return stats;
            }

            // Extract all Event IDs into a List of Strings
            List<String> myEventIds = myEvents.stream()
                    .map(Event::getId)
                    .toList();

            // 2. Calculate Stats
            stats.setTotalEvents((long) myEvents.size());
            stats.setTotalRegistrations(registrationRepository.countByEventIdIn(myEventIds));

            // Unique Users calculation
            Integer uniqueUsers = registrationRepository.countDistinctUsersByEventIds(myEventIds);
            stats.setTotalUsers((long) (uniqueUsers != null ? uniqueUsers : 0));

            // 3. Active Registrations (Now correctly filtering by RegistrationStatus!)
            // Note: You will need to add this method to your RegistrationRepository
            stats.setTotalActiveRegistrations(
                    registrationRepository.countByEventIdInAndStatus(myEventIds, RegistrationStatus.REGISTERED)
            );
        }

        return stats;
    }
}
