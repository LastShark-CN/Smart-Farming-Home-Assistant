package com.smartfarming.service;

import com.smartfarming.entity.Device;
import com.smartfarming.repository.DeviceRepository;
import com.smartfarming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Device> getDeviceList() {
        Long userId = getCurrentUserId();
        return deviceRepository.findByUserId(userId);
    }

    public Device getDeviceById(Long id) {
        Long userId = getCurrentUserId();
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        if (!device.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该设备");
        }
        return device;
    }

    public Device createDevice(Device device) {
        Long userId = getCurrentUserId();
        device.setUserId(userId);
        device.setStatus("online");
        return deviceRepository.save(device);
    }

    public Device updateDevice(Long id, Device updatedDevice) {
        Device device = getDeviceById(id);
        device.setName(updatedDevice.getName());
        device.setType(updatedDevice.getType());
        device.setData(updatedDevice.getData());
        device.setRemark(updatedDevice.getRemark());
        return deviceRepository.save(device);
    }

    public void deleteDevice(Long id) {
        getDeviceById(id);
        deviceRepository.deleteById(id);
    }

    public Device updateDeviceStatus(Long id, String status) {
        Device device = getDeviceById(id);
        device.setStatus(status);
        return deviceRepository.save(device);
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