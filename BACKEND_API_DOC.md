# 智慧农业助手 - 后端 API 文档

## 概述

本文档定义了智慧农业助手前端项目所需的后端 API 接口规范。前端项目使用 Vue 3 + Vite 构建，后端接口应遵循 RESTful 设计原则。

## 基础配置

### 基础 URL
- 开发环境: `http://localhost:3000`
- 生产环境: 配置于环境变量 `VITE_APP_BASE_API`

### 响应格式

所有接口统一返回 JSON 格式，包含以下字段：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | number | 状态码，200/0 表示成功，其他表示失败 |
| message | string | 提示信息 |
| data | any | 响应数据 |

### 认证机制

使用 JWT Token 认证，Token 通过 `Authorization` 请求头传递：

```
Authorization: Bearer <token>
```

**Token 失效处理**：后端返回 401 状态码时，前端自动清除 Token 并跳转到登录页。

---

## 接口列表

### 1. 用户认证模块

#### 1.1 用户登录

- **路径**: `POST /login`
- **描述**: 用户登录，获取 Token
- **请求体**:

```json
{
  "email": "string, 必填, 用户邮箱",
  "password": "string, 必填, 用户密码"
}
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "string, JWT Token",
    "user": {
      "id": "number, 用户ID",
      "username": "string, 用户名",
      "email": "string, 邮箱",
      "role": "string, 角色: super_admin | farmer"
    }
  }
}
```

- **失败响应** (code: 401):

```json
{
  "code": 401,
  "message": "邮箱或密码错误"
}
```

#### 1.2 用户注册

- **路径**: `POST /register`
- **描述**: 新用户注册
- **请求体**:

```json
{
  "username": "string, 必填, 用户名",
  "email": "string, 必填, 用户邮箱",
  "password": "string, 必填, 密码",
  "confirmPassword": "string, 必填, 确认密码"
}
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": "number, 新用户ID",
    "username": "string, 用户名",
    "email": "string, 邮箱",
    "role": "string, 默认角色: farmer"
  }
}
```

- **失败响应**:

```json
{
  "code": 400,
  "message": "邮箱已被注册"
}
```

#### 1.3 获取用户信息

- **路径**: `GET /user/info`
- **描述**: 获取当前登录用户信息（需携带 Token）
- **请求头**:

```
Authorization: Bearer <token>
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "number, 用户ID",
    "username": "string, 用户名",
    "email": "string, 邮箱",
    "role": "string, 角色",
    "phone": "string, 电话号码",
    "avatar": "string, 头像URL",
    "farmProvince": "string, 农场所在省份",
    "farmCity": "string, 农场所在城市",
    "farmDistrict": "string, 农场所在区县",
    "farmAddress": "string, 农场详细地址",
    "homeAddress": "string, 家庭住址",
    "personalInfo": "string, 个人简介"
  }
}
```

---

### 2. 环境监测模块

#### 2.1 获取当前环境数据

- **路径**: `GET /api/monitor/environment`
- **描述**: 获取当前环境监测数据（温度、湿度、CO2、土壤pH）
- **请求头**:

```
Authorization: Bearer <token>
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "temperature": {
      "value": "number, 当前温度值",
      "unit": "string, 单位: °C",
      "status": "string, 状态: normal | warning | danger",
      "range": {
        "min": "number, 最小值",
        "max": "number, 最大值",
        "optimal": {
          "min": "number, 最佳最小值",
          "max": "number, 最佳最大值"
        }
      }
    },
    "humidity": {
      "value": "number, 当前湿度值",
      "unit": "string, 单位: %",
      "status": "string, 状态",
      "range": {
        "min": "number",
        "max": "number",
        "optimal": {
          "min": "number",
          "max": "number"
        }
      }
    },
    "co2": {
      "value": "number, 当前CO2值",
      "unit": "string, 单位: ppm",
      "status": "string, 状态",
      "range": {
        "min": "number",
        "max": "number",
        "optimal": {
          "min": "number",
          "max": "number"
        }
      }
    },
    "soilPh": {
      "value": "number, 当前土壤pH值",
      "unit": "string, 单位: ''",
      "status": "string, 状态",
      "range": {
        "min": "number",
        "max": "number",
        "optimal": {
          "min": "number",
          "max": "number"
        }
      }
    }
  }
}
```

#### 2.2 获取环境历史数据

- **路径**: `GET /api/monitor/history`
- **描述**: 获取指定时间段的环境历史数据（默认24小时）
- **请求头**:

```
Authorization: Bearer <token>
```

