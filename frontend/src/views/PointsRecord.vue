<template>
  <div class="points-record">
    <div class="header">
      <h2>积分记录</h2>
      <div class="balance">
        当前积分: <span class="points">{{ points }}</span>
      </div>
    </div>

    <el-table :data="records" v-loading="loading">
      <el-table-column prop="createTime" label="时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="points" label="积分变动" width="120">
        <template #default="{ row }">
          <span :class="row.points > 0 ? 'positive' : 'negative'">
            {{ row.points > 0 ? '+' : '' }}{{ row.points }}
          </span>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && records.length === 0" description="暂无积分记录" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/axios'

const loading = ref(false)
const points = ref(0)
const records = ref([])

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const loadPoints = async () => {
  loading.value = true
  try {
    const res = await request.get('/points/balance')
    points.value = res.data || 0
  } catch (e) {
  }
}

const loadRecords = async () => {
  try {
    const res = await request.get('/points/records')
    records.value = res.data || []
  } catch (e) {
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPoints()
  loadRecords()
})
</script>

<style scoped>
.points-record {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.balance {
  font-size: 18px;
}

.points {
  color: #667eea;
  font-weight: bold;
  font-size: 24px;
}

.positive {
  color: green;
}

.negative {
  color: red;
}
</style>
