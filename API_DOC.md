# 智慧农业管理系统 - 后端接口文档

## 基础信息

- **服务地址**: http://localhost:8080
- **接口前缀**: `/api`（部分认证接口除外）
- **认证方式**: JWT Token（Bearer Token）
- **请求格式**: JSON
- **响应格式**: JSON

---

## 目录

1. [认证模块 (Auth)](#1-认证模块-auth)
2. [用户模块 (User)](#2-用户模块-user)
3. [管理员模块 (Admin)](#3-管理员模块-admin)
4. [首页模块 (Home)](#4-首页模块-home)
5. [设备管理模块 (Device)](#5-设备管理模块-device)
6. [设备类型模块 (DeviceType)](#6-设备类型模块-devicetype)
7. [农作物模块 (Crop)](#7-农作物模块-crop)
8. [种植记录模块 (PlantRecord)](#8-种植记录模块-plantrecord)
9. [环境监测模块 (Monitor)](#9-环境监测模块-monitor)
10. [统计模块 (Statistics)](#10-统计模块-statistics)
11. [文件上传模块 (Upload)](#11-文件上传模块-upload)
12. [帮助中心模块 (Help)](#12-帮助中心模块-help)
13. [意见反馈模块 (Feedback)](#13-意见反馈模块-feedback)
14. [设备控制模块 (DeviceControl)](#14-设备控制模块-devicecontrol)
15. [聊天模块 (Chat)](#15-聊天模块-chat)
16. [状态码说明](#16-状态码说明)
17. [白名单接口](#17-白名单接口)

---

## 1. 认证模块 (Auth)

### 1.1 用户登录

| 属性 | 值 |
|------|-----|
| 路径 | `/login` |
| 方法 | POST |
| 认证 | 否 |

**请求体**:
```json
{
  "email": "admin@smartfarm.com",
  "password": "admin123"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@smartfarm.com",
      "role": "super_admin"
    }
  }
}
```

**失败响应** (401):
```json
{
  "code": 401,
  "message": "邮箱或密码错误",
  "data": null
}
```

### 1.2 用户注册

| 属性 | 值 |
|------|-----|
| 路径 | `/register` |
| 方法 | POST |
| 认证 | 否 |

**请求体**:
```json
{
  "username": "testuser",
  "email": "test@smartfarm.com",
  "password": "123456",
  "confirmPassword": "123456",
  "phone": "13800138000"
}
```

**字段说明**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| email | string | 是 | 邮箱（需符合格式） |
| password | string | 是 | 密码（≥6位） |
| confirmPassword | string | 是 | 确认密码（需与password一致） |
| phone | string | 否 | 手机号（11位） |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 2,
    "username": "testuser",
    "email": "test@smartfarm.com",
    "role": "farmer"
  }
}
```

---

## 2. 用户模块 (User)

### 2.1 获取用户信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/info` |
| 方法 | GET |
| 认证 | 是 |

**请求头**:
```
Authorization: Bearer <token>
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@smartfarm.com",
    "role": "super_admin",
    "phone": "13800138000",
    "avatar": null,
    "farmProvince": "浙江省",
    "farmCity": "杭州市",
    "farmDistrict": "余杭区",
    "farmAddress": "未来科技城",
    "homeAddress": null,
    "personalInfo": null,
    "loginBackground": "/api/uploads/xxx.jpg",
    "homeBackground": null
  }
}
```

### 2.2 更新用户信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/profile` |
| 方法 | PUT |
| 认证 | 是 |

**请求体**:
```json
{
  "email": "new@smartfarm.com",
  "phone": "13900139000",
  "avatar": "/api/uploads/avatar.jpg",
  "farmProvince": "浙江省",
  "farmCity": "杭州市",
  "farmDistrict": "西湖区",
  "farmAddress": "文三路",
  "homeAddress": "住址信息",
  "personalInfo": "个人简介"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "信息更新成功",
  "data": null
}
```

### 2.3 更新登录背景

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/background/login` |
| 方法 | PUT |
| 认证 | 是 |

**请求体**:
```json
{
  "url": "/api/uploads/bg_login.jpg"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "登录背景更新成功",
  "data": null
}
```

### 2.4 更新主页背景

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/background/home` |
| 方法 | PUT |
| 认证 | 是 |

**请求体**:
```json
{
  "url": "/api/uploads/bg_home.jpg"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "主页背景更新成功",
  "data": null
}
```

### 2.5 获取背景设置

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/background` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "loginBackground": "/api/uploads/bg_login.jpg",
    "homeBackground": "/api/uploads/bg_home.jpg"
  }
}
```

---

## 3. 管理员模块 (Admin)

### 3.1 获取管理员列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users` |
| 方法 | GET |
| 认证 | 是 |
| 权限 | super_admin |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页条数 |
| keyword | string | 否 | - | 搜索关键词（用户名/邮箱） |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 5,
    "page": 1,
    "size": 10
  }
}
```

### 3.2 创建管理员

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users` |
| 方法 | POST |
| 认证 | 是 |
| 权限 | super_admin |

**请求体**:
```json
{
  "username": "newadmin",
  "email": "newadmin@smartfarm.com",
  "password": "123456",
  "phone": "13700137000"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 3,
    "username": "newadmin",
    "email": "newadmin@smartfarm.com",
    "role": "admin"
  }
}
```

### 3.3 更新管理员信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users/{id}` |
| 方法 | PUT |
| 认证 | 是 |
| 权限 | super_admin |

**请求体**:
```json
{
  "username": "updatedadmin",
  "email": "updated@smartfarm.com",
  "phone": "13600136000"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 3.4 删除管理员

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users/{id}` |
| 方法 | DELETE |
| 认证 | 是 |
| 权限 | super_admin |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 3.5 更新管理员状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users/{id}/status` |
| 方法 | PUT |
| 认证 | 是 |
| 权限 | super_admin |

**请求体**:
```json
{
  "status": "active"
}
```

**status可选值**: `active`（启用）、`inactive`（禁用）

**成功响应** (200):
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

## 4. 首页模块 (Home)

### 4.1 获取天气信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/home/weather` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "city": "杭州市",
    "temperature": 25.6,
    "weather": "晴",
    "humidity": 68,
    "wind": "东南风",
    "windLevel": "3级",
    "sunrise": "05:45",
    "sunset": "18:55",
    "tips": "今日天气晴朗，适合农业生产活动"
  }
}
```

### 4.2 获取公司信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/home/company` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "name": "智慧农业科技有限公司",
    "slogan": "科技赋能农业，智慧引领未来",
    "vision": "成为全球领先的智慧农业解决方案提供商",
    "mission": "用科技改变传统农业，让每一寸土地都发挥最大价值",
    "phone": "400-888-8888",
    "email": "contact@smartfarm.com",
    "address": "浙江省杭州市余杭区未来科技城"
  }
}
```

### 4.3 获取欢迎语

| 属性 | 值 |
|------|-----|
| 路径 | `/api/home/welcome` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "message": "欢迎回来！",
    "tips": "今天也是充满希望的一天，祝您农业生产顺利！"
  }
}
```

---

## 5. 设备管理模块 (Device)

### 5.1 获取设备列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": [...]
}
```

### 5.2 分页查询设备

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/page` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页条数 |
| keyword | string | 否 | - | 搜索关键词 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

### 5.3 获取设备详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/{id}` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "温湿度传感器",
    "code": "D001",
    "status": 1,
    "location": "温室A区",
    "deviceTypeId": 1,
    "typeName": "传感器"
  }
}
```

### 5.4 创建设备

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device` |
| 方法 | POST |
| 认证 | 是 |

**请求体**:
```json
{
  "name": "新设备",
  "code": "D002",
  "status": 0,
  "location": "温室B区",
  "deviceTypeId": 1,
  "description": "设备描述"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {...}
}
```

### 5.5 更新设备

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/{id}` |
| 方法 | PUT |
| 认证 | 是 |

**请求体**:
```json
{
  "name": "更新设备",
  "code": "D002",
  "status": 1,
  "location": "温室C区",
  "deviceTypeId": 2,
  "description": "更新描述"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {...}
}
```

### 5.6 删除设备

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/{id}` |
| 方法 | DELETE |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 6. 设备类型模块 (DeviceType)

### 6.1 获取设备类型列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/type` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": [...]
}
```

### 6.2 获取设备类型详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/type/{id}` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 6.3 创建设备类型

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/type` |
| 方法 | POST |
| 认证 | 是 |

**请求体**:
```json
{
  "name": "传感器",
  "description": "环境传感器类型"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {...}
}
```

### 6.4 更新设备类型

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/type/{id}` |
| 方法 | PUT |
| 认证 | 是 |

**请求体**:
```json
{
  "name": "传感器类型",
  "description": "更新描述"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {...}
}
```

### 6.5 删除设备类型

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/type/{id}` |
| 方法 | DELETE |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 7. 农作物模块 (Crop)

### 7.1 获取农作物选项

| 属性 | 值 |
|------|-----|
| 路径 | `/api/crop/options` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"value": 1, "label": "水稻"},
    {"value": 2, "label": "小麦"},
    {"value": 3, "label": "蔬菜"}
  ]
}
```

---

## 8. 种植记录模块 (PlantRecord)

### 8.1 获取种植记录列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/plant/list` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页条数 |
| keyword | string | 否 | - | 搜索关键词 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

### 8.2 获取种植记录详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/plant/{id}` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 8.3 创建种植记录

| 属性 | 值 |
|------|-----|
| 路径 | `/api/plant` |
| 方法 | POST |
| 认证 | 是 |

**请求体**:
```json
{
  "cropId": 1,
  "cropName": "水稻",
  "plantDate": "2024-03-01",
  "expectedHarvestDate": "2024-06-01",
  "area": 10.5,
  "status": "growing"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {...}
}
```

### 8.4 更新种植记录

| 属性 | 值 |
|------|-----|
| 路径 | `/api/plant/{id}` |
| 方法 | PUT |
| 认证 | 是 |

**请求体**:
```json
{
  "cropId": 1,
  "cropName": "水稻",
  "plantDate": "2024-03-01",
  "expectedHarvestDate": "2024-06-15",
  "area": 10.5,
  "status": "harvested"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {...}
}
```

### 8.5 删除种植记录

| 属性 | 值 |
|------|-----|
| 路径 | `/api/plant/{id}` |
| 方法 | DELETE |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 9. 环境监测模块 (Monitor)

### 9.1 获取当前环境数据

| 属性 | 值 |
|------|-----|
| 路径 | `/api/monitor/environment` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "temperature": {
      "value": 25.6,
      "unit": "°C",
      "status": "normal",
      "range": {"min": 0, "max": 50, "optimal": {"min": 18, "max": 28}}
    },
    "humidity": {
      "value": 65,
      "unit": "%",
      "status": "normal",
      "range": {"min": 0, "max": 100, "optimal": {"min": 40, "max": 80}}
    },
    "co2": {
      "value": 450,
      "unit": "ppm",
      "status": "normal",
      "range": {"min": 0, "max": 2000, "optimal": {"min": 300, "max": 800}}
    },
    "soilPh": {
      "value": 6.8,
      "unit": "",
      "status": "normal",
      "range": {"min": 0, "max": 14, "optimal": {"min": 5.5, "max": 7.5}}
    }
  }
}
```

### 9.2 获取历史数据

| 属性 | 值 |
|------|-----|
| 路径 | `/api/monitor/history` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| hours | int | 否 | 24 | 最近小时数 |
| startTime | string | 否 | - | 开始时间（与endTime配合） |
| endTime | string | 否 | - | 结束时间 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"time": "08:00", "temperature": 24.5, "humidity": 60, "co2": 430},
    {"time": "09:00", "temperature": 25.2, "humidity": 62, "co2": 440}
  ]
}
```

### 9.3 分页查询环境数据

| 属性 | 值 |
|------|-----|
| 路径 | `/api/monitor/data/list` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页条数 |
| minTemp | double | 否 | - | 最低温度 |
| maxTemp | double | 否 | - | 最高温度 |
| minHumidity | double | 否 | - | 最低湿度 |
| maxHumidity | double | 否 | - | 最高湿度 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 9.4 获取单个数据详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/monitor/data/{id}` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 9.5 添加环境数据

| 属性 | 值 |
|------|-----|
| 路径 | `/api/monitor/data` |
| 方法 | POST |
| 认证 | 是 |

**请求体**:
```json
{
  "temperature": 26.5,
  "humidity": 68,
  "co2": 460,
  "soilPh": 6.7
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {...}
}
```

### 9.6 删除环境数据

| 属性 | 值 |
|------|-----|
| 路径 | `/api/monitor/data/{id}` |
| 方法 | DELETE |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 9.7 获取每日平均值图表数据

| 属性 | 值 |
|------|-----|
| 路径 | `/api/monitor/chart/daily` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| days | int | 否 | 7 | 最近天数 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"date": "2024-07-01", "avgTemperature": 25.1, "avgHumidity": 65, "avgCo2": 445},
    {"date": "2024-07-02", "avgTemperature": 25.8, "avgHumidity": 67, "avgCo2": 450}
  ]
}
```

### 9.8 获取每小时平均值图表数据

| 属性 | 值 |
|------|-----|
| 路径 | `/api/monitor/chart/hourly` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| hours | int | 否 | 24 | 最近小时数 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"time": "2024-07-06 08:00:00", "temperature": 24.5, "humidity": 60, "co2": 430},
    {"time": "2024-07-06 09:00:00", "temperature": 25.2, "humidity": 62, "co2": 440}
  ]
}
```

### 9.9 获取实时图表数据

| 属性 | 值 |
|------|-----|
| 路径 | `/api/monitor/chart/realtime` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"time": "08:00", "temperature": 24.5, "humidity": 60, "co2": 430},
    {"time": "09:00", "temperature": 25.2, "humidity": 62, "co2": 440}
  ]
}
```