- **请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| hours | number | 否 | 查询小时数，默认24 |
| startTime | string | 否 | 开始时间，格式: YYYY-MM-DD HH:mm:ss |
| endTime | string | 否 | 结束时间，格式: YYYY-MM-DD HH:mm:ss |

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "time": "string, 时间点: HH:mm",
      "temperature": "number, 温度值",
      "humidity": "number, 湿度值",
      "co2": "number, CO2值"
    }
  ]
}
```

---

### 3. 统计分析模块

#### 3.1 获取农作物产量数据

- **路径**: `GET /api/statistics/production`
- **描述**: 获取农作物产量统计数据
- **请求头**:

```
Authorization: Bearer <token>
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalProduction": "number, 总产量(kg)",
    "unit": "string, 单位: kg",
    "trend": [
      {
        "month": "string, 月份: 1月",
        "value": "number, 当月产量"
      }
    ],
    "crops": [
      {
        "name": "string, 农作物名称",
        "production": "number, 产量",
        "percentage": "number, 占比(%)"
      }
    ]
  }
}
```

#### 3.2 获取种植面积数据

- **路径**: `GET /api/statistics/area`
- **描述**: 获取种植面积分布数据
- **请求头**:

```
Authorization: Bearer <token>
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalArea": "number, 总面积(亩)",
    "unit": "string, 单位: 亩",
    "crops": [
      {
        "name": "string, 农作物名称",
        "area": "number, 种植面积",
        "color": "string, 图表颜色: #5470c6"
      }
    ],
    "growthStage": [
      {
        "stage": "string, 生长阶段",
        "percentage": "number, 占比(%)"
      }
    ]
  }
}
```

---

### 4. 用户管理模块（管理员）

#### 4.1 获取用户列表

- **路径**: `GET /api/admin/users`
- **描述**: 获取系统用户列表（仅超级管理员）
- **请求头**:

```
Authorization: Bearer <token>
```

- **请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码，默认1 |
| size | number | 否 | 每页数量，默认10 |
| keyword | string | 否 | 搜索关键词 |

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "number, 用户ID",
        "username": "string, 用户名",
        "email": "string, 邮箱",
        "role": "string, 角色",
        "status": "string, 状态: active | inactive",
        "createTime": "string, 创建时间"
      }
    ],
    "total": "number, 总记录数",
    "page": "number, 当前页码",
    "size": "number, 每页数量"
  }
}
```

#### 4.2 更新用户状态

- **路径**: `PUT /api/admin/users/:id/status`
- **描述**: 更新用户状态（禁用/启用）
- **请求头**:

```
Authorization: Bearer <token>
```

- **请求体**:

```json
{
  "status": "string, 必填, active | inactive"
}
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

### 5. 个人信息模块

#### 5.1 更新用户信息

- **路径**: `PUT /api/user/profile`
- **描述**: 更新用户个人信息
- **请求头**:

```
Authorization: Bearer <token>
```

- **请求体**:

```json
{
  "username": "string, 用户名",
  "phone": "string, 电话号码",
  "email": "string, 邮箱",
  "farmProvince": "string, 农场省份",
  "farmCity": "string, 农场城市",
  "farmDistrict": "string, 农场区县",
  "farmAddress": "string, 农场详细地址",
  "homeAddress": "string, 家庭住址",
  "personalInfo": "string, 个人简介"
}
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "信息更新成功",
  "data": null
}
```

---

### 6. AI 聊天模块

#### 6.1 发送消息

- **路径**: `POST /api/chat/send`
- **描述**: 用户发送消息给AI助手，接收AI回复及表情指令
- **请求头**:

```
Authorization: Bearer <token>
```

- **请求体**:

```json
{
  "content": "string, 必填, 用户发送的消息内容"
}
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": "string, AI助手的回复内容",
    "expression": "string, AI决定的表情类型，可选值见下表"
  }
}
```

**表情类型说明**:

| 表情值 | 说明 | 触发行为 |
|--------|------|----------|
| `happy` | 开心 | 点击交互 |
| `sad` | 悲伤 | 悬停交互 |
| `surprised` | 惊讶 | 摇晃效果 |
| `blink` | 眨眼 | 眨眼动作 |
| `shy` | 害羞 | 触摸交互 |
| `normal` | 正常 | 空闲状态 |
| `talking` | 说话 | 点击交互 |
| `thinking` | 思考 | 悬停交互 |

#### 6.2 获取聊天历史

- **路径**: `GET /api/chat/history`
- **描述**: 获取当前用户的聊天历史记录
- **请求头**:

```
Authorization: Bearer <token>
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "number, 消息ID",
      "role": "string, 角色: user | bot",
      "content": "string, 消息内容",
      "expression": "string, AI表情（仅bot消息有）",
      "timestamp": "string, 消息时间"
    }
  ]
}
```

#### 6.3 清空聊天历史

- **路径**: `POST /api/chat/clear`
- **描述**: 清空当前用户的聊天历史记录
- **请求头**:

```
Authorization: Bearer <token>
```

- **成功响应** (code: 200):

```json
{
  "code": 200,
  "message": "聊天记录已清空",
  "data": null
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或Token失效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 数据模型示例

### 用户模型

```json
{
  "id": 1,
  "username": "张农场主",
  "email": "farmer@example.com",
  "password": "string, 加密存储",
  "role": "farmer",
  "status": "active",
  "phone": "138****8888",
  "avatar": "",
  "farmProvince": "浙江省",
  "farmCity": "杭州市",
  "farmDistrict": "余杭区",
  "farmAddress": "阳光大道888号",
  "homeAddress": "杭州市西湖区文三路123号",
  "personalInfo": "从事农业生产15年",
  "createTime": "2024-01-01 10:00:00",
  "updateTime": "2024-01-01 10:00:00"
}
```

### 环境监测数据模型

```json
{
  "id": 1,
  "userId": 1,
  "temperature": 25.5,
  "humidity": 65,
  "co2": 450,
  "soilPh": 6.8,
  "recordTime": "2024-01-01 10:00:00"
}
```

### 农作物产量数据模型

```json
{
  "id": 1,
  "userId": 1,
  "cropName": "小麦",
  "production": 45200,
  "area": 800,
  "year": 2024,
  "month": 6,
  "createTime": "2024-06-01 00:00:00"
}
```