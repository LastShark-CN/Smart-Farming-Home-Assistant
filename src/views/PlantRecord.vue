<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElButton, ElForm, ElFormItem, ElInput, ElDatePicker, ElSelect, ElOption, ElUpload, ElDialog, ElTable, ElTableColumn, ElTag, ElPagination, ElImage } from 'element-plus'
import { Plus, Edit, Delete, Eye, Calendar, Leaf, Filter, Upload, X, Check, Back } from '@element-plus/icons-vue'
import { createPlantRecord, getPlantRecords, getPlantRecord, updatePlantRecord, deletePlantRecord, getCropOptions } from '../api/plantRecord'
import { MdEditor, MdPreview } from 'md-editor-v3'
import 'md-editor-v3/dist/style.css'

const router = useRouter()

const records = ref([])
const cropOptions = ref([])
const loading = ref(false)
const showFormDialog = ref(false)
const showDetailDialog = ref(false)
const editMode = ref(false)
const currentRecord = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filterCrop = ref('')
const filterDate = ref('')

const form = ref({
  cropId: '',
  cropName: '',
  recordDate: '',
  content: '',
  images: []
})

const detailRecord = ref(null)

const uploadUrl = computed(() => {
  const baseUrl = import.meta.env.VITE_APP_BASE_API || ''
  return `${baseUrl}/api/upload/image`
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: token ? `Bearer ${token}` : ''
  }
})

const filteredRecords = computed(() => {
  let result = records.value
  if (filterCrop.value) {
    result = result.filter(r => r.cropId === filterCrop.value)
  }
  if (filterDate.value) {
    result = result.filter(r => r.recordDate === filterDate.value)
  }
  return result
})

const handleFilter = () => {
  loadRecords()
}

const resetFilter = () => {
  filterCrop.value = ''
  filterDate.value = ''
  loadRecords()
}

const loadRecords = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      cropId: filterCrop.value || undefined,
      recordDate: filterDate.value || undefined
    }
    const response = await getPlantRecords(params)
    records.value = response.data.list || response.data
    total.value = response.data.total || records.value.length
  } catch (error) {
    ElMessage.error('加载记录失败')
    records.value = mockRecords
    total.value = mockRecords.length
  } finally {
    loading.value = false
  }
}

const loadCropOptions = async () => {
  try {
    const response = await getCropOptions()
    cropOptions.value = response.data || mockCropOptions
  } catch (error) {
    cropOptions.value = mockCropOptions
  }
}

const openAddDialog = () => {
  editMode.value = false
  currentRecord.value = null
  form.value = {
    cropId: '',
    cropName: '',
    recordDate: new Date().toISOString().split('T')[0],
    content: '',
    images: []
  }
  showFormDialog.value = true
}

const openEditDialog = (record) => {
  editMode.value = true
  currentRecord.value = record
  form.value = {
    cropId: record.cropId,
    cropName: record.cropName,
    recordDate: record.recordDate,
    content: record.content,
    images: record.images || []
  }
  showFormDialog.value = true
}

const openDetailDialog = async (record) => {
  try {
    const response = await getPlantRecord(record.id)
    detailRecord.value = response.data || record
  } catch (error) {
    detailRecord.value = record
  }
  showDetailDialog.value = true
}

