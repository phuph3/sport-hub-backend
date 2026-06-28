package com.badminton.platform.controller;

import com.badminton.platform.dto.EventResponseDTO;
import com.badminton.platform.entity.Event;
import com.badminton.platform.entity.FavoriteEvent;
import com.badminton.platform.repository.EventParticipantRepository;
import com.badminton.platform.repository.EventRepository;
import com.badminton.platform.repository.FavoriteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.badminton.platform.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;

    @Autowired
    private JwtService jwtService;

    public FavoriteController(
            FavoriteRepository favoriteRepository,
            EventRepository eventRepository,
            EventParticipantRepository eventParticipantRepository) {
        this.favoriteRepository = favoriteRepository;
        this.eventRepository = eventRepository;
        this.eventParticipantRepository = eventParticipantRepository;
    }

    // GET FAVORITES

    @GetMapping("/favorites")
    public List<EventResponseDTO> getFavorites(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromHeader(authHeader);

        if (userId == null)
            return List.of();

        List<FavoriteEvent> list = favoriteRepository.findByUserId(userId);

        List<Long> ids = list.stream()
                .map(FavoriteEvent::getEventId)
                .collect(Collectors.toList());

        // List<Event> events = eventRepository.findAllById(ids);
        List<Event> events = eventRepository.findAllWithSportByIdIn(ids);

        for (Event e : events) {
            long count = eventParticipantRepository.countByEventIdAndStatus(
                    e.getId(),
                    "JOINED");
            e.setCurrentPlayers(count);
        }

        return events.stream()
                .map((Event e) -> {
                    EventResponseDTO dto = new EventResponseDTO();

                    dto.setId(e.getId());
                    dto.setTitle(e.getTitle());
                    dto.setNote(e.getNote());
                    dto.setGoogleMapLink(e.getGoogleMapLink());
                    dto.setLocationName(e.getLocationName());

                    dto.setLat(e.getLocationLat());
                    dto.setLng(e.getLocationLng());

                    dto.setStartTime(e.getStartTime().toString());
                    dto.setEndTime(e.getEndTime().toString());

                    dto.setMaxPlayers(e.getMaxPlayers());
                    dto.setCurrentPlayers(e.getCurrentPlayers());
                    dto.setPrefectureCode(e.getPrefectureCode());
                    dto.setCityCode(e.getCityCode());

                    dto.setSportId(e.getSport().getId());
                    dto.setSport(e.getSport());

                    dto.setLevelFrom(e.getLevelFrom());
                    dto.setLevelTo(e.getLevelTo());
                    dto.setStatus(e.getStatus().name());

                    return dto;
                })
                .toList();

    }

    // ADD FAVORITE

    @PostMapping("/favorites/{eventId}")
    public ResponseEntity<?> addFavorite(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        if (favoriteRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
            return ResponseEntity.ok("Already added");
        }

        FavoriteEvent f = new FavoriteEvent();
        f.setUserId(userId);
        f.setEventId(eventId);

        favoriteRepository.save(f);

        Map<String, Object> res = new HashMap<>();
        res.put("status", "ADDED");

        return ResponseEntity.ok(res);

    }

    // REMOVE FAVORITE
    @DeleteMapping("/favorites/{eventId}")
    public ResponseEntity<?> removeFavorite(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        System.out.println("DELETE request eventId = " + eventId);

        var existing = favoriteRepository.findByUserIdAndEventId(userId, eventId);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return ResponseEntity.ok("Removed");
        }

        Map<String, Object> res = new HashMap<>();
        res.put("status", "REMOVED");

        return ResponseEntity.ok(res);

    }

    @GetMapping("/favorites/check/{eventId}")
    public boolean isFavorite(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        if (userId == null)
            return false;

        return favoriteRepository
                .findByUserIdAndEventId(userId, eventId)
                .isPresent();
    }

    private Long getUserIdFromHeader(String authHeader) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }

            String token = authHeader.replace("Bearer ", "");

            return jwtService.extractUserId(token);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}