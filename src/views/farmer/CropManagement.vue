<script setup>import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { ElMessage, ElMessageBox } from 'element-plus';
import ParticleBackground from '../../components/ParticleBackground.vue';
import ThreeScene from '../../components/ThreeScene.vue';
const router = useRouter();
const userStore = useUserStore();
const cropList = ref([]);
const showAddModal = ref(false);
const showEditModal = ref(false);
const form = reactive({
 id: null,
 name: '',
 category: '',
 info: '',
 remark: ''
});
const categories = [
 { label: '主食', value: '主食' },
 { label: '药材', value: '药材' },
 { label: '蔬菜', value: '蔬菜' },
 { label: '水果', value: '水果' },
 { label: '经济作物', value: '经济作物' },
 { label: '花卉', value: '花卉' }
];
onMounted(() => {
 loadCropList();
});
async function loadCropList() {
 const mockData = [
 {
 id: 1,
 name: '水稻',
 category: '主食',
 info: '一年生禾本科植物，喜温喜湿，是我国主要粮食作物之一。',
 remark: '种植面积20亩，预计产量15吨'
 },
 {
 id: 2,
 name: '人参',
 category: '药材',
 info: '多年生草本植物，名贵中药材，具有滋补强身的功效。',
 remark: '种植面积5亩，三年生'
 },
 {
 id: 3,
 name: '西红柿',
 category: '蔬菜',
 info: '一年生草本植物，喜温喜光，富含维生素C。',
 remark: '种植面积8亩，大棚种植'
 },
 {
 id: 4,
 name: '草莓',
 category: '水果',
 info: '多年生草本植物，喜凉爽气候，果实甜美多汁。',
 remark: '种植面积3亩，有机种植'
 },
 {
 id: 5,
 name: '棉花',
 category: '经济作物',
 info: '锦葵科棉属植物，重要的纤维作物。',
 remark: '种植面积10亩'
 }
 ];
 cropList.value = mockData;
}
function openAddModal() {
 resetForm();
 showAddModal.value = true;
}
function openEditModal(crop) {
 Object.assign(form, crop);
 showEditModal.value = true;
}
function resetForm() {
 form.id = null;
 form.name = '';
 form.category = '';
 form.info = '';
 form.remark = '';
}
async function handleAdd() {
 if (!form.name || !form.category) {
 ElMessage.warning('请填写农作物名称和种类');
 return;
 }
 const newCrop = {
 id: Date.now(),
 ...{ ...form }
 };
 cropList.value.unshift(newCrop);
 showAddModal.value = false;
 ElMessage.success('添加成功');
}
async function handleEdit() {
 if (!form.name || !form.category) {
 ElMessage.warning('请填写农作物名称和种类');
 return;
 }
 const index = cropList.value.findIndex(c => c.id === form.id);
 if (index !== -1) {
 cropList.value[index] = { ...form };
 }
 showEditModal.value = false;
 ElMessage.success('修改成功');
}
async function handleDelete(id) {
 try {
 await ElMessageBox.confirm('确定要删除该农作物记录吗？', '提示', {
 confirmButtonText: '确定',
 cancelButtonText: '取消',
 type: 'warning'
 });
 cropList.value = cropList.value.filter(c => c.id !== id);
 ElMessage.success('删除成功');
 }
 catch (error) {
 if (error !== 'cancel') {
 ElMessage.error('删除失败');
 }
 }
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
 { name: '系统设置', path: '/admin/settings', icon: '⚙️' },
 { name: '个人信息', path: '/profile', icon: '👤' }
];
const farmerMenus = [
 { name: '我的农场', path: '/farmer/my-farm', icon: '🌾' },
 { name: '设备管理', path: '/farmer/devices', icon: '📡' },
 { name: '农作物管理', path: '/farmer/crops', icon: '🌱' },
 { name: '个人信息', path: '/profile', icon: '👤' }
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
        <h1>农作物管理</h1>
        <button class="add-btn" @click="openAddModal">
          <span>+</span>
          <span>添加农作物</span>
        </button>
      </header>

      <div class="crop-list">
        <div
          v-for="crop in cropList"
          :key="crop.id"
          class="crop-card"
        >
          <div class="crop-header">
            <div class="crop-icon">🌱</div>
            <div class="crop-title">
              <h3>{{ crop.name }}</h3>
              <span class="category-tag">{{ crop.category }}</span>
            </div>
            <div class="crop-actions">
              <button class="edit-btn" @click="openEditModal(crop)">编辑</button>
              <button class="delete-btn" @click="handleDelete(crop.id)">删除</button>
            </div>
          </div>
          <div class="crop-body">
            <div class="crop-info">
              <h4>作物信息</h4>
              <p>{{ crop.info }}</p>
            </div>
            <div v-if="crop.remark" class="crop-remark">
              <h4>备注</h4>
              <p>{{ crop.remark }}</p>
            </div>
          </div>
        </div>

        <div v-if="cropList.length === 0" class="empty-state">
          <span class="empty-icon">🌾</span>
          <p>暂无农作物记录</p>
          <button class="add-btn" @click="openAddModal">添加农作物</button>
        </div>
      </div>
    </main>

    <el-dialog v-model="showAddModal" title="添加农作物" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="农作物名称">
          <el-input v-model="form.name" placeholder="请输入农作物名称" />
        </el-form-item>
        <el-form-item label="种类">
          <el-select v-model="form.category" placeholder="请选择种类">
            <el-option v-for="cat in categories" :key="cat.value" :label="cat.label" :value="cat.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="作物信息">
          <el-input type="textarea" v-model="form.info" placeholder="请输入作物信息" :rows="4" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注信息" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showEditModal" title="修改农作物" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="农作物名称">
          <el-input v-model="form.name" placeholder="请输入农作物名称" />
        </el-form-item>
        <el-form-item label="种类">
          <el-select v-model="form.category" placeholder="请选择种类">
            <el-option v-for="cat in categories" :key="cat.value" :label="cat.label" :value="cat.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="作物信息">
          <el-input type="textarea" v-model="form.info" placeholder="请输入作物信息" :rows="4" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注信息" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditModal = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确定</el-button>
      </template>
    </el-dialog>
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
  max-width: 1400px;
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
  max-width: 1200px;
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

.add-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #52c41a, #73d13d);
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

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(82, 196, 26, 0.4);
}

