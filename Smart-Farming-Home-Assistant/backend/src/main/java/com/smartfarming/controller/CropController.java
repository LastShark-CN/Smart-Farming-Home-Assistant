package com.smartfarming.controller;

import com.smartfarming.entity.Crop;
import com.smartfarming.service.CropService;
import com.smartfarming.service.PlantRecordService;
import com.smartfarming.util.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;
    private final PlantRecordService plantRecordService;

    public CropController(CropService cropService, PlantRecordService plantRecordService) {
        this.cropService = cropService;
        this.plantRecordService = plantRecordService;
    }

    @GetMapping("/options")
    public Result<List<Map<String, Object>>> getCropOptions() {
        return Result.success(plantRecordService.getCropOptions());
    }

    @GetMapping
    public Result<List<Crop>> getCropList() {
        return Result.success(cropService.getCropList());
    }

    @PostMapping
    public Result<Crop> createCrop(@RequestBody Crop crop) {
        return Result.success("创建成功", cropService.createCrop(crop));
    }

    @PutMapping("/{id}")
    public Result<Crop> updateCrop(@PathVariable Long id, @RequestBody Crop crop) {
        return Result.success("更新成功", cropService.updateCrop(id, crop));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCrop(@PathVariable Long id) {
        cropService.deleteCrop(id);
        return Result.success("删除成功");
    }
}
