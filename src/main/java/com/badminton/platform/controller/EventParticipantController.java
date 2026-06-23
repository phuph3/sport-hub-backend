package com.badminton.platform.controller;

import com.badminton.platform.entity.EventParticipant;
import com.badminton.platform.repository.EventParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/event-participants")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép Frontend (React) gọi API
public class EventParticipantController {

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @GetMapping
    public List<EventParticipant> getAllEventParticipants() {
        return eventParticipantRepository.findAll();
    }

    @PostMapping
    public EventParticipant createEventParticipant(@RequestBody EventParticipant eventParticipant) {
        return eventParticipantRepository.save(eventParticipant);
    }
}