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

import java.util.ArrayList;
import java.util.List;

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
    public List<EventStatsDTO> getEventStats(){
        List<Event> events = eventRepository.findAll();
        List<EventStatsDTO> eventStatsDTOS=new ArrayList<>();
        for(Event e : events){
            EventStatsDTO eventStats=new EventStatsDTO();
            eventStats.setTitle(e.getTitle());
            eventStats.setEventId(e.getId());
            eventStats.setMaxParticipants(e.getMaxParticipants());
            int current = e.getCurrentParticipants() == null ? 0 : e.getCurrentParticipants();
            eventStats.setCurrentParticipants(current);
            eventStats.setRemainingSeats(e.getMaxParticipants() - current);

            eventStatsDTOS.add(eventStats);
        }
        return eventStatsDTOS;
    }
}