.add-btn span:first-child {
  font-size: 20px;
  font-weight: 300;
}

.crop-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.crop-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  transition: all 0.3s ease;
}

.crop-card:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.08);
}

.crop-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 16px;
}

.crop-icon {
  font-size: 40px;
}

.crop-title {
  flex: 1;
}

.crop-title h3 {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px;
}

.category-tag {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(82, 196, 26, 0.2);
  border: 1px solid rgba(82, 196, 26, 0.4);
  border-radius: 12px;
  color: #52c41a;
  font-size: 13px;
}

.crop-actions {
  display: flex;
  gap: 8px;
}

.crop-actions .edit-btn {
  padding: 6px 16px;
  background: rgba(170, 59, 255, 0.2);
  border: 1px solid rgba(170, 59, 255, 0.4);
  border-radius: 6px;
  color: #aa3bff;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.crop-actions .edit-btn:hover {
  background: rgba(170, 59, 255, 0.3);
}

.crop-actions .delete-btn {
  padding: 6px 16px;
  background: rgba(255, 107, 107, 0.2);
  border: 1px solid rgba(255, 107, 107, 0.4);
  border-radius: 6px;
  color: #ff6b6b;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.crop-actions .delete-btn:hover {
  background: rgba(255, 107, 107, 0.3);
}

.crop-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.crop-info h4,
.crop-remark h4 {
  font-size: 14px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 8px;
}

.crop-info p,
.crop-remark p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  line-height: 1.6;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  display: block;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 24px;
}

.el-dialog__header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.el-dialog__title {
  color: white;
}

.el-dialog__body {
  padding: 24px;
}

.el-dialog__footer {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.el-form-item__label {
  color: rgba(255, 255, 255, 0.8);
}

.el-input__wrapper,
.el-select .el-input__wrapper {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
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

  .crop-list {
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