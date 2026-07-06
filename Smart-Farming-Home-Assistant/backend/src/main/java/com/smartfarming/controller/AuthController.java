package com.smartfarming.controller;

import com.smartfarming.dto.*;
import com.smartfarming.service.UserService;
import com.smartfarming.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginRequest request) {
        Map<String, Object> result = userService.login(request);
        if (result == null) {
            return Result.error(401, "邮箱或密码错误");
        }
        return Result.success("登录成功", result);
    }

    @PostMapping("/register")
    public Result<AuthResponse.UserInfo> register(@RequestBody @Valid RegisterRequest request) {
        AuthResponse.UserInfo userInfo = userService.register(request);
        if (userInfo == null) {
            return Result.error(400, "邮箱已被注册或密码不一致");
        }
        return Result.success("注册成功", userInfo);
    }

    @GetMapping("/user/info")
    public Result<Map<String, Object>> getUserInfo() {
        Map<String, Object> info = userService.getCurrentUserInfo();
        if (info == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(info);
    }

    @PutMapping("/api/user/profile")
    public Result<Void> updateProfile(@RequestBody UpdateProfileRequest request) {
        userService.updateProfile(request);
        return Result.success("信息更新成功");
    }
}
