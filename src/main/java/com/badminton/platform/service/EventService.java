
package com.badminton.platform.service;

import com.badminton.platform.dto.EventRequestDTO;
import com.badminton.platform.entity.Event;
import com.badminton.platform.entity.EventParticipant;
import com.badminton.platform.entity.EventStatus;
import com.badminton.platform.entity.FavoriteEvent;
import com.badminton.platform.entity.Sport;
import com.badminton.platform.repository.EventParticipantRepository;
import com.badminton.platform.repository.EventRepository;
import com.badminton.platform.repository.FavoriteRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.badminton.platform.dto.MapEventDto;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository participantRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    public Event updateEvent(Long id, EventRequestDTO request, Long userId) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // CHẶN KHÔNG PHẢI HOST
        if (!event.getHostId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }

        // block event đã hết hạn
        if (event.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot edit ended event");
        }

        // TEXT
        event.setTitle(request.getTitle());
        event.setNote(request.getNote());

        // LOCATION
        event.setGoogleMapLink(request.getGoogleMapLink());
        event.setLocationName(request.getLocationName());

        event.setLocationLat(request.getLat());
        event.setLocationLng(request.getLng());

        // TIME
        event.setStartTime(LocalDateTime.parse(request.getStartTime()));
        event.setEndTime(LocalDateTime.parse(request.getEndTime()));

        // NUMBER
        event.setMaxPlayers(request.getMaxPlayers());

        // ADDRESS
        event.setPrefectureCode(request.getPrefectureCode());
        event.setCityCode(request.getCityCode());
        event.setFullAddress(request.getFullAddress());

        // SPORT
        Sport sport = new Sport();
        sport.setId(request.getSportId());
        event.setSport(sport);

        // LEVEL
        event.setLevelFrom(request.getLevelFrom());
        event.setLevelTo(request.getLevelTo());

        return eventRepository.save(event);
    }

    public Event createEvent(EventRequestDTO request, Long userId) {

        Event event = new Event();
        event.setHostId(userId);

        // TEXT
        event.setTitle(request.getTitle());
        event.setNote(request.getNote());

        // LOCATION
        event.setGoogleMapLink(request.getGoogleMapLink());
        event.setLocationName(request.getLocationName());
        event.setLocationLat(request.getLat());
        event.setLocationLng(request.getLng());

        // TIME
        event.setStartTime(LocalDateTime.parse(request.getStartTime()));
        event.setEndTime(LocalDateTime.parse(request.getEndTime()));

        // NUMBER
        event.setMaxPlayers(request.getMaxPlayers());

        // ADDRESS
        event.setPrefectureCode(request.getPrefectureCode());
        event.setCityCode(request.getCityCode());
        event.setFullAddress(request.getFullAddress());

        // LEVEL
        event.setLevelFrom(request.getLevelFrom());
        event.setLevelTo(request.getLevelTo());

        // SPORT
        if (request.getSportId() != null) {
            Sport s = new Sport();
            s.setId(request.getSportId());
            event.setSport(s);
        }

        return eventRepository.save(event);
    }

    public void cancelEvent(Long eventId, Long userId) {

        // 1. check event tồn tại
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 2. check host
        if (!event.getHostId().equals(userId)) {
            throw new RuntimeException("Not allowed");
        }

        // 3. lấy participants (JOINED + WAITING)
        List<EventParticipant> participants = participantRepository.findByEventId(eventId);

        // 4. send notification
        for (EventParticipant p : participants) {

            // không gửi cho chính host
            if (p.getUserId().equals(userId))
                continue;

            notificationService.createNotification(
                    p.getUserId(), // người nhận
                    eventId,
                    "EVENT_CANCELLED",
                    null, // actorId (optional)
                    event.getTitle() // để build message
            );
        }

        // 5. update event status (soft delete)
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    public List<MapEventDto> getEventsForMap(
            Double neLat,
            Double neLng,
            Double swLat,
            Double swLng) {

        LocalDateTime now = LocalDateTime.now();

        List<Event> events = eventRepository.findInBounds(
                neLat, neLng, swLat, swLng);

        return events.stream()

                //  chỉ lọc hết hạn
                .filter(ev -> ev.getEndTime() != null && ev.getEndTime().isAfter(now))

                //  bỏ canceled
                .filter(ev -> ev.getStatus() != EventStatus.CANCELLED)

                .map(ev -> {
                    MapEventDto dto = new MapEventDto();

                    dto.setId(ev.getId());
                    dto.setTitle(ev.getTitle());
                    dto.setLocationName(ev.getLocationName());

                    dto.setLat(ev.getLocationLat());
                    dto.setLng(ev.getLocationLng());

                    dto.setCurrentPlayers(
                            ev.getCurrentPlayers() != null ? ev.getCurrentPlayers() : 0);

                    dto.setMaxPlayers(ev.getMaxPlayers());

                    dto.setStartTime(
                            ev.getStartTime() != null ? ev.getStartTime().toString() : null);

                    dto.setEndTime(
                            ev.getEndTime() != null ? ev.getEndTime().toString() : null);

                    dto.setSportName(
                            ev.getSport() != null ? ev.getSport().getName() : null);

                    return dto;
                })

                .toList();
    }
}
