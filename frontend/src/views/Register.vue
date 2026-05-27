<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2>注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称（选填）" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" @keyup.enter="handleRegister" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleRegister" :loading="loading">注册</el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        已有账号？<el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/axios'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = ref({
  username: '',
  phone: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await request.post('/auth/register', form.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  position: relative;
  overflow: hidden;
}

.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.register-card {
  width: 450px;
  padding: 40px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  position: relative;
  z-index: 10;
  animation: fadeIn 0.5s ease forwards;
}

.register-card::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border-radius: 26px;
  z-index: -1;
  opacity: 0.8;
}

.register-card h2 {
  text-align: center;
  margin-bottom: 35px;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.el-form {
  margin-top: 10px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__label {
  font-weight: 600;
  color: #475569;
  font-size: 14px;
}

.el-input__wrapper {
  height: 48px;
  border-radius: 12px;
  border-color: #e2e8f0;
  transition: all 0.3s ease;
}

.el-input__wrapper:hover {
  border-color: #6ee7b7;
}

.el-input__wrapper:focus-within {
  box-shadow: 0 0 0 3px rgba(17, 153, 142, 0.1);
  border-color: #10b981;
}

.el-button--primary {
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  box-shadow: 0 4px 15px rgba(17, 153, 142, 0.4);
  transition: all 0.3s ease;
}

.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(17, 153, 142, 0.5);
}

.el-button--primary:active {
  transform: translateY(0);
}

.footer {
  text-align: center;
  margin-top: 28px;
  color: #64748b;
  font-size: 14px;
}

.footer a {
  color: #10b981;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer a:hover {
  color: #059669;
  text-decoration: underline;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
