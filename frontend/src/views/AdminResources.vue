<template>
  <div class="admin-resources">
    <div class="header">
      <h2>资源审核管理</h2>
      <el-button @click="loadResources" :loading="loading">刷新</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-num pending">{{ pendingCount }}</div>
          <div class="stats-label">待审核</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-num approved">{{ approvedCount }}</div>
          <div class="stats-label">已通过</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-num rejected">{{ rejectedCount }}</div>
          <div class="stats-label">已驳回</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-num total">{{ totalCount }}</div>
          <div class="stats-label">总资源</div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="previewVisible" title="文件预览" width="600px" destroy-on-close>
      <div class="preview-content">
        <div v-if="previewFile && isImageType(previewFile.fileType)" class="image-preview">
          <img :src="previewUrl" alt="预览图片" />
        </div>
        <div v-else-if="previewFile" class="file-preview">
          <div class="file-icon-large">{{ getFileIcon(previewFile.fileType) }}</div>
          <div class="file-name">{{ previewFile.fileName || '未知文件名' }}</div>
          <div class="file-type">{{ getFileTypeDesc(previewFile.fileType) }}</div>
          <div class="file-size">{{ formatFileSize(previewFile.fileSize) }}</div>
          <el-button type="primary" @click="downloadFile">下载文件</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="rejectVisible" title="驳回原因" width="400px">
      <div style="margin-bottom: 15px;">
        <label style="display: block; margin-bottom: 5px; font-weight: bold;">驳回原因</label>
        <textarea
          v-model="rejectForm.reason"
          rows="4"
          placeholder="请输入驳回原因，以便用户了解为何被驳回"
          style="width: 100%; padding: 10px; border: 1px solid #dcdfe6; border-radius: 4px; resize: vertical;"
        />
      </div>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>

    <el-table :data="resources" v-loading="loading" style="margin-top: 20px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="fileType" label="文件类型" width="100">
        <template #default="{ row }">
          <span>{{ getFileIcon(row.fileType) }} {{ getFileTypeDesc(row.fileType) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="fileSize" label="文件大小" width="100">
        <template #default="{ row }">
          <span>{{ formatFileSize(row.fileSize) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="uploaderName" label="上传者" width="120" />
      <el-table-column prop="price" label="价格(积分)" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已通过</el-tag>
          <el-tag v-else-if="row.status === -1" type="danger">已驳回</el-tag>
          <el-tag v-else-if="row.status === 2" type="info">已下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="handlePreview(row)">预览</el-button>
          <el-button size="small" @click="$router.push(`/resource/${row.id}`)">查看</el-button>
          <el-button v-if="row.status === 0" size="small" type="success" @click="handleApprove(row.id)">通过</el-button>
          <el-button v-if="row.status === 0" size="small" type="danger" @click="handleReject(row.id)">驳回</el-button>
          <el-button v-if="row.status === 1" size="small" type="info" @click="handleOffline(row.id)">下架</el-button>
          <el-button v-if="row.status === 2" size="small" type="success" @click="handleApprove(row.id)">上架</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && resources.length === 0" description="暂无资源" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/axios'

const loading = ref(false)
const resources = ref([])
const previewVisible = ref(false)
const previewFile = ref(null)
const rejectVisible = ref(false)
const rejectForm = ref({
  reason: ''
})
const rejectResourceId = ref(null)

const previewUrl = computed(() => {
  if (previewFile.value && previewFile.value.fileId) {
    return `/api/files/preview/${previewFile.value.fileId}`
  }
  return ''
})

const pendingCount = computed(() => resources.value.filter(r => r.status === 0).length)
const approvedCount = computed(() => resources.value.filter(r => r.status === 1).length)
const rejectedCount = computed(() => resources.value.filter(r => r.status === -1).length)
const totalCount = computed(() => resources.value.length)

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const formatFileSize = (size) => {
  if (!size || size === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(size) / Math.log(k))
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const getFileTypeDesc = (fileType) => {
  if (!fileType) return '未知类型'
  const type = fileType.toLowerCase()
  const imageTypes = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp']
  const docTypes = ['.doc', '.docx', '.pdf', '.txt', '.ppt', '.pptx', '.xls', '.xlsx']
  const videoTypes = ['.mp4', '.avi', '.mkv', '.mov', '.flv']
  const audioTypes = ['.mp3', '.wav', '.ogg', '.flac']
  const zipTypes = ['.zip', '.rar', '.7z', '.tar', '.gz']

  if (imageTypes.includes(type)) return '图片'
  if (docTypes.includes(type)) return '文档'
  if (videoTypes.includes(type)) return '视频'
  if (audioTypes.includes(type)) return '音频'
  if (zipTypes.includes(type)) return '压缩包'
  return type
}

const getFileIcon = (fileType) => {
  if (!fileType) return '📄'
  const type = fileType.toLowerCase()
  const imageTypes = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp']
  const docTypes = ['.doc', '.docx', '.pdf', '.txt', '.ppt', '.pptx', '.xls', '.xlsx']
  const videoTypes = ['.mp4', '.avi', '.mkv', '.mov', '.flv']
  const audioTypes = ['.mp3', '.wav', '.ogg', '.flac']
  const zipTypes = ['.zip', '.rar', '.7z', '.tar', '.gz']

  if (imageTypes.includes(type)) return '🖼️'
  if (docTypes.includes(type)) return '📄'
  if (videoTypes.includes(type)) return '🎬'
  if (audioTypes.includes(type)) return '🎵'
  if (zipTypes.includes(type)) return '📦'
  return '📄'
}

const isImageType = (fileType) => {
  if (!fileType) return false
  const type = fileType.toLowerCase()
  const imageTypes = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp']
  return imageTypes.includes(type)
}

const handlePreview = async (row) => {
  previewFile.value = {
    fileId: row.fileId,
    fileName: row.fileName,
    fileType: row.fileType,
    fileSize: row.fileSize
  }
  previewVisible.value = true
}

const downloadFile = () => {
  if (previewFile.value && previewFile.value.fileId) {
    window.open(`/files/download/${previewFile.value.fileId}`, '_blank')
  }
}

const loadResources = async () => {
  loading.value = true
  try {
    const res = await request.get('/resource/admin/all')
    resources.value = res.data || []
  } catch (e) {
    ElMessage.error('获取资源列表失败')
  } finally {
    loading.value = false
  }
}

const handleApprove = async (id) => {
  try {
    await request.put(`/resource/approve/${id}`)
    ElMessage.success('审核通过')
    loadResources()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleReject = (id) => {
  rejectResourceId.value = id
  rejectForm.value.reason = ''
  rejectVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.value.reason.trim()) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  
  try {
    await request.put(`/resource/reject/${rejectResourceId.value}`, {
      reason: rejectForm.value.reason
    })
    ElMessage.success('已驳回')
    rejectVisible.value = false
    loadResources()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleOffline = async (id) => {
  try {
    await request.put(`/resource/offline/${id}`)
    ElMessage.success('已下架')
    loadResources()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(loadResources)
</script>

<style scoped>
.admin-resources {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stats-card {
  text-align: center;
}

.stats-num {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stats-num.pending {
  color: #e6a23c;
}

.stats-num.approved {
  color: #67c23a;
}

.stats-num.rejected {
  color: #f56c6c;
}

.stats-num.total {
  color: #409eff;
}

.stats-label {
  font-size: 14px;
  color: #999;
}

.preview-content {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview {
  max-width: 100%;
  max-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
}

.file-preview {
  text-align: center;
  padding: 40px;
}

.file-icon-large {
  font-size: 80px;
  margin-bottom: 20px;
}

.file-preview .file-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
  word-break: break-all;
}

.file-preview .file-type {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.file-preview .file-size {
  font-size: 14px;
  color: #999;
  margin-bottom: 20px;
}
</style>
