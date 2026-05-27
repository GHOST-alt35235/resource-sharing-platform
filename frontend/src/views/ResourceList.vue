<template>
  <div class="resource-list">
    <div class="header">
      <el-button @click="$router.push('/')">返回首页</el-button>
      <el-input
        v-model="keyword"
        placeholder="搜索资源"
        style="width: 300px"
        @keyup.enter="loadResources"
      >
        <template #append>
          <el-button @click="loadResources">搜索</el-button>
        </template>
      </el-input>
    </div>

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

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8" v-for="item in resources" :key="item.id">
        <el-card class="resource-card" @click="$router.push(`/resource/${item.id}`)">
          <div class="resource-header">
            <span class="file-icon">{{ getFileIcon(item.fileType) }}</span>
            <div class="resource-title">{{ item.title }}</div>
          </div>
          <div class="resource-desc">{{ item.description || '暂无描述' }}</div>
          <div class="resource-info">
            <span>上传者: {{ item.uploaderName }}</span>
            <span>{{ getFileTypeDesc(item.fileType) }}</span>
          </div>
          <div class="resource-meta">
            <span>价格: {{ item.price }}积分</span>
            <span>大小: {{ formatFileSize(item.fileSize) }}</span>
            <span>下载: {{ item.downloadCount }}次</span>
          </div>
          <div class="resource-actions" @click.stop>
            <el-button size="small" type="primary" @click="$router.push(`/resource/${item.id}`)">查看详情</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && resources.length === 0" description="暂无资源" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import request from '../api/axios'

const route = useRoute()
const loading = ref(false)
const keyword = ref('')
const resources = ref([])
const previewVisible = ref(false)
const previewFile = ref(null)

const previewUrl = computed(() => {
  if (previewFile.value && previewFile.value.fileId) {
    return `/api/files/preview/${previewFile.value.fileId}`
  }
  return ''
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
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

const handlePreview = (item) => {
  previewFile.value = {
    fileId: item.fileId,
    fileName: item.fileName,
    fileType: item.fileType,
    fileSize: item.fileSize
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
    const params = keyword.value ? { keyword: keyword.value } : {}
    const res = await request.get('/resource/list', { params })
    resources.value = res.data || []
  } catch (e) {
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (route.query.keyword) {
    keyword.value = route.query.keyword
  }
  loadResources()
})
</script>

<style scoped>
.resource-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.header .el-button {
  padding: 10px 24px;
  border-radius: 12px;
}

.header .el-input {
  width: 350px;
}

.header .el-input__wrapper {
  border-radius: 12px;
  transition: all 0.3s ease;
}

.header .el-input__wrapper:focus-within {
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.resource-card {
  margin-bottom: 24px;
  cursor: pointer;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  overflow: hidden;
}

.resource-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.15);
}

.resource-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.resource-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
  padding-top: 12px;
}

.file-icon {
  font-size: 32px;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 14px;
}

.resource-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
}

.resource-desc {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 16px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.resource-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #94a3b8;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.resource-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.resource-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 16px;
}

.resource-meta span {
  padding: 4px 12px;
  background: #f1f5f9;
  border-radius: 20px;
  color: #64748b;
}

.resource-meta span:first-child {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #6366f1;
  font-weight: 500;
}

.resource-actions {
  display: flex;
  gap: 12px;
}

.resource-actions .el-button {
  flex: 1;
  height: 40px;
  border-radius: 10px;
  font-weight: 500;
}

.resource-actions .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.preview-content {
  min-height: 350px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.image-preview {
  max-width: 100%;
  max-height: 550px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  border-radius: 16px;
  padding: 20px;
}

.image-preview img {
  max-width: 100%;
  max-height: 550px;
  object-fit: contain;
  border-radius: 12px;
}

.file-preview {
  text-align: center;
  padding: 60px 40px;
  background: #f8fafc;
  border-radius: 16px;
}

.file-icon-large {
  font-size: 96px;
  margin-bottom: 24px;
}

.file-preview .file-name {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 12px;
  word-break: break-all;
}

.file-preview .file-type {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.file-preview .file-size {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 24px;
}

.file-preview .el-button {
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}
</style>
