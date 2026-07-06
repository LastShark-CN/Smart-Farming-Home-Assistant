package com.smartfarming.controller;

import com.smartfarming.service.MonitorService;
import com.smartfarming.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @GetMapping("/environment")
    public Result<Map<String, Object>> getEnvironment() {
        return Result.success(monitorService.getCurrentEnvironment());
    }

    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getHistory(
            @RequestParam(required = false) Integer hours,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(monitorService.getHistoryData(hours, startTime, endTime));
    }
}
