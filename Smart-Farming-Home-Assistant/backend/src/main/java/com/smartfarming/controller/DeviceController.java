package com.smartfarming.controller;

import com.smartfarming.entity.Device;
import com.smartfarming.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('FARMER', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getDevices() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Device> devices = deviceService.getDeviceList();
            response.put("code", 200);
            response.put("data", devices);
            response.put("message", "success");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('FARMER', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getDeviceById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Device device = deviceService.getDeviceById(id);
            response.put("code", 200);
            response.put("data", device);
            response.put("message", "success");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FARMER', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> createDevice(@RequestBody Device device) {
        Map<String, Object> response = new HashMap<>();
        try {
            Device created = deviceService.createDevice(device);
            response.put("code", 200);
            response.put("data", created);
            response.put("message", "设备添加成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FARMER', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        Map<String, Object> response = new HashMap<>();
        try {
            Device updated = deviceService.updateDevice(id, device);
            response.put("code", 200);
            response.put("data", updated);
            response.put("message", "设备更新成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FARMER', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteDevice(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            deviceService.deleteDevice(id);
            response.put("code", 200);
            response.put("message", "设备删除成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('FARMER', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateDeviceStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Device updated = deviceService.updateDeviceStatus(id, request.get("status"));
            response.put("code", 200);
            response.put("data", updated);
            response.put("message", "设备状态更新成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}