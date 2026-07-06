package com.smartfarming.config;

import com.smartfarming.entity.*;
import com.smartfarming.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private CropProductionRepository cropProductionRepository;

    @Autowired
    private EnvironmentRecordRepository environmentRecordRepository;

    @Autowired
    private PlantRecordRepository plantRecordRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        User admin = userRepository.save(User.builder()
                .username("超级管理员")
                .email("admin@example.com")
                .password(passwordEncoder.encode("123456"))
                .role("super_admin")
                .status("active")
                .phone("13800000000")
                .build());

        User farmer = userRepository.save(User.builder()
                .username("张农场主")
                .email("farmer@example.com")
                .password(passwordEncoder.encode("123456"))
                .role("farmer")
                .status("active")
                .phone("13888888888")
                .farmProvince("浙江省")
                .farmCity("杭州市")
                .farmDistrict("余杭区")
                .farmAddress("阳光大道888号")
                .homeAddress("杭州市西湖区文三路123号")
                .personalInfo("从事农业生产15年，专注于智慧农业技术应用")
                .build());

        cropRepository.save(Crop.builder().userId(farmer.getId()).name("水稻").category("主食").info("一年生禾本科植物，喜温喜湿").remark("种植面积20亩").build());
        cropRepository.save(Crop.builder().userId(farmer.getId()).name("人参").category("药材").info("多年生草本植物，名贵中药材").remark("种植面积5亩").build());
        cropRepository.save(Crop.builder().userId(farmer.getId()).name("西红柿").category("蔬菜").info("一年生草本植物，喜温喜光").remark("种植面积8亩").build());
        cropRepository.save(Crop.builder().userId(farmer.getId()).name("草莓").category("水果").info("多年生草本植物，喜凉爽气候").remark("种植面积3亩").build());
        cropRepository.save(Crop.builder().userId(farmer.getId()).name("棉花").category("经济作物").info("锦葵科棉属植物").remark("种植面积10亩").build());

        String[] cropNames = {"小麦", "玉米", "水稻", "蔬菜"};
        double[] productions = {45200, 38500, 26800, 15300};
        double[] areas = {800, 750, 550, 250};

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < cropNames.length; j++) {
                cropProductionRepository.save(CropProduction.builder()
                        .userId(farmer.getId())
                        .cropName(cropNames[j])
                        .production(productions[j] / 12.0 + Math.random() * 1000)
                        .area(areas[j])
                        .year(2024)
                        .month(i + 1)
                        .build());
            }
        }

        for (int i = 0; i < 24; i++) {
            environmentRecordRepository.save(EnvironmentRecord.builder()
                    .userId(farmer.getId())
                    .temperature(20 + Math.random() * 10)
                    .humidity(40 + Math.random() * 45)
                    .co2(300 + Math.random() * 500)
                    .soilPh(5.5 + Math.random() * 2.0)
                    .recordTime(LocalDateTime.now().minusHours(24 - i))
                    .build());
        }

        plantRecordRepository.save(PlantRecord.builder()
                .userId(farmer.getId())
                .cropId(1L)
                .cropName("水稻")
                .recordDate(LocalDate.now().minusDays(2))
                .content("# 水稻种植记录\n\n## 今日情况\n\n今天天气晴朗，气温25°C，湿度65%。水稻长势良好，叶片翠绿。")
                .images("[]")
                .build());

        plantRecordRepository.save(PlantRecord.builder()
                .userId(farmer.getId())
                .cropId(3L)
                .cropName("西红柿")
                .recordDate(LocalDate.now().minusDays(1))
                .content("# 西红柿种植记录\n\n## 今日情况\n\n西红柿开始开花，需要加强水肥管理。")
                .images("[]")
                .build());

        deviceRepository.save(Device.builder()
                .deviceCode("D001")
                .userId(farmer.getId())
                .name("温湿度传感器")
                .type("sensor")
                .status("online")
                .data("{\"temp\":25.6,\"humidity\":68}")
                .lastUpdate(LocalDateTime.now().minusMinutes(2))
                .remark("安装于大棚A区")
                .build());

        deviceRepository.save(Device.builder()
                .deviceCode("D002")
                .userId(farmer.getId())
                .name("土壤湿度传感器")
                .type("sensor")
                .status("online")
                .data("{\"soilMoisture\":72}")
                .lastUpdate(LocalDateTime.now().minusMinutes(5))
                .remark("安装于水稻田")
                .build());

        deviceRepository.save(Device.builder()
                .deviceCode("D003")
                .userId(farmer.getId())
                .name("智能灌溉控制器")
                .type("controller")
                .status("online")
                .data("{\"status\":\"自动模式\"}")
                .lastUpdate(LocalDateTime.now().minusMinutes(10))
                .remark("控制大棚灌溉系统")
                .build());

        deviceRepository.save(Device.builder()
                .deviceCode("D004")
                .userId(farmer.getId())
                .name("光照传感器")
                .type("sensor")
                .status("offline")
                .data("{\"light\":450}")
                .lastUpdate(LocalDateTime.now().minusHours(2))
                .remark("安装于温室大棚")
                .build());

        deviceRepository.save(Device.builder()
                .deviceCode("D005")
                .userId(farmer.getId())
                .name("CO2传感器")
                .type("sensor")
                .status("online")
                .data("{\"co2\":420}")
                .lastUpdate(LocalDateTime.now().minusMinutes(3))
                .remark("安装于温室大棚")
                .build());

        deviceRepository.save(Device.builder()
                .deviceCode("D006")
                .userId(farmer.getId())
                .name("智能通风系统")
                .type("controller")
                .status("online")
                .data("{\"status\":\"运行中\"}")
                .lastUpdate(LocalDateTime.now().minusMinutes(8))
                .remark("控制温室通风")
                .build());

        System.out.println("===== 初始数据创建完成 =====");
        System.out.println("管理员账号: admin@example.com / 123456");
        System.out.println("农场主账号: farmer@example.com / 123456");
    }
}
