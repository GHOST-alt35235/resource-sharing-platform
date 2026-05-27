<template>
  <div class="my-resources">
    <div class="header">
      <h2>我的资源</h2>
      <el-button type="primary" @click="$router.push('/upload')">上传新资源</el-button>
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

    <el-table :data="resources" v-loading="loading">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
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
      <el-table-column prop="price" label="价格(积分)" width="100" />
      <el-table-column prop="downloadCount" label="下载次数" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已通过</el-tag>
          <el-tag v-else-if="row.status === -1" type="danger">已驳回</el-tag>
          <el-tag v-else-if="row.status === 2" type="info">已下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="上传时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="$router.push(`/resource/${row.id}`)">查看详情</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && resources.length === 0" description="暂无上传的资源" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/axios'

const loading = ref(false)
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

const handlePreview = (row) => {
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
    window.open(`/api/files/download/${previewFile.value.fileId}`, '_blank')
  }
}

const loadResources = async () => {
  loading.value = true
  try {
    const res = await request.get('/resource/my')
    resources.value = res.data || []
  } catch (e) {
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个资源吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/resource/${id}`)
    ElMessage.success('删除成功')
    loadResources()
  } catch (e) {
  }
}

onMounted(loadResources)
</script>

<style scoped>
.my-resources {
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
