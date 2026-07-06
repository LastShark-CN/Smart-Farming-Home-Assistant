SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    role VARCHAR(20) NOT NULL DEFAULT 'farmer' COMMENT '角色：super_admin, admin, farmer',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active, inactive',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    farm_province VARCHAR(50) COMMENT '农场省份',
    farm_city VARCHAR(50) COMMENT '农场城市',
    farm_district VARCHAR(50) COMMENT '农场区县',
    farm_address VARCHAR(255) COMMENT '农场地址',
    home_address VARCHAR(255) COMMENT '家庭地址',
    personal_info TEXT COMMENT '个人简介',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 作物表
CREATE TABLE IF NOT EXISTS crops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '作物ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '作物名称',
    category VARCHAR(50) NOT NULL COMMENT '分类：主食, 蔬菜, 水果, 药材, 经济作物等',
    info TEXT COMMENT '作物信息',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_name (name),
    INDEX idx_category (category),
    CONSTRAINT fk_crops_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作物表';

-- 作物产量表
CREATE TABLE IF NOT EXISTS crop_production (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '产量记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    crop_name VARCHAR(100) NOT NULL COMMENT '作物名称',
    production DOUBLE NOT NULL COMMENT '产量(kg)',
    area DOUBLE COMMENT '种植面积(m²)',
    crop_year INT NOT NULL COMMENT '年份',
    crop_month INT NOT NULL COMMENT '月份',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_crop_name (crop_name),
    INDEX idx_year_month (crop_year, crop_month),
    CONSTRAINT fk_crop_production_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作物产量表';

-- 设备表
CREATE TABLE IF NOT EXISTS devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '设备ID',
    device_code VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编码',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '设备名称',
    type VARCHAR(50) NOT NULL COMMENT '类型：sensor, controller',
    status VARCHAR(20) NOT NULL COMMENT '状态：online, offline',
    data TEXT COMMENT '设备数据(JSON)',
    last_update DATETIME COMMENT '最后更新时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_device_code (device_code),
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_status (status),
    CONSTRAINT fk_devices_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';

-- 环境记录表
CREATE TABLE IF NOT EXISTS environment_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    temperature DOUBLE COMMENT '温度(°C)',
    humidity DOUBLE COMMENT '湿度(%)',
    co2 DOUBLE COMMENT 'CO2浓度(ppm)',
    soil_ph DOUBLE COMMENT '土壤pH值',
    record_time DATETIME NOT NULL COMMENT '记录时间',
    INDEX idx_user_id (user_id),
    INDEX idx_record_time (record_time),
    CONSTRAINT fk_environment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='环境记录表';

-- 种植记录表
CREATE TABLE IF NOT EXISTS plant_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    crop_id BIGINT COMMENT '作物ID',
    crop_name VARCHAR(100) COMMENT '作物名称',
    record_date DATE NOT NULL COMMENT '记录日期',
    content TEXT NOT NULL COMMENT '记录内容(Markdown)',
    images TEXT COMMENT '图片列表(JSON)',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_crop_id (crop_id),
    INDEX idx_record_date (record_date),
    CONSTRAINT fk_plant_records_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_plant_records_crop FOREIGN KEY (crop_id) REFERENCES crops(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='种植记录表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(20) NOT NULL COMMENT '角色：user, bot',
    content TEXT NOT NULL COMMENT '消息内容',
    expression VARCHAR(20) COMMENT '表情：happy, sad, thinking, surprised, normal',
    timestamp DATETIME NOT NULL COMMENT '时间戳',
    INDEX idx_user_id (user_id),
    INDEX idx_timestamp (timestamp),
    CONSTRAINT fk_chat_messages_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';
