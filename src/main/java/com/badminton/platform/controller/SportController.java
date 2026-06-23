package com.badminton.platform.controller;

import com.badminton.platform.entity.Sport;
import com.badminton.platform.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép Frontend (React) gọi API
public class SportController {

    @Autowired
    private SportRepository sportRepository;

    @GetMapping
    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }

    @PostMapping
    public Sport createSport(@RequestBody Sport sport) {
        return sportRepository.save(sport);
    }
}