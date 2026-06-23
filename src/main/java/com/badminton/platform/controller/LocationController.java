package com.badminton.platform.controller;

import com.badminton.platform.service.LocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public List<Map<String, Object>> getLocations() {
        return locationService.getGroupedLocations();
    }
}