---

## 10. 统计模块 (Statistics)

### 10.1 获取产量统计

| 属性 | 值 |
|------|-----|
| 路径 | `/api/statistics/yield` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 10.2 获取面积统计

| 属性 | 值 |
|------|-----|
| 路径 | `/api/statistics/area` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

---

## 11. 文件上传模块 (Upload)

### 11.1 上传图片

| 属性 | 值 |
|------|-----|
| 路径 | `/api/upload/image` |
| 方法 | POST |
| 认证 | 是 |

**请求体**: `multipart/form-data`
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 图片文件 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "/api/uploads/xxx.jpg"
  }
}
```

### 11.2 获取上传的图片

| 属性 | 值 |
|------|-----|
| 路径 | `/api/uploads/{filename}` |
| 方法 | GET |
| 认证 | 否 |

**成功响应**: 返回图片二进制数据

---

## 12. 帮助中心模块 (Help)

### 12.1 获取FAQ列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/faq` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| category | string | 否 | 分类筛选 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "question": "如何添加设备？",
      "answer": "在设备管理页面点击添加按钮...",
      "category": "设备",
      "sortOrder": 1,
      "isActive": 1
    }
  ]
}
```

### 12.2 获取FAQ分类

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/faq/categories` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": ["设备", "种植", "环境", "其他"]
}
```

### 12.3 获取FAQ详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/faq/{id}` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 12.4 创建FAQ

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/faq` |
| 方法 | POST |
| 认证 | 是 |

**请求体**:
```json
{
  "question": "新问题",
  "answer": "新答案",
  "category": "设备",
  "sortOrder": 2,
  "isActive": 1
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {...}
}
```

### 12.5 更新FAQ

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/faq/{id}` |
| 方法 | PUT |
| 认证 | 是 |

