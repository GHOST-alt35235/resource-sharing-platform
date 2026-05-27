<template>
  <div class="upload-resource">
    <h2>上传资源</h2>

    <el-form :model="form" ref="formRef" label-width="120px">
      <el-form-item label="资源标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入资源标题" />
      </el-form-item>
      <el-form-item label="资源描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入资源描述" />
      </el-form-item>
      <el-form-item label="上传文件">
        <el-upload
          class="upload-demo"
          action="/api/files/upload"
          :on-success="handleFileUpload"
          :on-error="handleFileError"
          :file-list="fileList"
          :auto-upload="true"
        >
          <el-button type="primary">选择文件</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="下载价格" prop="price">
        <el-input-number v-model="form.price" :min="0" :max="1000" />
        <span style="margin-left: 10px">积分</span>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
        <el-button @click="$router.push('/my-resources')">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/axios'

const formRef = ref()
const loading = ref(false)
const fileList = ref([])

const form = reactive({
  title: '',
  description: '',
  fileId: null,
  price: 0
})

const handleFileUpload = (response) => {
  form.fileId = response.data.id
  ElMessage.success('文件上传成功')
}

const handleFileError = () => {
  ElMessage.error('文件上传失败')
}

const handleSubmit = async () => {
  if (!form.title.trim()) {
    ElMessage.error('请输入资源标题')
    return
  }
  if (!form.fileId) {
    ElMessage.error('请上传文件')
    return
  }

  loading.value = true
  try {
    await request.post('/resource', {
      title: form.title,
      description: form.description,
      fileId: form.fileId,
      price: form.price
    })
    ElMessage.success('资源上传成功，等待审核')
    window.location.href = '/my-resources'
  } catch (e) {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.upload-resource {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.upload-resource h2 {
  margin-bottom: 20px;
}

.upload-demo {
  margin-bottom: 20px;
}
</style>
