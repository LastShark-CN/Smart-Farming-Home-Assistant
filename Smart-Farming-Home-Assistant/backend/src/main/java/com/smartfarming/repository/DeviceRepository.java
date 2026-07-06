package com.smartfarming.repository;

import com.smartfarming.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findByUserId(Long userId);

    List<Device> findByUserIdAndStatus(Long userId, String status);

    List<Device> findByUserIdAndType(Long userId, String type);

    Device findByDeviceCode(String deviceCode);
}