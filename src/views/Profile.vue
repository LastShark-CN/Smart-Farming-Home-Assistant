<script setup>import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../stores/user';
import { ElMessage } from 'element-plus';
import ParticleBackground from '../components/ParticleBackground.vue';
import ThreeScene from '../components/ThreeScene.vue';
const router = useRouter();
const userStore = useUserStore();
const isEditing = ref(false);
const form = reactive({
 username: '',
 avatar: '',
 phone: '',
 email: '',
 farmProvince: '',
 farmCity: '',
 farmDistrict: '',
 farmAddress: '',
 homeAddress: '',
 personalInfo: ''
});
const provinces = [
 { label: '浙江省', value: '浙江省', cities: ['杭州市', '宁波市', '温州市', '嘉兴市', '湖州市'] },
 { label: '江苏省', value: '江苏省', cities: ['南京市', '苏州市', '无锡市', '常州市', '徐州市'] },
 { label: '山东省', value: '山东省', cities: ['济南市', '青岛市', '烟台市', '潍坊市', '临沂市'] },
 { label: '河南省', value: '河南省', cities: ['郑州市', '洛阳市', '开封市', '新乡市', '许昌市'] },
 { label: '四川省', value: '四川省', cities: ['成都市', '绵阳市', '德阳市', '南充市', '达州市'] },
 { label: '广东省', value: '广东省', cities: ['广州市', '深圳市', '东莞市', '佛山市', '中山市'] }
];
const cities = ref([]);
const districts = ref([]);
const districtMap = {
 '杭州市': [{ label: '上城区', value: '上城区' }, { label: '下城区', value: '下城区' }, { label: '西湖区', value: '西湖区' }, { label: '余杭区', value: '余杭区' }, { label: '萧山区', value: '萧山区' }],
 '宁波市': [{ label: '海曙区', value: '海曙区' }, { label: '江北区', value: '江北区' }, { label: '北仑区', value: '北仑区' }, { label: '镇海区', value: '镇海区' }],
 '南京市': [{ label: '玄武区', value: '玄武区' }, { label: '秦淮区', value: '秦淮区' }, { label: '鼓楼区', value: '鼓楼区' }, { label: '建邺区', value: '建邺区' }],
 '成都市': [{ label: '锦江区', value: '锦江区' }, { label: '青羊区', value: '青羊区' }, { label: '金牛区', value: '金牛区' }, { label: '武侯区', value: '武侯区' }],
 '广州市': [{ label: '天河区', value: '天河区' }, { label: '越秀区', value: '越秀区' }, { label: '荔湾区', value: '荔湾区' }, { label: '海珠区', value: '海珠区' }],
 '深圳市': [{ label: '福田区', value: '福田区' }, { label: '罗湖区', value: '罗湖区' }, { label: '南山区', value: '南山区' }, { label: '宝安区', value: '宝安区' }]
};
onMounted(() => {
 loadUserInfo();
});
async function loadUserInfo() {
 const mockData = {
 username: userStore.username || '张农场主',
 avatar: '',
 phone: '138****8888',
 email: userStore.email || 'farmer@example.com',
 farmProvince: '浙江省',
 farmCity: '杭州市',
 farmDistrict: '余杭区',
 farmAddress: '阳光大道888号',
 homeAddress: '杭州市西湖区文三路123号',
 personalInfo: '从事农业生产15年，专注于智慧农业技术应用，目前经营50亩有机农场。'
 };
 Object.assign(form, mockData);
 updateCities();
 updateDistricts();
}
function updateCities() {
 const province = provinces.find(p => p.value === form.farmProvince);
 cities.value = province ? province.cities.map(city => ({ label: city, value: city })) : [];
 form.farmCity = '';
 districts.value = [];
 form.farmDistrict = '';
}
function updateDistricts() {
 districts.value = districtMap[form.farmCity] || [];
 form.farmDistrict = '';
}
function startEdit() {
 isEditing.value = true;
}
async function saveEdit() {
 try {
 ElMessage.success('信息修改成功');
 isEditing.value = false;
 }
 catch (error) {
 ElMessage.error('修改失败');
 }
}
function cancelEdit() {
 isEditing.value = false;
 loadUserInfo();
}
function handleLogout() {
 userStore.logout();
 ElMessage.success('退出成功');
 router.push('/login');
}
function goBack() {
 router.push('/dashboard');
}
const adminMenus = [
 { name: '用户管理', path: '/admin/users', icon: '👥' },
 { name: '系统设置', path: '/admin/settings', icon: '⚙️' }
];
const farmerMenus = [
 { name: '我的农场', path: '/farmer/my-farm', icon: '🌾' },
 { name: '设备管理', path: '/farmer/devices', icon: '📡' }
];
</script>

