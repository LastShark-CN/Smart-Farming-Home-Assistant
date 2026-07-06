package com.smartfarming.controller;

import com.smartfarming.dto.UpdateUserStatusRequest;
import com.smartfarming.service.AdminService;
import com.smartfarming.util.PageResult;
import com.smartfarming.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public Result<PageResult<Map<String, Object>>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(adminService.getUserList(page, size, keyword));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id,
                                          @RequestBody @Valid UpdateUserStatusRequest request) {
        adminService.updateUserStatus(id, request.getStatus());
        return Result.success("状态更新成功");
    }
}
