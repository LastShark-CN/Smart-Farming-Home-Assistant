package com.smartfarming.controller;

import com.smartfarming.dto.CreatePlantRecordRequest;
import com.smartfarming.dto.UpdatePlantRecordRequest;
import com.smartfarming.service.PlantRecordService;
import com.smartfarming.util.PageResult;
import com.smartfarming.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plant-records")
public class PlantRecordController {

    @Autowired
    private PlantRecordService plantRecordService;

    @PostMapping
    public Result<Map<String, Object>> createRecord(@RequestBody @Valid CreatePlantRecordRequest request) {
        return Result.success("创建成功", plantRecordService.createRecord(request));
    }

    @GetMapping
    public Result<PageResult<Map<String, Object>>> getRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long cropId,
            @RequestParam(required = false) String recordDate) {
        return Result.success(plantRecordService.getRecords(page, size, cropId, recordDate));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getRecord(@PathVariable Long id) {
        Map<String, Object> record = plantRecordService.getRecordById(id);
        if (record == null) {
            return Result.error(404, "记录不存在");
        }
        return Result.success(record);
    }

    @PutMapping("/{id}")
    public Result<Void> updateRecord(@PathVariable Long id,
                                     @RequestBody UpdatePlantRecordRequest request) {
        plantRecordService.updateRecord(id, request);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        plantRecordService.deleteRecord(id);
        return Result.success("删除成功");
    }
}
