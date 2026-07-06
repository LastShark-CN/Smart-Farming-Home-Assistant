# Smart Farming Home Assistant

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Vue 3](https://img.shields.io/badge/Vue-3.x-brightgreen.svg)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-8.x-purple.svg)](https://vitejs.dev/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.x-blue.svg)](https://element-plus.org/)

基于 Vue 3 + Vite 的智能农业管理前端项目，提供完整的农场管理、环境监测、AI助手等功能。

## 版本选择

本项目提供两个版本分支，您可以根据需求选择：

| 分支 | 说明 | 特性 |
|------|------|------|
| `main` | 🎯 **彩蛋版本（推荐）** | 包含丰富的彩蛋内容和隐藏功能 |
| `no-easter-egg` | 🧹 **纯净版本** | 无任何彩蛋，仅保留核心功能 |

### 选择版本

```bash
# 使用彩蛋版本（默认）
git clone https://github.com/LastShark-CN/Smart-Farming-Home-Assistant.git

# 使用纯净版本
git clone -b no-easter-egg https://github.com/LastShark-CN/Smart-Farming-Home-Assistant.git

# 或在本地切换分支
git checkout no-easter-egg
```

## 功能特性

### 🌾 农场管理
- 我的农场 - 查看农场基本信息
- 设备管理 - 管理农业设备和传感器
- 作物管理 - 管理各类农作物信息

### 📊 数据监测
- 环境监测仪表盘 - 实时显示温度、湿度、CO2、土壤pH值
- 统计图表 - 产量趋势、种植面积分布、24小时环境变化
- 数据可视化 - ECharts图表展示

### 📝 种植记录
- 每日种植记录 - Markdown格式编写
- 作物照片上传 - 支持多图上传
- 分类筛选 - 按作物/日期筛选记录

### 🤖 AI助手
- AI聊天 - 智能农业问答
- Live2D模型 - 可爱的虚拟助手形象
- 表情互动 - 根据AI回复自动切换表情

### 👥 用户管理
- 用户认证 - JWT Token登录
- 角色权限 - 超级管理员/农场主
- 个人信息 - 编辑个人资料和农场地址

### 🎨 视觉效果
- 粒子背景动画
- 3D场景展示
- 玻璃态UI设计
- 响应式布局

### 🎁 彩蛋内容（仅彩蛋版本）

> 💡 彩蛋版本包含大量隐藏的交互彩蛋和视觉特效，为您的使用体验增添乐趣！

| 彩蛋类型 | 说明 |
|----------|------|
| 🎭 特殊动画 | 特定操作触发的独特动画效果 |
| 🖼️ 隐藏页面 | 神秘的彩蛋页面，包含独特的视觉展示 |
| 🎨 特效渲染 | 自定义字体、特殊图像渲染效果 |
| ⚡ 交互惊喜 | 意想不到的交互反馈和视觉效果 |

**提示**：尝试探索不同的页面和操作，说不定会发现惊喜哦！

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue | 3.x |
| 构建工具 | Vite | 8.x |
| 组件库 | Element Plus | 2.x |
| 路由 | Vue Router | 5.x |
| 状态管理 | Pinia | 3.x |
| HTTP客户端 | Axios | 1.x |
| 图表库 | ECharts | 6.x |
| Markdown编辑器 | md-editor-v3 | 4.x |
| Live2D | vue-live2d | 1.x |
| 3D渲染 | Three.js | 0.x |
| 粒子效果 | tsparticles | 4.x |

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 项目结构

```
src/
├── api/                    # API接口模块
│   ├── chat.js             # AI聊天接口
│   ├── farmData.js         # 农场数据接口
│   └── plantRecord.js      # 种植记录接口
├── assets/                 # 静态资源
│   ├── hero.png
│   ├── vite.svg
│   └── vue.svg
├── components/             # 公共组件
│   ├── ParticleBackground.vue  # 粒子背景
│   └── ThreeScene.vue          # 3D场景
├── mock/                   # 模拟数据
│   └── farmData.js         # 农场模拟数据
├── router/                 # 路由配置
│   └── index.js
├── stores/                 # Pinia状态管理
│   └── user.js             # 用户状态
├── utils/                  # 工具函数
│   └── request.js          # Axios封装
├── views/                  # 页面组件
│   ├── admin/              # 管理员页面
│   │   ├── Settings.vue
│   │   └── UserManagement.vue
│   ├── farmer/             # 农场主页面
│   │   ├── CropManagement.vue
│   │   ├── DeviceManagement.vue
│   │   └── MyFarm.vue
│   ├── AIChat.vue          # AI聊天页面
│   ├── Dashboard.vue       # 仪表盘页面
│   ├── EasterEgg.vue       # 彩蛋页面（仅彩蛋版本）
│   ├── HomePage.vue        # 首页
│   ├── LoginRegister.vue   # 登录注册页
│   ├── PlantRecord.vue     # 种植记录页面
│   └── Profile.vue         # 个人信息页面
├── App.vue
├── main.js
└── style.css
```

## 路由配置

| 路径 | 名称 | 权限 | 说明 |
|------|------|------|------|
| `/` | home | 公开 | 首页 |
| `/login` | login | 公开 | 登录/注册 |
| `/dashboard` | dashboard | 登录用户 | 仪表盘 |
| `/profile` | profile | 登录用户 | 个人信息 |
| `/ai-chat` | aiChat | 登录用户 | AI助手 |
| `/farmer/my-farm` | myFarm | 农场主 | 我的农场 |
| `/farmer/devices` | devices | 农场主 | 设备管理 |
| `/farmer/crops` | crops | 农场主 | 作物管理 |
| `/farmer/plant-records` | plantRecords | 农场主 | 种植记录 |
| `/admin/users` | adminUsers | 超级管理员 | 用户管理 |
| `/admin/settings` | adminSettings | 超级管理员 | 系统设置 |
| `/easter-egg` | easterEgg | 公开 | 彩蛋页面（仅彩蛋版本） |

## API文档

后端API接口文档请查看 [BACKEND_API_DOC.md](BACKEND_API_DOC.md)

## 开发说明

### 环境变量

创建 `.env.development` 文件配置开发环境：

```env
VITE_APP_BASE_API=http://localhost:3000
```

### 认证机制

项目使用JWT Token认证，Token存储在localStorage中，通过Axios请求拦截器自动添加到请求头。

```
Authorization: Bearer <token>
```

### 状态管理

使用Pinia管理全局状态，主要store包括：
- `user` - 用户信息、登录状态、权限管理

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 贡献

欢迎提交Issue和Pull Request！