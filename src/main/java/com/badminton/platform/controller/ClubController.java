package com.badminton.platform.controller;

import com.badminton.platform.entity.Club;
import com.badminton.platform.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép Frontend (React) gọi API
public class ClubController {

    @Autowired
    private ClubRepository clubRepository;

    @GetMapping
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    @PostMapping
    public Club createClub(@RequestBody Club club) {
        return clubRepository.save(club);
    }
}