**请求体**:
```json
{
  "question": "更新问题",
  "answer": "更新答案",
  "category": "种植",
  "sortOrder": 1,
  "isActive": 1
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {...}
}
```

### 12.6 删除FAQ

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/faq/{id}` |
| 方法 | DELETE |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 12.7 获取客服人员列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/staff` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "张三",
      "position": "客服专员",
      "phone": "13800138001",
      "email": "zhangsan@smartfarm.com",
      "avatar": null,
      "isActive": 1
    }
  ]
}
```

### 12.8 获取客服人员详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/staff/{id}` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 12.9 创建客服人员

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/staff` |
| 方法 | POST |
| 认证 | 是 |

**请求体**:
```json
{
  "name": "李四",
  "position": "客服主管",
  "phone": "13800138002",
  "email": "lisi@smartfarm.com",
  "avatar": null,
  "isActive": 1
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {...}
}
```

### 12.10 更新客服人员

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/staff/{id}` |
| 方法 | PUT |
| 认证 | 是 |

**请求体**:
```json
{
  "name": "李四",
  "position": "高级客服",
  "phone": "13800138003",
  "email": "lisi@smartfarm.com",
  "avatar": null,
  "isActive": 1
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {...}
}
```

### 12.11 删除客服人员

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/staff/{id}` |
| 方法 | DELETE |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 12.12 获取FAQ轮播数据

| 属性 | 值 |
|------|-----|
| 路径 | `/api/help/carousel` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "currentIndex": 0,
    "total": 5,
    "faqs": [...]
  }
}
```