const handleSubmit = async () => {
  if (!form.value.cropId) {
    ElMessage.warning('请选择作物')
    return
  }
  if (!form.value.content.trim()) {
    ElMessage.warning('请填写种植记录')
    return
  }

  try {
    const crop = cropOptions.value.find(c => c.id === form.value.cropId)
    form.value.cropName = crop ? crop.name : ''

    if (editMode.value && currentRecord.value) {
      await updatePlantRecord(currentRecord.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createPlantRecord(form.value)
      ElMessage.success('创建成功')
    }
    showFormDialog.value = false
    loadRecords()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (record) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePlantRecord(record.id)
    ElMessage.success('删除成功')
    loadRecords()
  } catch (error) {
    console.log('用户取消删除')
  }
}

const handleImageUpload = (response, file, fileList) => {
  if (response && response.data) {
    form.value.images.push(response.data.url)
  }
}

const handleImageRemove = (file, fileList) => {
  const index = form.value.images.indexOf(file.response?.data?.url || file.url)
  if (index > -1) {
    form.value.images.splice(index, 1)
  }
}

const goBack = () => {
  router.push('/dashboard')
}

onMounted(() => {
  loadRecords()
  loadCropOptions()
})

const mockCropOptions = [
  { id: 1, name: '小麦' },
  { id: 2, name: '水稻' },
  { id: 3, name: '玉米' },
  { id: 4, name: '大豆' },
  { id: 5, name: '蔬菜' },
  { id: 6, name: '水果' }
]

const mockRecords = [
  {
    id: 1,
    cropId: 1,
    cropName: '小麦',
    recordDate: '2024-01-15',
    content: '# 小麦种植记录\n\n## 今日情况\n\n今天天气晴朗，气温15°C，湿度60%。\n\n### 施肥情况\n- 施加了有机肥料\n- 每亩用量：50kg\n\n### 生长状态\n小麦长势良好，叶片翠绿，预计下个月可以进行第一次除草。\n\n![小麦照片](https://picsum.photos/seed/wheat1/800/600)',
    images: ['https://picsum.photos/seed/wheat1/800/600'],
    createTime: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    cropId: 2,
    cropName: '水稻',
    recordDate: '2024-01-14',
    content: '# 水稻种植心得\n\n最近连续降雨，需要注意排水问题。\n\n## 病虫害防治\n今天发现少量稻飞虱，已经喷洒了农药进行防治。\n\n## 灌溉\n由于降雨充足，本周不需要额外灌溉。',
    images: [],
    createTime: '2024-01-14 14:20:00'
  },
  {
    id: 3,
    cropId: 3,
    cropName: '玉米',
    recordDate: '2024-01-13',
    content: '# 玉米播种记录\n\n今天完成了春季玉米的播种工作。\n\n### 播种详情\n- 品种：甜玉米\n- 行距：60cm\n- 株距：30cm\n- 每亩播种量：2kg\n\n期待今年有个好收成！🌽',
    images: ['https://picsum.photos/seed/corn1/800/600', 'https://picsum.photos/seed/corn2/800/600'],
    createTime: '2024-01-13 09:00:00'
  }
]
</script>

<template>
  <div class="plant-record-container">
    <nav class="navbar">
      <div class="nav-content">
        <div class="header-left">
          <button class="back-btn" @click="goBack">
            <Back />
          </button>
          <div class="logo" @click="router.push('/')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
              <path d="M2 17l10 5 10-5"></path>
              <path d="M2 12l10 5 10-5"></path>
            </svg>
            <span>种植记录管理</span>
          </div>
        </div>
        <div class="header-right">
          <ElButton type="primary" @click="openAddDialog" class="add-btn">
            <Plus />
            新建记录
          </ElButton>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <div class="filter-section">
        <div class="filter-group">
          <ElSelect 
            v-model="filterCrop" 
            placeholder="按作物筛选" 
            class="filter-select"
            clearable
          >
            <ElOption 
              v-for="crop in cropOptions" 
              :key="crop.id" 
              :label="crop.name" 
              :value="crop.id" 
            />
          </ElSelect>
        </div>
        <div class="filter-group">
          <ElDatePicker
            v-model="filterDate"
            type="date"
            placeholder="按日期筛选"
            class="filter-date"
            value-format="YYYY-MM-DD"
          />
        </div>
        <div class="filter-actions">
          <ElButton type="primary" @click="handleFilter" :icon="Filter">
            筛选
          </ElButton>
          <ElButton @click="resetFilter">
            重置
          </ElButton>
        </div>
      </div>

      <div class="stats-bar">
        <div class="stat-item">
          <span class="stat-icon">📝</span>
          <span class="stat-value">{{ total }}</span>
          <span class="stat-label">总记录数</span>
        </div>
        <div class="stat-item">
          <span class="stat-icon">🌾</span>
          <span class="stat-value">{{ cropOptions.length }}</span>
          <span class="stat-label">作物种类</span>
        </div>
      </div>

      <div class="records-table">
        <ElTable 
          :data="records" 
          :loading="loading" 
          stripe 
          border
          class="table"
        >
          <ElTableColumn prop="cropName" label="作物" width="100">
            <template #default="{ row }">
              <ElTag type="success">{{ row.cropName }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="recordDate" label="记录日期" width="120">
            <template #default="{ row }">
              <span class="date-tag">{{ row.recordDate }}</span>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="content" label="记录内容" min-width="300">
            <template #default="{ row }">
              <div class="content-preview">{{ row.content.slice(0, 100) }}...</div>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="images" label="图片" width="100">
            <template #default="{ row }">
              <span v-if="row.images && row.images.length > 0" class="image-count">
                📷 {{ row.images.length }}张
              </span>
              <span v-else class="no-image">-</span>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="createTime" label="创建时间" width="160" />
          <ElTableColumn label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <ElButton size="small" @click="openDetailDialog(row)" :icon="Eye">
                  查看
                </ElButton>
                <ElButton size="small" type="primary" @click="openEditDialog(row)" :icon="Edit">
                  编辑
                </ElButton>
                <ElButton size="small" type="danger" @click="handleDelete(row)" :icon="Delete">
                  删除
                </ElButton>
              </div>
            </template>
          </ElTableColumn>
        </ElTable>

        <div class="pagination-container">
          <ElPagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
          />
        </div>
      </div>
    </main>

    <ElDialog 
      v-model="showFormDialog" 
      :title="editMode ? '编辑种植记录' : '新建种植记录'" 
      width="800px"
      class="form-dialog"
    >
      <ElForm :model="form" label-width="100px">
        <ElFormItem label="作物">
          <ElSelect v-model="form.cropId" placeholder="请选择作物" style="width: 100%">
            <ElOption 
              v-for="crop in cropOptions" 
              :key="crop.id" 
              :label="crop.name" 
              :value="crop.id" 
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="记录日期">
          <ElDatePicker
            v-model="form.recordDate"
            type="date"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </ElFormItem>
        <ElFormItem label="图片上传">
          <ElUpload
            :action="uploadUrl"
            list-type="picture-card"
            :headers="uploadHeaders"
            :on-success="handleImageUpload"
            :on-remove="handleImageRemove"
            :file-list="form.images.map(img => ({ url: img }))"
            :limit="9"
          >
            <Upload />
          </ElUpload>
        </ElFormItem>
        <ElFormItem label="种植记录">
          <MdEditor v-model="form.content" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showFormDialog = false" :icon="X">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit" :icon="Check">确定</ElButton>
      </template>
    </ElDialog>

    <ElDialog v-model="showDetailDialog" title="种植记录详情" width="800px" class="detail-dialog">
      <div v-if="detailRecord" class="detail-content">
        <div class="detail-header">
          <div class="detail-info">
            <ElTag type="success" size="large">{{ detailRecord.cropName }}</ElTag>
            <span class="detail-date">{{ detailRecord.recordDate }}</span>
          </div>
          <span class="detail-time">创建于 {{ detailRecord.createTime }}</span>
        </div>
        <div v-if="detailRecord.images && detailRecord.images.length > 0" class="detail-images">
          <h4>照片</h4>
          <div class="image-grid">
            <ElImage 
              v-for="(img, index) in detailRecord.images" 
              :key="index" 
              :src="img" 
              fit="cover"
              preview-src-list="detailRecord.images"
            />
          </div>
        </div>
        <div class="detail-body">
          <h4>记录内容</h4>
          <MdPreview :model-value="detailRecord.content" />
        </div>
      </div>
    </ElDialog>
  </div>
</template>

<style scoped>
.plant-record-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
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
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
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

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.main-content {
  padding: 100px 24px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.filter-section {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  flex-wrap: wrap;
}

.filter-group {
  flex: 1;
  min-width: 150px;
}

.filter-select, .filter-date {
  width: 100%;
}

.filter-actions {
  display: flex;
  gap: 8px;
}

.stats-bar {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
}

.stat-icon {
  font-size: 24px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: white;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.records-table {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  overflow: hidden;
}

.table {
  width: 100%;
}

.table :deep(.el-table__header-wrapper) {
  background: rgba(0, 0, 0, 0.2);
}

.table :deep(.el-table__header th) {
  color: rgba(255, 255, 255, 0.8);
  background: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.table :deep(.el-table__body tr) {
  color: rgba(255, 255, 255, 0.9);
}

.table :deep(.el-table__body tr:hover) {
  background: rgba(170, 59, 255, 0.1);
}

.date-tag {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(59, 130, 246, 0.2);
  border-radius: 4px;
  font-size: 12px;
}

.content-preview {
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  line-height: 1.5;
}

.image-count {
  color: #5470c6;
  font-size: 13px;
}

.no-image {
  color: rgba(255, 255, 255, 0.3);
  font-size: 13px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.pagination-container {
  padding: 16px;
  display: flex;
  justify-content: flex-end;
}

.pagination-container :deep(.el-pagination) {
  color: rgba(255, 255, 255, 0.8);
}

.form-dialog :deep(.el-dialog) {
  background: rgba(20, 20, 40, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.form-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.form-dialog :deep(.el-dialog__title) {
  color: white;
}

.form-dialog :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.8);
}

.form-dialog :deep(.el-input__inner),
.form-dialog :deep(.el-select__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.form-dialog :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.detail-dialog :deep(.el-dialog) {
  background: rgba(20, 20, 40, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.detail-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.detail-dialog :deep(.el-dialog__title) {
  color: white;
}

.detail-content {
  color: rgba(255, 255, 255, 0.9);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.detail-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.detail-date {
  font-size: 18px;
  color: white;
}

.detail-time {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.detail-images {
  margin-bottom: 24px;
}

.detail-images h4 {
  margin: 0 0 12px;
  color: white;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.image-grid :deep(.el-image) {
  width: 100%;
  height: 120px;
  border-radius: 8px;
  cursor: pointer;
}

.detail-body {
  margin-top: 16px;
}

.detail-body h4 {
  margin: 0 0 12px;
  color: white;
}

.detail-body :deep(.mavon-editor) {
  background: rgba(255, 255, 255, 0.03);
}

@media (max-width: 768px) {
  .filter-section {
    flex-direction: column;
  }

  .filter-group {
    width: 100%;
  }

  .stats-bar {
    flex-wrap: wrap;
  }

  .stat-item {
    flex: 1;
    min-width: 140px;
  }

  .logo span {
    display: none;
  }
}
</style>