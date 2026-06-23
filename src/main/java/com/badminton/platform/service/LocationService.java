package com.badminton.platform.service;

import com.badminton.platform.entity.Location;
import com.badminton.platform.repository.LocationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public List<Map<String, Object>> getGroupedLocations() {
        List<Location> list = locationRepository.findAll();

        Map<String, Map<String, Object>> map = new LinkedHashMap<>();

        for (Location l : list) {
            map.putIfAbsent(l.getPrefectureCode(), new HashMap<>());

            Map<String, Object> pref = map.get(l.getPrefectureCode());

            pref.put("code", l.getPrefectureCode());
            pref.put("ja", l.getPrefectureJa());
            pref.put("en", l.getPrefectureEn());
            pref.put("vi", l.getPrefectureVi());

            pref.putIfAbsent("cities", new ArrayList<>());

            List<Map<String, Object>> cities =
                    (List<Map<String, Object>>) pref.get("cities");

            Map<String, Object> city = new HashMap<>();
            city.put("code", l.getCityCode());
            city.put("ja", l.getCityJa());
            city.put("en", l.getCityEn());
            city.put("vi", l.getCityVi());

            cities.add(city);
        }

        return new ArrayList<>(map.values());
    }
    
}