---

## 13. 意见反馈模块 (Feedback)

### 13.1 获取反馈列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/feedback/list` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页条数 |
| keyword | string | 否 | - | 搜索关键词 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 15,
    "page": 1,
    "size": 10
  }
}
```

### 13.2 获取当前用户反馈

| 属性 | 值 |
|------|-----|
| 路径 | `/api/feedback/my` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页条数 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 3,
    "page": 1,
    "size": 10
  }
}
```

### 13.3 获取反馈详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/feedback/{id}` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "username": "admin",
    "email": "admin@smartfarm.com",
    "phone": "13800138000",
    "content": "反馈内容",
    "type": "suggestion",
    "status": "pending",
    "reply": null,
    "createTime": "2024-07-06 10:00:00",
    "replyTime": null
  }
}
```

### 13.4 创建反馈

| 属性 | 值 |
|------|-----|
| 路径 | `/api/feedback` |
| 方法 | POST |
| 认证 | 是 |

**请求体**:
```json
{
  "content": "这是一条反馈内容",
  "type": "suggestion",
  "email": "test@smartfarm.com",
  "phone": "13900139000"
}
```

**type可选值**: `suggestion`（功能建议）、`bug`（问题反馈）、`complaint`（投诉）、`other`（其他）

**成功响应** (200):
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {...}
}
```

