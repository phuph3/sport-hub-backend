package com.badminton.platform.controller;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import com.badminton.platform.entity.Location;
import com.badminton.platform.config.EventWebSocketHandler;
import com.badminton.platform.dto.NotificationResponseDTO;
import com.badminton.platform.entity.Event;
import com.badminton.platform.entity.EventParticipant;

import com.badminton.platform.repository.EventRepository;
import com.badminton.platform.service.EventService;
import com.badminton.platform.service.JoinEventAsyncService;
import com.badminton.platform.service.JwtService;
import com.badminton.platform.dto.EventRequestDTO;
import com.badminton.platform.dto.EventResponseDTO;
import com.badminton.platform.dto.EventTodayDTO;
import com.badminton.platform.dto.MapEventDto;
import com.badminton.platform.repository.LocationRepository;
import com.badminton.platform.repository.EventParticipantRepository;
import com.badminton.platform.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.badminton.platform.service.EventParticipantService;
import com.badminton.platform.repository.UserRepository;
import com.badminton.platform.entity.User;

//import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.Page;
import com.badminton.platform.entity.Notification;
import com.badminton.platform.repository.NotificationRepository;

import java.util.Map;

import java.net.URLEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
// Đảm bảo bạn cũng có các import cũ
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/events")
// @CrossOrigin(origins = "http://localhost:3000") // Cho phép Frontend (React)
// gọi API
public class EventController {

    @Autowired
    private JoinEventAsyncService joinEventAsyncService;

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository participantRepo;

    @Autowired
    private LocationRepository locationRepo;

    // @Autowired
    // private NotificationRepository notificationRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EventParticipantService eventParticipantService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // @Autowired
    // private RestTemplate restTemplate;

    private final RestTemplate restTemplate;

    // @GetMapping
    // public List<Event> getAllEvents() {
    // return eventRepository.findAll();
    // }

