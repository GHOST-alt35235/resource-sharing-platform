<template>
  <div class="home">
    <div class="header">
      <div class="container">
        <h1 class="title">资源共享与积分交易平台</h1>
        <p class="subtitle">分享资源、获取积分、下载共享资料</p>
        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索资源..."
            @keyup.enter="handleSearch"
            class="search-input"
          >
            <template #append>
              <el-button @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
        <div class="actions">
          <el-button v-if="!isLoggedIn" type="success" @click="$router.push('/login')">登录</el-button>
          <el-button v-if="!isLoggedIn" @click="$router.push('/register')">注册</el-button>
          <el-button v-if="isLoggedIn" type="primary" @click="$router.push('/upload')">上传资源</el-button>
          <el-button v-if="isLoggedIn" @click="$router.push('/my-resources')">我的资源</el-button>
          <el-button v-if="isAdmin" type="warning" @click="$router.push('/admin/resources')">资源审核</el-button>
          <el-button v-if="isAdmin" type="danger" @click="$router.push('/admin/users')">用户管理</el-button>
          <el-button v-if="isLoggedIn" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </div>

    <div class="stats" v-if="isAdmin">
      <div class="stats-header">
        <span class="stats-title">数据统计</span>
        <el-button type="text" @click="loadStats" :loading="refreshing">
          <el-icon :size="18"><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="stat-card clickable" @click="handleBrowseResources">
            <div class="stat-value">{{ stats.resources }}</div>
            <div class="stat-label">共享资源</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-value">{{ stats.users }}</div>
            <div class="stat-label">注册用户</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-value">{{ stats.downloads }}</div>
            <div class="stat-label">下载次数</div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="features">
      <h2>平台特色</h2>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="feature-card">
            <h3>资源分享</h3>
            <p>上传您的优质资源，分享给更多人</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card">
            <h3>积分激励</h3>
            <p>上传获得积分奖励，下载消耗积分</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card">
            <h3>互动评论</h3>
            <p>对资源进行评价，与作者交流</p>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import request from '../api/axios'

const router = useRouter()
const keyword = ref('')
const stats = ref({
  resources: 0,
  users: 0,
  downloads: 0
})
const refreshing = ref(false)

const isLoggedIn = computed(() => !!localStorage.getItem('token'))
const isAdmin = computed(() => {
  const user = localStorage.getItem('user')
  if (!user) return false
  try {
    return JSON.parse(user).role === 'admin'
  } catch (e) {
    return false
  }
})

const handleBrowseResources = () => {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  router.push('/resources')
}

const handleSearch = () => {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  router.push({ name: 'ResourceList', query: { keyword: keyword.value } })
}

const handleLogout = async () => {
  try {
    await request.post('/auth/logout')
  } catch (e) {
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('退出成功')
    window.location.href = '/'
  }
}

const loadStats = async () => {
  refreshing.value = true
  try {
    const res = await request.get('/statistics')
    stats.value.resources = res.data?.resources || 0
    stats.value.users = res.data?.users || 0
    stats.value.downloads = res.data?.downloads || 0
  } catch (e) {
  } finally {
    refreshing.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f8fafc;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  color: white;
  padding: 100px 20px 140px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.header::before {
  content: '';
  position: absolute;
  top: 0;
  left: -50%;
  width: 200%;
  height: 100%;
  background: radial-gradient(circle at 30% 60%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
}

.header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 100px;
  background: linear-gradient(to bottom, rgba(248, 250, 252, 0) 0%, #f8fafc 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 10;
}

.title {
  font-size: 56px;
  font-weight: 800;
  margin-bottom: 24px;
  letter-spacing: -1px;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.subtitle {
  font-size: 22px;
  margin-bottom: 50px;
  opacity: 0.95;
  font-weight: 400;
}

.search-box {
  max-width: 600px;
  margin: 0 auto 40px;
}

.search-input {
  width: 100%;
  height: 56px;
  border-radius: 16px;
}

.search-input .el-input__wrapper {
  height: 56px;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
  border: none;
}

.search-input .el-input__inner {
  font-size: 16px;
}

.search-input .el-button {
  height: 40px;
  padding: 0 24px;
  border-radius: 12px;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.actions .el-button {
  padding: 12px 28px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 14px;
}

.actions .el-button--success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.actions .el-button--warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.actions .el-button--danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.stats {
  max-width: 1200px;
  margin: -60px auto 60px;
  padding: 0 24px;
  position: relative;
  z-index: 20;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.stats-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}

.stat-card {
  text-align: center;
  padding: 35px 20px;
  border-radius: 20px;
  background: white;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border: 1px solid #f1f5f9;
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 25px 50px rgba(102, 126, 234, 0.15);
}

.stat-value {
  font-size: 42px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 15px;
  color: #64748b;
  font-weight: 500;
}

.features {
  max-width: 1200px;
  margin: 0 auto;
  padding: 80px 24px;
}

.features h2 {
  text-align: center;
  margin-bottom: 50px;
  font-size: 36px;
  font-weight: 700;
  color: #1e293b;
}

.feature-card {
  text-align: center;
  padding: 40px 30px;
  border-radius: 20px;
  background: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid #f1f5f9;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.12);
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
}

.feature-card p {
  color: #64748b;
  font-size: 14px;
  line-height: 1.8;
}

.feature-card .icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}
</style>