### 13.5 更新反馈

| 属性 | 值 |
|------|-----|
| 路径 | `/api/feedback/{id}` |
| 方法 | PUT |
| 认证 | 是 |

**请求体**:
```json
{
  "content": "更新后的反馈内容",
  "type": "bug",
  "email": "update@smartfarm.com",
  "phone": "13700137000"
}
```

**限制**: 只能修改自己的反馈，且未回复的反馈才能修改

**成功响应** (200):
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {...}
}
```

### 13.6 回复反馈

| 属性 | 值 |
|------|-----|
| 路径 | `/api/feedback/{id}/reply` |
| 方法 | POST |
| 认证 | 是 |
| 权限 | super_admin 或 admin |

**请求体**:
```json
{
  "reply": "感谢您的反馈，我们会尽快处理。"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "回复成功",
  "data": {...}
}
```

### 13.7 删除反馈

| 属性 | 值 |
|------|-----|
| 路径 | `/api/feedback/{id}` |
| 方法 | DELETE |
| 认证 | 是 |

**限制**: 只能删除自己的反馈或管理员可删除所有反馈

**成功响应** (200):
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 13.8 获取客服信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/feedback/service-info` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hotline": "400-888-8888",
    "workTime": "周一至周日 9:00-21:00",
    "email": "support@smartfarm.com",
    "staffList": [...]
  }
}
```

### 13.9 获取反馈类型

| 属性 | 值 |
|------|-----|
| 路径 | `/api/feedback/types` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"value": "suggestion", "label": "功能建议"},
    {"value": "bug", "label": "问题反馈"},
    {"value": "complaint", "label": "投诉"},
    {"value": "other", "label": "其他"}
  ]
}
```

