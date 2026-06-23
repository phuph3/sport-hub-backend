package com.badminton.platform.controller;

import com.badminton.platform.entity.Venue;
import com.badminton.platform.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép Frontend (React) gọi API
public class VenueController {

    @Autowired
    private VenueRepository venueRepository;

    @GetMapping
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    @PostMapping
    public Venue createVenue(@RequestBody Venue venue) {
        return venueRepository.save(venue);
    }
}