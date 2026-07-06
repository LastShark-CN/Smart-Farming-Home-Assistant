package com.smartfarming.service;

import com.smartfarming.entity.CropProduction;
import com.smartfarming.repository.CropProductionRepository;
import com.smartfarming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private CropProductionRepository cropProductionRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getProductionData() {
        Long userId = getCurrentUserId();
        int currentYear = LocalDate.now().getYear();
        List<CropProduction> records = cropProductionRepository.findByUserIdAndYear(userId, currentYear);

        Map<String, Object> data = new HashMap<>();

        if (records.isEmpty()) {
            return generateDefaultProductionData();
        }

        double totalProduction = records.stream()
                .mapToDouble(CropProduction::getProduction)
                .sum();
        data.put("totalProduction", Math.round(totalProduction));
        data.put("unit", "kg");

        List<Map<String, Object>> trend = new ArrayList<>();
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        for (int i = 0; i < 12; i++) {
            final int month = i + 1;
            double monthTotal = records.stream()
                    .filter(r -> r.getMonth() == month)
                    .mapToDouble(CropProduction::getProduction)
                    .sum();
            trend.add(Map.of("month", months[i], "value", Math.round(monthTotal)));
        }
        data.put("trend", trend);

        Map<String, Double> cropMap = new LinkedHashMap<>();
        for (CropProduction record : records) {
            cropMap.merge(record.getCropName(), record.getProduction(), Double::sum);
        }

        List<Map<String, Object>> crops = cropMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> crop = new HashMap<>();
                    crop.put("name", entry.getKey());
                    crop.put("production", Math.round(entry.getValue()));
                    crop.put("percentage", (int) Math.round(entry.getValue() / totalProduction * 100));
                    return crop;
                })
                .collect(Collectors.toList());
        data.put("crops", crops);

        return data;
    }

    public Map<String, Object> getAreaData() {
        Long userId = getCurrentUserId();
        List<CropProduction> records = cropProductionRepository.findByUserId(userId);

        if (records.isEmpty()) {
            return generateDefaultAreaData();
        }

        Map<String, Object> data = new HashMap<>();

        double totalArea = records.stream()
                .mapToDouble(r -> r.getArea() != null ? r.getArea() : 0)
                .sum();
        data.put("totalArea", Math.round(totalArea));
        data.put("unit", "亩");

        String[] colors = {"#5470c6", "#91cc75", "#fac858", "#ee6666", "#73c0de", "#3ba272", "#fc8452", "#9a60b4"};
        Map<String, Double> areaMap = new LinkedHashMap<>();
        for (CropProduction record : records) {
            if (record.getArea() != null) {
                areaMap.merge(record.getCropName(), record.getArea(), Double::sum);
            }
        }

        List<Map<String, Object>> crops = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Double> entry : areaMap.entrySet()) {
            Map<String, Object> crop = new HashMap<>();
            crop.put("name", entry.getKey());
            crop.put("area", Math.round(entry.getValue()));
            crop.put("color", colors[i % colors.length]);
            crops.add(crop);
            i++;
        }
        data.put("crops", crops);

        List<Map<String, Object>> growthStage = List.of(
                Map.of("stage", "播种期", "percentage", 15),
                Map.of("stage", "生长期", "percentage", 45),
                Map.of("stage", "成熟期", "percentage", 25),
                Map.of("stage", "收获期", "percentage", 15)
        );
        data.put("growthStage", growthStage);

        return data;
    }

    private Map<String, Object> generateDefaultProductionData() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalProduction", 125800);
        data.put("unit", "kg");

        List<Map<String, Object>> trend = new ArrayList<>();
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        int[] values = {8500, 9200, 11500, 13800, 15200, 16800, 14500, 12800, 10500, 9200, 8500, 6800};
        for (int i = 0; i < 12; i++) {
            trend.add(Map.of("month", months[i], "value", values[i]));
        }
        data.put("trend", trend);

        data.put("crops", List.of(
                Map.of("name", "小麦", "production", 45200, "percentage", 36),
                Map.of("name", "玉米", "production", 38500, "percentage", 31),
                Map.of("name", "水稻", "production", 26800, "percentage", 21),
                Map.of("name", "蔬菜", "production", 15300, "percentage", 12)
        ));

        return data;
    }

    private Map<String, Object> generateDefaultAreaData() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalArea", 2500);
        data.put("unit", "亩");

        data.put("crops", List.of(
                Map.of("name", "小麦", "area", 800, "color", "#5470c6"),
                Map.of("name", "玉米", "area", 750, "color", "#91cc75"),
                Map.of("name", "水稻", "area", 550, "color", "#fac858"),
                Map.of("name", "蔬菜", "area", 250, "color", "#ee6666"),
                Map.of("name", "果树", "area", 150, "color", "#73c0de")
        ));

        data.put("growthStage", List.of(
                Map.of("stage", "播种期", "percentage", 15),
                Map.of("stage", "生长期", "percentage", 45),
                Map.of("stage", "成熟期", "percentage", 25),
                Map.of("stage", "收获期", "percentage", 15)
        ));

        return data;
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