---

## 14. 设备控制模块 (DeviceControl)

### 14.1 获取控制日志

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/control/logs` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页条数 |
| deviceId | long | 否 | - | 设备ID筛选 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

### 14.2 获取当前用户控制日志

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/control/logs/my` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页条数 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 5,
    "page": 1,
    "size": 10
  }
}
```

### 14.3 获取控制日志详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/control/logs/{id}` |
| 方法 | GET |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "deviceId": 1,
    "deviceName": "温湿度传感器",
    "deviceCode": "D001",
    "userId": 1,
    "action": "turn_on",
    "statusBefore": "关闭",
    "statusAfter": "开启",
    "remark": null,
    "createTime": "2024-07-06 10:30:00"
  }
}
```

### 14.4 设备控制（通用接口）

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/control/action` |
| 方法 | POST |
| 认证 | 是 |

**请求体**:
```json
{
  "deviceId": 1,
  "action": "turn_on",
  "remark": "手动开启"
}
```

**action可选值**: `turn_on`（开启）、`turn_off`（关闭）、`toggle`（切换）

**成功响应** (200):
```json
{
  "code": 200,
  "message": "设备控制成功",
  "data": {
    "deviceId": 1,
    "deviceName": "温湿度传感器",
    "action": "turn_on",
    "statusBefore": "关闭",
    "statusAfter": "开启",
    "message": "设备控制成功"
  }
}
```

### 14.5 开启设备

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/control/turn-on/{deviceId}` |
| 方法 | POST |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "设备已开启",
  "data": {...}
}
```

### 14.6 关闭设备

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/control/turn-off/{deviceId}` |
| 方法 | POST |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "设备已关闭",
  "data": {...}
}
```

### 14.7 切换设备状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/control/toggle/{deviceId}` |
| 方法 | POST |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "设备状态已切换",
  "data": {...}
}
```

### 14.8 删除控制日志

| 属性 | 值 |
|------|-----|
| 路径 | `/api/device/control/logs/{id}` |
| 方法 | DELETE |
| 认证 | 是 |

**限制**: 只能删除自己的日志或管理员可删除所有日志

**成功响应** (200):
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 15. 聊天模块 (Chat)

### 15.1 发送消息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/chat/send` |
| 方法 | POST |
| 认证 | 是 |

**请求体**:
```json
{
  "content": "你好，我有个问题"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 15.2 获取消息历史

| 属性 | 值 |
|------|-----|
| 路径 | `/api/chat/history` |
| 方法 | GET |
| 认证 | 是 |

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 20 | 每页条数 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 50,
    "page": 1,
    "size": 20
  }
}
```

### 15.3 清空消息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/chat/clear` |
| 方法 | DELETE |
| 认证 | 是 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 16. 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或登录过期 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 17. 白名单接口

以下接口无需认证即可访问：

| 接口 | 方法 |
|------|------|
| `/login` | POST |
| `/register` | POST |
| `/api/uploads/*` | GET |

---

## 响应格式说明

所有接口统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码 |
| message | string | 响应消息 |
| data | object | 响应数据（可为null） |