<template>
  <div class="page-container">
    <ParticleBackground />
    <ThreeScene />

    <nav class="navbar">
      <div class="nav-content">
        <div class="logo" @click="router.push('/')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
            <path d="M2 17l10 5 10-5"></path>
            <path d="M2 12l10 5 10-5"></path>
          </svg>
          <span>智慧农业</span>
        </div>
        <div class="nav-links">
          <button
            v-for="menu in (userStore.role === 'super_admin' ? adminMenus : farmerMenus)"
            :key="menu.name"
            class="menu-btn"
            @click="router.push(menu.path)"
          >
            <span>{{ menu.icon }}</span>
            <span>{{ menu.name }}</span>
          </button>
          <button class="logout-btn" @click="handleLogout">退出登录</button>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <header class="header">
        <button class="back-btn" @click="goBack">← 返回</button>
        <h1>个人信息</h1>
        <div class="header-actions">
          <button v-if="!isEditing" class="edit-btn" @click="startEdit">
            <span>✏️</span>
            <span>修改信息</span>
          </button>
          <template v-else>
            <button class="save-btn" @click="saveEdit">保存</button>
            <button class="cancel-btn" @click="cancelEdit">取消</button>
          </template>
        </div>
      </header>

      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-container">
            <div class="avatar" :style="{ background: `linear-gradient(135deg, #aa3bff, #00d4ff)` }">
              <span v-if="!form.avatar">{{ form.username.charAt(0) }}</span>
              <img v-else :src="form.avatar" alt="头像" />
            </div>
            <input v-if="isEditing" type="file" class="avatar-input" accept="image/*" />
          </div>
          <div class="profile-title">
            <h2>{{ form.username }}</h2>
            <p class="role-tag">{{ userStore.role === 'super_admin' ? '超级管理员' : '农场主' }}</p>
          </div>
        </div>

        <div class="form-section">
          <h3>基本信息</h3>
          <div class="form-grid">
            <div class="form-item">
              <label>电话号码</label>
              <el-input v-model="form.phone" :disabled="!isEditing" placeholder="请输入电话号码" />
            </div>
            <div class="form-item">
              <label>邮箱</label>
              <el-input v-model="form.email" :disabled="!isEditing" placeholder="请输入邮箱" />
            </div>
          </div>
        </div>

        <div class="form-section">
          <h3>农场地址</h3>
          <div class="form-grid">
            <div class="form-item">
              <label>省份</label>
              <el-select v-model="form.farmProvince" :disabled="!isEditing" placeholder="请选择省份" @change="updateCities">
                <el-option v-for="province in provinces" :key="province.value" :label="province.label" :value="province.value" />
              </el-select>
            </div>
            <div class="form-item">
              <label>城市</label>
              <el-select v-model="form.farmCity" :disabled="!isEditing" placeholder="请选择城市" @change="updateDistricts">
                <el-option v-for="city in cities" :key="city.value" :label="city.label" :value="city.value" />
              </el-select>
            </div>
            <div class="form-item">
              <label>区县</label>
              <el-select v-model="form.farmDistrict" :disabled="!isEditing" placeholder="请选择区县">
                <el-option v-for="district in districts" :key="district.value" :label="district.label" :value="district.value" />
              </el-select>
            </div>
          </div>
          <div class="form-item full-width">
            <label>详细地址</label>
            <el-input v-model="form.farmAddress" :disabled="!isEditing" placeholder="请输入农场详细地址" />
          </div>
        </div>

        <div class="form-section">
          <h3>家庭住址</h3>
          <div class="form-item full-width">
            <label>详细地址</label>
            <el-input v-model="form.homeAddress" :disabled="!isEditing" placeholder="请输入家庭住址" />
          </div>
        </div>

        <div class="form-section">
          <h3>个人信息</h3>
          <div class="form-item full-width">
            <label>个人简介</label>
            <el-input type="textarea" v-model="form.personalInfo" :disabled="!isEditing" placeholder="请输入个人简介" :rows="4" />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.page-container {
  min-height: 100vh;
  position: relative;
  z-index: 10;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 16px 24px;
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
}

.logo svg {
  width: 32px;
  height: 32px;
  color: #aa3bff;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-btn {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-btn:hover {
  background: rgba(170, 59, 255, 0.3);
  border-color: #aa3bff;
}

.logout-btn {
  padding: 10px 20px;
  background: rgba(255, 107, 107, 0.2);
  border: 1px solid rgba(255, 107, 107, 0.4);
  border-radius: 8px;
  color: #ff6b6b;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: rgba(255, 107, 107, 0.3);
}

.main-content {
  padding: 100px 24px 40px;
  max-width: 800px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.back-btn {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.header h1 {
  flex: 1;
  font-size: 28px;
  font-weight: 600;
  color: white;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.edit-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(170, 59, 255, 0.4);
}

.save-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #52c41a, #73d13d);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(82, 196, 26, 0.4);
}

.cancel-btn {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.profile-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 32px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 24px;
}

.avatar-container {
  position: relative;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 36px;
  font-weight: 700;
}

.avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  border-radius: 50%;
}

.profile-title h2 {
  font-size: 24px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px;
}

.role-tag {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(170, 59, 255, 0.2);
  border: 1px solid rgba(170, 59, 255, 0.4);
  border-radius: 12px;
  color: #aa3bff;
  font-size: 13px;
}

.form-section {
  margin-bottom: 28px;
}

.form-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin: 0 0 20px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item.full-width {
  grid-column: 1 / -1;
}

.form-item label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.el-input__wrapper,
.el-select .el-input__wrapper {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
}

.el-input__wrapper.is-disabled {
  background: rgba(255, 255, 255, 0.03);
  opacity: 0.7;
}

.el-input__inner,
.el-select .el-input__inner {
  color: white;
}

.el-input__placeholder,
.el-select .el-input__placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.el-textarea__inner {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.el-textarea__inner::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

@media (max-width: 768px) {
  .header {
    flex-wrap: wrap;
  }

  .header h1 {
    font-size: 24px;
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .nav-links {
    gap: 8px;
  }

  .menu-btn {
    padding: 8px 12px;
    font-size: 12px;
  }

  .logout-btn {
    padding: 8px 12px;
    font-size: 12px;
  }
}
</style>