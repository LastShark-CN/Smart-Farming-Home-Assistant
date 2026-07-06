package com.smartfarming.controller;

import com.smartfarming.service.StatisticsService;
import com.smartfarming.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/production")
    public Result<Map<String, Object>> getProduction() {
        return Result.success(statisticsService.getProductionData());
    }

    @GetMapping("/area")
    public Result<Map<String, Object>> getArea() {
        return Result.success(statisticsService.getAreaData());
    }
}
