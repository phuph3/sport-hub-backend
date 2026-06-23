package com.badminton.platform.service;

import com.badminton.platform.dto.ParticipantDTO;
import com.badminton.platform.repository.EventParticipantRepository;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventParticipantService {

    private final EventParticipantRepository repository;

    public EventParticipantService(EventParticipantRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getParticipants(Long eventId) {

        List<ParticipantDTO> joined =
                repository.findParticipantsByEventAndStatus(eventId, "JOINED");

        List<ParticipantDTO> waiting =
                repository.findParticipantsByEventAndStatus(eventId, "WAITING");

        Map<String, Object> result = new HashMap<>();
        result.put("joined", joined);
        result.put("waiting", waiting);

        return result;
    }
}
