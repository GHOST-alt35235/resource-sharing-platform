<template>
  <div class="resource-detail">
    <el-button @click="$router.back()">返回</el-button>

    <el-card v-loading="loading">
      <h1>{{ resource.title }}</h1>
      <div class="meta">
        <span>上传者: {{ resource.uploaderName }}</span>
        <span>价格: {{ resource.price }}积分</span>
        <span>下载: {{ resource.downloadCount }}次</span>
        <span>{{ formatDate(resource.createTime) }}</span>
      </div>
      <div class="file-info">
        <span class="file-icon">{{ getFileIcon(resource.fileType) }}</span>
        <span>文件类型: {{ getFileTypeDesc(resource.fileType) }}</span>
        <span>文件大小: {{ formatFileSize(resource.fileSize) }}</span>
        <span>文件名: {{ resource.fileName }}</span>
      </div>
      <div class="description">
        <h3>资源描述</h3>
        <p>{{ resource.description || '暂无描述' }}</p>
      </div>

      <div class="actions">
        <el-button type="primary" @click="handleDownload" :loading="downloading">
          {{ resource.price > 0 ? `下载 (消耗${resource.price}积分)` : '免费下载' }}
        </el-button>
      </div>
    </el-card>

    <el-card class="comments">
      <h3>评论</h3>
      <div class="comment-form" v-if="isLoggedIn">
        <el-input
          v-model="commentContent"
          type="textarea"
          placeholder="发表你的评论..."
          :rows="3"
        />
        <el-button type="primary" @click="submitComment" style="margin-top: 10px">发表评论</el-button>
      </div>
      <el-empty v-else description="登录后可以评论" />

      <div class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <span class="nickname">{{ comment.nickname }}</span>
            <span class="time">{{ formatDate(comment.createTime) }}</span>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-actions">
            <el-button size="small" @click="handleLike(comment.id)" :disabled="!isLoggedIn">
              点赞 ({{ comment.likeCount }})
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import request from '../api/axios'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const downloading = ref(false)
const resource = ref({})
const comments = ref([])
const commentContent = ref('')

const isLoggedIn = computed(() => !!localStorage.getItem('token'))

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

const loadResource = async () => {
  loading.value = true
  try {
    const res = await request.get(`/resource/${route.params.id}`)
    resource.value = res.data
    await loadComments()
  } catch (e) {
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    const res = await request.get(`/comment/resource/${route.params.id}`)
    comments.value = res.data || []
  } catch (e) {
  }
}

const handleDownload = async () => {
  downloading.value = true
  try {
    await request.post(`/resource/download/${route.params.id}`)
    
    const token = localStorage.getItem('token')
    const response = await axios.get(`/api/files/download/${resource.value.fileId}`, {
      headers: {
        'Authorization': token
      },
      responseType: 'blob'
    })
    
    const blob = new Blob([response.data])
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = resource.value.fileName
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('下载成功')
    loadResource()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '下载失败')
  } finally {
    downloading.value = false
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) return
  try {
    await request.post('/comment', {
      content: commentContent.value,
      resourceId: route.params.id
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    loadComments()
  } catch (e) {
  }
}

const handleLike = async (commentId) => {
  try {
    await request.post(`/comment/like/${commentId}`)
    loadComments()
  } catch (e) {
  }
}

onMounted(loadResource)
</script>

<style scoped>
.resource-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.resource-detail h1 {
  margin-bottom: 15px;
}

.meta {
  display: flex;
  gap: 20px;
  color: #999;
  font-size: 14px;
  margin-bottom: 15px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
  padding: 12px 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.file-icon {
  font-size: 24px;
}

.description {
  margin: 20px 0;
}

.description h3 {
  margin-bottom: 10px;
}

.actions {
  margin-top: 20px;
}

.comments {
  margin-top: 20px;
}

.comment-form {
  margin-bottom: 20px;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.nickname {
  font-weight: bold;
  color: #333;
}

.time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  color: #666;
  margin-bottom: 10px;
}

.comment-actions {
  text-align: right;
}
</style>