    @PutMapping("/{id}")
    public Event updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }

        return eventService.updateEvent(id, request, userId);
    }

    @PostMapping
    public Event createEvent(
            @RequestBody EventRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }

        return eventService.createEvent(request, userId);
    }

    // @PostMapping
    // public Event createEvent(@RequestBody Map<String, Object> payload) {

    // try {

    // Long userId = 1L; // TODO: replace JWT

    // Event event = new Event();

    // // =====================
    // // BASIC
    // // =====================
    // event.setHostId(userId);
    // event.setStatus(EventStatus.OPEN);

    // // =====================
    // // TIME
    // // =====================
    // if (payload.get("startTime") != null) {
    // event.setStartTime(LocalDateTime.parse((String) payload.get("startTime")));
    // }

    // if (payload.get("endTime") != null) {
    // event.setEndTime(LocalDateTime.parse((String) payload.get("endTime")));
    // }

    // // =====================
    // // PLAYERS
    // // =====================
    // if (payload.get("maxPlayers") != null) {
    // event.setMaxPlayers((Integer) payload.get("maxPlayers"));
    // }

    // // =====================
    // // LEVEL
    // // =====================
    // event.setLevelFrom((String) payload.getOrDefault("levelFrom", ""));
    // event.setLevelTo((String) payload.getOrDefault("levelTo", ""));

    // // =====================
    // // LOCATION (HIỂN THỊ)
    // // =====================
    // event.setLocationName((String) payload.getOrDefault("locationName", ""));
    // event.setFullAddress((String) payload.getOrDefault("fullAddress", ""));
    // event.setGoogleMapLink((String) payload.getOrDefault("googleMapLink", ""));

    // // LAT / LNG
    // if (payload.get("lat") != null) {
    // event.setLocationLat(Double.valueOf(payload.get("lat").toString()));
    // }

    // if (payload.get("lng") != null) {
    // event.setLocationLng(Double.valueOf(payload.get("lng").toString()));
    // }

    // // =====================
    // // LOCATION CODE
    // // =====================

    // event.setCityCode((String) payload.get("cityCode"));
    // event.setPrefectureCode((String) payload.get("prefectureCode"));

    // // =====================
    // // SPORT
    // // =====================
    // if (payload.get("sportId") != null) {
    // Long sportId = Long.valueOf(payload.get("sportId").toString());

    // Sport sport = new Sport();
    // sport.setId(sportId);

    // event.setSport(sport);
    // }

    // // =====================
    // // MULTI LANGUAGE
    // // =====================
    // String lang = (String) payload.getOrDefault("lang", "ja");

    // Map<String, String> titleMap = new HashMap<>();
    // titleMap.put(lang, (String) payload.getOrDefault("title", ""));
    // event.setTitle(titleMap);

    // Map<String, String> noteMap = new HashMap<>();
    // noteMap.put(lang, (String) payload.getOrDefault("note", ""));
    // event.setNotesTranslations(noteMap);

    // // =====================
    // // SAVE
    // // =====================
    // Event saved = eventRepository.save(event);

    // return saved;

    // } catch (Exception e) {
    // e.printStackTrace();
    // throw e;
    // }
    // }
    private String resolveRedirect(String url) {
        try {
            URL currentUrl = new URL(url);

            for (int i = 0; i < 5; i++) {
                HttpURLConnection conn = (HttpURLConnection) currentUrl.openConnection();

                conn.setInstanceFollowRedirects(false);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.connect();

                String redirect = conn.getHeaderField("Location");

                if (redirect == null) {
                    break;
                }

                System.out.println("➡️ Redirect to: " + redirect);
                currentUrl = new URL(redirect);
            }

            return currentUrl.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }

    // Spring sẽ tự động "bơm" cái Bean RestTemplate vào đây
    public EventController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/geocode")
    public Object geocode(@RequestParam String q) {
        try {
            // Encode URL cẩn thận
            String encodedQuery = URLEncoder.encode(q, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q=" + encodedQuery
                    + "&format=json&accept-language=ja&limit=5";

            // Thiết lập Headers quan trọng
            HttpHeaders headers = new HttpHeaders();

            headers.set("User-Agent", "MyGeocodingApp/1.0 (contact@goshub.jp)");
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Sử dụng RestTemplate để gọi
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            System.out.println("👉 API Response: " + response.getBody());
            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/extract-latlng")
    public Map<String, Double> extractLatLng(@RequestParam String url) {
        try {
            String finalUrl = resolveRedirect(url);

            System.out.println("✅ Final URL: " + finalUrl);

            // =========================
            // 1. CASE: @lat,lng
            // =========================
            Pattern pattern1 = Pattern.compile("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)");
            Matcher matcher1 = pattern1.matcher(finalUrl);

            if (matcher1.find()) {
                return Map.of(
                        "lat", Double.parseDouble(matcher1.group(1)),
                        "lng", Double.parseDouble(matcher1.group(2)));
            }

            // =========================
            // 2. CASE: q=lat,lng
            // =========================
            Pattern pattern2 = Pattern.compile("[?&]q=(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)");
            Matcher matcher2 = pattern2.matcher(finalUrl);

            if (matcher2.find()) {
                return Map.of(
                        "lat", Double.parseDouble(matcher2.group(1)),
                        "lng", Double.parseDouble(matcher2.group(2)));
            }

            // =========================
            // 3. CASE: search fallback
            // =========================
            Pattern pattern3 = Pattern.compile("!3d(-?\\d+\\.\\d+)!4d(-?\\d+\\.\\d+)");
            Matcher matcher3 = pattern3.matcher(finalUrl);

            if (matcher3.find()) {
                return Map.of(
                        "lat", Double.parseDouble(matcher3.group(1)),
                        "lng", Double.parseDouble(matcher3.group(2)));
            }

            System.out.println("❌ Cannot extract lat/lng");
            return Map.of();

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of();
        }
    }

    @GetMapping("/home")
    public List<Event> getHomeEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getEventDetail(@PathVariable Long id) {

        Event e = eventRepository.findById(id).orElse(null);

        if (e == null) {
            return Map.of();
        }

        long count = participantRepo.countByEventIdAndStatus(
                id,
                "JOINED");

        e.setCurrentPlayers(count);

        User host = userRepository.findById(e.getHostId()).orElse(null);

        String hostNickname = null;

        if (host != null) {
            hostNickname = host.getNickname();
        }

        // tạo eventMap mới (KHÔNG dùng entity trực tiếp)
        Map<String, Object> eventMap = new HashMap<>();

        eventMap.put("id", e.getId());
        eventMap.put("hostId", e.getHostId());
        eventMap.put("hostNickname", hostNickname); // QUAN TRỌNG

        eventMap.put("title", e.getTitle());
        eventMap.put("note", e.getNote());
        eventMap.put("locationName", e.getLocationName());
        eventMap.put("fullAddress", e.getFullAddress());
        eventMap.put("googleMapLink", e.getGoogleMapLink());
        eventMap.put("startTime", e.getStartTime());
        eventMap.put("endTime", e.getEndTime());
        eventMap.put("levelFrom", e.getLevelFrom());
        eventMap.put("levelTo", e.getLevelTo());
        eventMap.put("maxPlayers", e.getMaxPlayers());
        eventMap.put("currentPlayers", e.getCurrentPlayers());
        eventMap.put("sport", e.getSport());
        eventMap.put("status", e.getStatus().name());

        Map<String, Object> res = new HashMap<>();
        res.put("event", eventMap);
        // res.put("event", e);
        System.out.println("EventController.getEventDetail: " + eventMap.toString());

        // dùng cityCode để map
        Location loc = resolveLocation(e.getCityCode());

        if (loc != null) {
            res.put("prefectureJa", loc.getPrefectureJa());
            res.put("prefectureEn", loc.getPrefectureEn());
            res.put("prefectureVi", loc.getPrefectureVi());

            res.put("cityJa", loc.getCityJa());
            res.put("cityEn", loc.getCityEn());
            res.put("cityVi", loc.getCityVi());
        }

        return res;
    }

    @Transactional
    @DeleteMapping("/{id}/leave")
    public Map<String, Object> leaveEvent(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        if (userId == null) {
            return Map.of(
                    "status", "ERROR",
                    "message", "Unauthorized");
        }

        EventParticipant ep = participantRepo.findByEventIdAndUserId(id, userId);

        if (ep == null) {
            return Map.of(
                    "status", "NOT_FOUND",
                    "message", "You are not in this event");
        }

        // 1. delete (atomic trong transaction)
        participantRepo.delete(ep);

        // 2. promote waiting
        List<EventParticipant> waitingList = participantRepo.findByEventIdAndStatusOrderByCreatedAtAsc(id, "WAITING");

        Long promotedUserId = null;

        if (!waitingList.isEmpty()) {
            EventParticipant next = waitingList.get(0);
            next.setStatus("JOINED");
            participantRepo.save(next);
            promotedUserId = next.getUserId();
        }

        // 3. trả response ngay (không block)
        Map<String, Object> response = new HashMap<>();
        response.put("status", "LEFT");
        response.put("eventId", id);
        response.put("userId", userId);
        if (promotedUserId != null) {
            response.put("promotedUserId", promotedUserId);
        }

        // 4. async (sau khi DB OK)
        joinEventAsyncService.handleAfterLeave(id, userId, promotedUserId);

        return response;
    }

    @Transactional
    @PostMapping("/{id}/join")
    public Map<String, Object> joinEvent(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        if (userId == null) {
            return Map.of(
                    "status", "ERROR",
                    "message", "Unauthorized");
        }

        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return Map.of(
                    "status", "NOT_FOUND",
                    "message", "Event not found");
        }

        long joinedCount = participantRepo.countByEventIdAndStatus(id, "JOINED");

        EventParticipant ep = new EventParticipant();
        ep.setEventId(id);
        ep.setUserId(userId);

        String status = joinedCount >= event.getMaxPlayers()
                ? "WAITING"
                : "JOINED";

        ep.setStatus(status);

        try {
            participantRepo.save(ep);
        } catch (DataIntegrityViolationException e) {
            return Map.of(
                    "status", "ALREADY",
                    "message", "You already joined this event");
        }

        Map<String, Object> response = Map.of(
                "status", status,
                "eventId", id,
                "userId", userId,
                "message",
                status.equals("JOINED")
                        ? "Joined successfully"
                        : "Added to waiting list");

        // async (không block)
        joinEventAsyncService.handleAfterJoin(event, userId, id, status);

        return response;
    }

    @GetMapping("/{id}/joined")
    public boolean isJoined(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        if (userId == null) {
            return false;
        }

        return participantRepo.existsByEventIdAndUserId(id, userId);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelEvent(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtService.getUserIdFromHeader(authHeader);

        eventService.cancelEvent(id, userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby")
    public List<Event> getNearbyEvents(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(defaultValue = "10") double radius) {
        List<Event> events = eventRepository.findAll();
        List<Event> result = new ArrayList<>();

        if (lat == null || lng == null) {
            return eventRepository.findAll(); // fallback
        }

        for (Event e : events) {

            // ADD THÊM - ĐẾM USER
            long count = participantRepo.countByEventIdAndStatus(
                    e.getId(),
                    "JOINED");

            e.setCurrentPlayers(count);

            if (e.getLocationLat() != null && e.getLocationLng() != null) {

                double distance = calculateDistance(
                        lat, lng,
                        e.getLocationLat(),
                        e.getLocationLng());

                if (distance <= radius) {
                    e.setDistance(distance);
                    result.add(e);
                }
            }
        }

        // sort gần → xa
        result.sort((a, b) -> Double.compare(a.getDistance(), b.getDistance()));

        return result;
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) *
                        Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @GetMapping("/my")
    public List<EventResponseDTO> getMyEvents(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        if (userId == null) {
            return new ArrayList<>();
        }

        // events bạn join
        List<EventParticipant> participants = participantRepo.findByUserId(userId);

        List<Long> joinedEventIds = participants.stream()
                .map(EventParticipant::getEventId)
                .toList();

        // List<Event> joinedEvents = eventRepository.findAllById(joinedEventIds);

        List<Event> joinedEvents = eventRepository.findAllWithSportByIdIn(joinedEventIds);

        // events bạn host
        // List<Event> hostedEvents = eventRepository.findByHostId(userId);

        List<Event> hostedEvents = eventRepository.findByHostIdWithSport(userId);

        // merge + tránh duplicate
        Map<Long, Event> map = new HashMap<>();

        for (Event e : joinedEvents) {
            map.put(e.getId(), e);
        }

        for (Event e : hostedEvents) {
            map.put(e.getId(), e);
        }

        // return new ArrayList<>(map.values());
        return map.values().stream().map(e -> {
            EventResponseDTO dto = new EventResponseDTO();

            dto.setId(e.getId());
            dto.setHostId(e.getHostId());

            boolean isHost = e.getHostId().equals(userId);

            dto.setHost(isHost);

            // // KEY LOGIC
            // dto.setHost(e.getHostId().equals(userId));

            // map các field cũ
            dto.setTitle(e.getTitle());
            dto.setNote(e.getNote());
            dto.setGoogleMapLink(e.getGoogleMapLink());
            dto.setLocationName(e.getLocationName());

            dto.setLat(e.getLocationLat());
            dto.setLng(e.getLocationLng());

            dto.setStartTime(e.getStartTime().toString());
            dto.setEndTime(e.getEndTime().toString());

            dto.setMaxPlayers(e.getMaxPlayers());

            dto.setPrefectureCode(e.getPrefectureCode());
            dto.setCityCode(e.getCityCode());

            dto.setSportId(e.getSport().getId());
            dto.setSport(e.getSport());

            dto.setLevelFrom(e.getLevelFrom());
            dto.setLevelTo(e.getLevelTo());
            dto.setStatus(e.getStatus().name());

            // ✅ NEW: currentPlayers
            long count = participantRepo.countByEventIdAndStatus(e.getId(), "JOINED");
            dto.setCurrentPlayers(count);

            // ✅ NEW: joinStatus
            if (isHost) {
                dto.setJoinStatus("HOST");
            } else {
                EventParticipant ep = participants.stream()
                        .filter(p -> p.getEventId().equals(e.getId()))
                        .findFirst()
                        .orElse(null);

                if (ep != null) {
                    dto.setJoinStatus(ep.getStatus()); // JOINED / WAITING
                } else {
                    dto.setJoinStatus("NONE");
                }
            }

            return dto;

        }).toList();

    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<?> getParticipants(@PathVariable Long id) {

        return ResponseEntity.ok(
                eventParticipantService.getParticipants(id));
    }

    @GetMapping("/{id}/status")
    public String getStatus(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        if (userId == null) {
            return "none";
        }

        EventParticipant ep = participantRepo
                .findByEventIdAndUserId(id, userId);

        if (ep == null) {
            return "none"; // chưa join
        }

        return ep.getStatus(); // "joined" hoặc "waiting"
    }

    @GetMapping("/open")
    public List<Event> getOpenEvents() {
        return eventRepository.findByStatus("open");
    }

    @GetMapping("/latest")
    public List<Event> getLatestEvents(
            @RequestParam(defaultValue = "50") int limit) {

        List<Event> events = eventRepository.findLatestEvents(
                PageRequest.of(0, limit));

        // ADD LOGIC COUNT USERS
        for (Event e : events) {
            long count = participantRepo.countByEventIdAndStatus(
                    e.getId(),
                    "JOINED" // đúng với DB của bạn
            );

            e.setCurrentPlayers(count);
        }

        return events;
    }

    @GetMapping("/host/{hostId}/past")
    public Page<Event> getHostPastEvents(
            @PathVariable Long hostId) {

        PageRequest pageable = PageRequest.of(0, 10);

        return eventRepository
                .findByHostIdAndStartTimeBeforeOrderByStartTimeDesc(
                        hostId,
                        LocalDateTime.now(),
                        pageable);
    }

    @GetMapping("/host/{hostId}/upcoming")
    public Page<Event> getHostUpcomingEvents(
            @PathVariable Long hostId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size);

        return eventRepository
                .findByHostIdAndStartTimeAfterOrderByStartTimeAsc(
                        hostId,
                        LocalDateTime.now(),
                        pageable);
    }

    @GetMapping("/home-feed")
    public Page<Event> getHomeFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,

            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,

            @RequestParam(defaultValue = "") String prefecture,
            @RequestParam(defaultValue = "") String city,

            @RequestParam(required = false) Long sport,
            @RequestParam(defaultValue = "") String keyword,

            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radius) {

        PageRequest pageable = PageRequest.of(page, size);

        LocalDateTime startTime = start != null
                ? LocalDateTime.parse(start)
                : null;

        LocalDateTime endTime = end != null
                ? LocalDateTime.parse(end)
                : null;

        // lấy page từ DB
        Page<Event> eventPage = eventRepository.searchFull(
                startTime,
                endTime,
                prefecture,
                city,
                sport,
                keyword,
                lat,
                lng,
                radius,
                pageable);

        // FIX QUAN TRỌNG: add currentPlayers
        List<Event> events = eventPage.getContent();

        for (Event e : events) {
            long count = participantRepo.countByEventIdAndStatus(e.getId(), "JOINED");

            e.setCurrentPlayers(count);
        }

        // giữ nguyên Page structure
        return eventPage;
    }

    @GetMapping
    public Page<Event> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,

            @RequestParam(defaultValue = "") String prefecture,
            @RequestParam(defaultValue = "") String city,

            @RequestParam(required = false) Long sport,
            @RequestParam(defaultValue = "") String keyword,

            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radius) {

        PageRequest pageable = PageRequest.of(page, size);

        LocalDateTime startTime = start != null
                ? LocalDateTime.parse(start)
                : null;

        LocalDateTime endTime = end != null
                ? LocalDateTime.parse(end)
                : null;

        return eventRepository.searchFull(
                startTime,
                endTime,
                prefecture,
                city,
                sport,
                keyword,
                lat,
                lng,
                radius,
                pageable);
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchEvents(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "ALL") String sport,
            @RequestParam(defaultValue = "") String prefecture,
            @RequestParam(defaultValue = "") String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Integer radius) {

        if (q == null)
            q = "";

        List<Event> events = eventRepository.searchEvents(
                q, sport, prefecture, city);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Event e : events) {
            Map<String, Object> item = new HashMap<>();

            // base event
            item.put("event", e);

            // currentPlayers
            long count = participantRepo.countByEventIdAndStatus(
                    e.getId(), "JOINED");
            e.setCurrentPlayers(count);

            // ADD LOCATION
            Location loc = resolveLocation(e.getCityCode());

            if (loc != null) {
                item.put("prefectureJa", loc.getPrefectureJa());
                item.put("prefectureEn", loc.getPrefectureEn());
                item.put("prefectureVi", loc.getPrefectureVi());

                item.put("cityJa", loc.getCityJa());
                item.put("cityEn", loc.getCityEn());
                item.put("cityVi", loc.getCityVi());
            }

            result.add(item);
        }

        return result;
    }

    private Location resolveLocation(String cityCode) {

        if (cityCode == null)
            return null;

        String code = cityCode;

        while (code != null && !code.isEmpty()) {

            // try exact match
            Location loc = locationRepo
                    .findByCityCode(code)
                    .orElse(null);

            if (loc != null) {
                return loc;
            }

            // fallback: remove last level
            int lastDash = code.lastIndexOf("-");
            if (lastDash == -1)
                break;

            code = code.substring(0, lastDash);
        }

        return null;
    }

    @GetMapping("/today")
    public List<EventTodayDTO> getTodayEvents() {

        ZoneId zone = ZoneId.of("Asia/Tokyo");

        LocalDateTime now = LocalDateTime.now(zone);
        LocalDate today = LocalDate.now(zone);

        LocalDateTime end = today.atTime(23, 59, 59);

        return eventRepository.findTodayEventsDTO(now, end);
    }

    @GetMapping("/map")
    public List<MapEventDto> getMapEvents(
            @RequestParam Double neLat,
            @RequestParam Double neLng,
            @RequestParam Double swLat,
            @RequestParam Double swLng) {
        return eventService.getEventsForMap(neLat, neLng, swLat, swLng);
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