package com.smartfarming.service;

import com.smartfarming.dto.AuthResponse;
import com.smartfarming.dto.LoginRequest;
import com.smartfarming.dto.RegisterRequest;
import com.smartfarming.dto.UpdateProfileRequest;
import com.smartfarming.entity.User;
import com.smartfarming.repository.UserRepository;
import com.smartfarming.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return null;
        }

        if (!"active".equals(user.getStatus())) {
            return null;
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().toUpperCase())
                .build();

        String token = jwtUtil.generateToken(userDetails);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));

        return result;
    }

    public AuthResponse.UserInfo register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return null;
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return null;
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("farmer")
                .status("active")
                .build();

        userRepository.save(user);

        return new AuthResponse.UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    public Map<String, Object> getCurrentUserInfo() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return null;
        }

        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("email", user.getEmail());
        info.put("role", user.getRole());
        info.put("phone", user.getPhone());
        info.put("avatar", user.getAvatar());
        info.put("farmProvince", user.getFarmProvince());
        info.put("farmCity", user.getFarmCity());
        info.put("farmDistrict", user.getFarmDistrict());
        info.put("farmAddress", user.getFarmAddress());
        info.put("homeAddress", user.getHomeAddress());
        info.put("personalInfo", user.getPersonalInfo());

        return info;
    }

    public void updateProfile(UpdateProfileRequest request) {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getFarmProvince() != null) user.setFarmProvince(request.getFarmProvince());
        if (request.getFarmCity() != null) user.setFarmCity(request.getFarmCity());
        if (request.getFarmDistrict() != null) user.setFarmDistrict(request.getFarmDistrict());
        if (request.getFarmAddress() != null) user.setFarmAddress(request.getFarmAddress());
        if (request.getHomeAddress() != null) user.setHomeAddress(request.getHomeAddress());
        if (request.getPersonalInfo() != null) user.setPersonalInfo(request.getPersonalInfo());

        userRepository.save(user);
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}
