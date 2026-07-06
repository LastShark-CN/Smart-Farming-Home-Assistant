package com.smartfarming.service;

import com.smartfarming.entity.EnvironmentRecord;
import com.smartfarming.repository.EnvironmentRecordRepository;
import com.smartfarming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MonitorService {

    @Autowired
    private EnvironmentRecordRepository environmentRecordRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getCurrentEnvironment() {
        Long userId = getCurrentUserId();
        List<EnvironmentRecord> latestList = environmentRecordRepository.findTop1ByUserIdOrderByRecordTimeDesc(userId);
        EnvironmentRecord latest = latestList.isEmpty() ? null : latestList.get(0);

        if (latest == null) {
            latest = generateAndSaveRecord(userId);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("temperature", buildSensorData(latest.getTemperature(), "°C", 15.0, 35.0, 20.0, 28.0));
        data.put("humidity", buildSensorData(latest.getHumidity(), "%", 30.0, 90.0, 50.0, 80.0));
        data.put("co2", buildSensorData(latest.getCo2(), "ppm", 200.0, 1000.0, 350.0, 600.0));
        data.put("soilPh", buildSensorData(latest.getSoilPh(), "", 4.0, 9.0, 5.5, 7.5));

        return data;
    }

    public List<Map<String, Object>> getHistoryData(Integer hours, String startTime, String endTime) {
        Long userId = getCurrentUserId();
        LocalDateTime end;
        LocalDateTime start;

        if (startTime != null && endTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            start = LocalDateTime.parse(startTime, formatter);
            end = LocalDateTime.parse(endTime, formatter);
        } else {
            end = LocalDateTime.now();
            start = end.minusHours(hours != null ? hours : 24);
        }

        List<EnvironmentRecord> records = environmentRecordRepository
                .findByUserIdAndRecordTimeBetweenOrderByRecordTimeAsc(userId, start, end);

        List<Map<String, Object>> result = new ArrayList<>();
        for (EnvironmentRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("time", record.getRecordTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            item.put("temperature", record.getTemperature());
            item.put("humidity", record.getHumidity());
            item.put("co2", record.getCo2());
            result.add(item);
        }

        if (result.isEmpty()) {
            for (int i = (hours != null ? hours : 24); i >= 0; i--) {
                LocalDateTime time = LocalDateTime.now().minusHours(i);
                Map<String, Object> item = new HashMap<>();
                item.put("time", time.format(DateTimeFormatter.ofPattern("HH:mm")));
                item.put("temperature", generateRandomValue(20, 32));
                item.put("humidity", generateRandomValue(40, 85));
                item.put("co2", generateRandomValue(300, 800));
                result.add(item);
            }
        }

        return result;
    }

    private EnvironmentRecord generateAndSaveRecord(Long userId) {
        Random random = ThreadLocalRandom.current();
        EnvironmentRecord record = EnvironmentRecord.builder()
                .userId(userId)
                .temperature(20 + random.nextDouble() * 10)
                .humidity(40 + random.nextDouble() * 45)
                .co2(300 + random.nextDouble() * 500)
                .soilPh(5.5 + random.nextDouble() * 2.0)
                .recordTime(LocalDateTime.now())
                .build();
        return environmentRecordRepository.save(record);
    }

    private Map<String, Object> buildSensorData(Double value, String unit,
                                                 Double min, Double max,
                                                 Double optimalMin, Double optimalMax) {
        Map<String, Object> sensorData = new HashMap<>();
        sensorData.put("value", Math.round(value * 10.0) / 10.0);
        sensorData.put("unit", unit);

        String status;
        if (value >= optimalMin && value <= optimalMax) {
            status = "normal";
        } else if (value >= min && value <= max) {
            status = "warning";
        } else {
            status = "danger";
        }
        sensorData.put("status", status);

        Map<String, Object> range = new HashMap<>();
        range.put("min", min);
        range.put("max", max);
        Map<String, Object> optimal = new HashMap<>();
        optimal.put("min", optimalMin);
        optimal.put("max", optimalMax);
        range.put("optimal", optimal);
        sensorData.put("range", range);

        return sensorData;
    }

    private Double generateRandomValue(double min, double max) {
        return Math.round((min + ThreadLocalRandom.current().nextDouble() * (max - min)) * 10.0) / 10.0;
    }

    private Long getCurrentUserId() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}
