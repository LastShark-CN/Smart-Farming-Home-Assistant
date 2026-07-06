package com.smartfarming.service;

import com.smartfarming.dto.CreatePlantRecordRequest;
import com.smartfarming.dto.UpdatePlantRecordRequest;
import com.smartfarming.entity.Crop;
import com.smartfarming.entity.PlantRecord;
import com.smartfarming.repository.CropRepository;
import com.smartfarming.repository.PlantRecordRepository;
import com.smartfarming.repository.UserRepository;
import com.smartfarming.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlantRecordService {

    @Autowired
    private PlantRecordRepository plantRecordRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> createRecord(CreatePlantRecordRequest request) {
        Long userId = getCurrentUserId();
        LocalDate recordDate = LocalDate.parse(request.getRecordDate());

        PlantRecord record = PlantRecord.builder()
                .userId(userId)
                .cropId(request.getCropId() != null ? request.getCropId() : 0L)
                .cropName(request.getCropName())
                .recordDate(recordDate)
                .content(request.getContent())
                .images(request.getImages())
                .build();

        plantRecordRepository.save(record);

        return buildRecordResponse(record);
    }

    public PageResult<Map<String, Object>> getRecords(int page, int size, Long cropId, String recordDate) {
        Long userId = getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<PlantRecord> recordPage;

        if (cropId != null && recordDate != null) {
            LocalDate date = LocalDate.parse(recordDate);
            recordPage = plantRecordRepository.findByUserIdAndCropIdAndRecordDateOrderByRecordDateDesc(userId, cropId, date, pageRequest);
        } else if (cropId != null) {
            recordPage = plantRecordRepository.findByUserIdAndCropIdOrderByRecordDateDesc(userId, cropId, pageRequest);
        } else if (recordDate != null) {
            LocalDate date = LocalDate.parse(recordDate);
            recordPage = plantRecordRepository.findByUserIdAndRecordDateOrderByRecordDateDesc(userId, date, pageRequest);
        } else {
            recordPage = plantRecordRepository.findByUserIdOrderByRecordDateDesc(userId, pageRequest);
        }

        List<Map<String, Object>> list = recordPage.getContent().stream()
                .map(this::buildRecordResponse)
                .collect(Collectors.toList());

        return new PageResult<>(list, recordPage.getTotalElements(), page, size);
    }

    public Map<String, Object> getRecordById(Long id) {
        PlantRecord record = plantRecordRepository.findById(id)
                .orElse(null);
        if (record == null) return null;

        Map<String, Object> data = buildRecordResponse(record);
        data.put("updateTime", record.getUpdateTime() != null ?
                record.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
        return data;
    }

    public void updateRecord(Long id, UpdatePlantRecordRequest request) {
        PlantRecord record = plantRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("记录不存在"));

        if (request.getCropId() != null) record.setCropId(request.getCropId());
        if (request.getCropName() != null) record.setCropName(request.getCropName());
        if (request.getRecordDate() != null) record.setRecordDate(LocalDate.parse(request.getRecordDate()));
        if (request.getContent() != null) record.setContent(request.getContent());
        if (request.getImages() != null) record.setImages(request.getImages());

        plantRecordRepository.save(record);
    }

    public void deleteRecord(Long id) {
        plantRecordRepository.deleteById(id);
    }

    public List<Map<String, Object>> getCropOptions() {
        Long userId = getCurrentUserId();
        List<Crop> crops = cropRepository.findByUserId(userId);

        if (crops.isEmpty()) {
            return List.of(
                    Map.of("id", 1, "name", "水稻"),
                    Map.of("id", 2, "name", "小麦"),
                    Map.of("id", 3, "name", "玉米"),
                    Map.of("id", 4, "name", "蔬菜"),
                    Map.of("id", 5, "name", "水果")
            );
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Crop crop : crops) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", crop.getId());
            item.put("name", crop.getName());
            result.add(item);
        }
        return result;
    }

    private Map<String, Object> buildRecordResponse(PlantRecord record) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", record.getId());
        data.put("cropId", record.getCropId());
        data.put("cropName", record.getCropName());
        data.put("recordDate", record.getRecordDate().toString());
        data.put("content", record.getContent());
        data.put("images", record.getImages());
        data.put("createTime", record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
