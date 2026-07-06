package com.smartfarming.service;

import com.smartfarming.entity.User;
import com.smartfarming.repository.UserRepository;
import com.smartfarming.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public PageResult<Map<String, Object>> getUserList(int page, int size, String keyword) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("id").descending());

        Page<User> userPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            userPage = userRepository.findByUsernameContainingOrEmailContaining(keyword, keyword, pageRequest);
        } else {
            userPage = userRepository.findAll(pageRequest);
        }

        List<Map<String, Object>> list = userPage.getContent().stream()
                .map(this::buildUserInfo)
                .collect(Collectors.toList());

        return new PageResult<>(list, userPage.getTotalElements(), page, size);
    }

    public void updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        userRepository.save(user);
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("email", user.getEmail());
        info.put("role", user.getRole());
        info.put("status", user.getStatus());
        info.put("createTime", user.getCreateTime() != null ?
                user.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
        return info;
    }
}
