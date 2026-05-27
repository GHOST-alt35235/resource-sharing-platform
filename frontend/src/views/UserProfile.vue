<template>
  <div class="user-profile">
    <h2>个人资料</h2>

    <el-card>
      <div class="profile-header">
        <el-avatar :size="120" :src="user.avatar" />
        <div class="profile-info">
          <h3>{{ user.nickname }}</h3>
          <p>用户名: {{ user.username }}</p>
          <p>手机号: {{ user.phone }}</p>
          <p>积分: <span class="points">{{ user.points }}</span></p>
        </div>
      </div>

      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="个人简介" prop="introduction">
          <el-input v-model="form.introduction" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="loading">保存修改</el-button>
        </el-form-item>
      </el-form>

      <div class="actions">
        <el-button @click="showChangePassword = true">修改密码</el-button>
        <el-button @click="handleLogout">退出登录</el-button>
      </div>
    </el-card>

    <el-dialog title="修改密码" v-model="showChangePassword">
      <el-form :model="passwordForm" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/axios'

const router = useRouter()
const formRef = ref()
const passwordFormRef = ref()
const loading = ref(false)
const showChangePassword = ref(false)

const user = reactive({
  id: 0,
  username: '',
  nickname: '',
  phone: '',
  avatar: '',
  introduction: '',
  points: 0
})

const form = reactive({
  nickname: '',
  introduction: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loadUser = async () => {
  try {
    const res = await request.get('/auth/current')
    Object.assign(user, res.data)
    form.nickname = user.nickname
    form.introduction = user.introduction || ''
  } catch (e) {
  }
}

const handleUpdate = async () => {
  loading.value = true
  try {
    await request.put('/user/info', {
      nickname: form.nickname,
      introduction: form.introduction
    })
    ElMessage.success('修改成功')
    user.nickname = form.nickname
    user.introduction = form.introduction
  } catch (e) {
  } finally {
    loading.value = false
  }
}

const handleChangePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入密码不一致')
    return
  }
  try {
    await request.put('/user/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    showChangePassword.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (e) {
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/login')
}

onMounted(loadUser)
</script>

<style scoped>
.user-profile {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.user-profile h2 {
  margin-bottom: 20px;
}

.profile-header {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.profile-info {
  flex: 1;
}

.profile-info h3 {
  margin-bottom: 10px;
}

.profile-info p {
  margin-bottom: 5px;
  color: #666;
}

.points {
  color: #667eea;
  font-weight: bold;
}

.actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}
</style>
