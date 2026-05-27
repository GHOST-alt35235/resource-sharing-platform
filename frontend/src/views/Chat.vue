<template>
  <div class="chat">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="session-list">
          <h3>会话列表</h3>
          <div v-for="session in sessions" :key="session.id" 
               class="session-item" 
               :class="{ active: currentSession?.id === session.id }"
               @click="selectSession(session)">
            <div class="session-name">{{ getOtherName(session) }}</div>
            <div class="session-last">{{ session.lastMessage || '暂无消息' }}</div>
          </div>
          <el-empty v-if="sessions.length === 0" description="暂无会话" />
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card v-if="currentSession" class="chat-room">
          <div class="chat-header">
            <h3>{{ getOtherName(currentSession) }}</h3>
          </div>
          <div class="message-list" ref="messageList">
            <div v-for="msg in messages" :key="msg.id" 
                 class="message-item" 
                 :class="{ self: msg.senderId === currentUserId }">
              <div class="message-content">{{ msg.content }}</div>
              <div class="message-time">{{ formatDate(msg.createTime) }}</div>
            </div>
          </div>
          <div class="chat-input">
            <el-input v-model="messageContent" placeholder="输入消息..." @keyup.enter="sendMessage" />
            <el-button type="primary" @click="sendMessage">发送</el-button>
          </div>
        </el-card>
        <el-card v-else class="empty-chat">
          <el-empty description="请选择一个会话开始聊天" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/axios'

const currentUserId = ref(0)
const sessions = ref([])
const currentSession = ref(null)
const messages = ref([])
const messageContent = ref('')
const messageList = ref(null)

const getOtherName = (session) => {
  return session.user1Id === currentUserId.value ? session.user2Nickname : session.user1Nickname
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const loadSessions = async () => {
  try {
    const res = await request.get('/chat/sessions')
    sessions.value = res.data || []
  } catch (e) {
  }
}

const selectSession = async (session) => {
  currentSession.value = session
  await loadMessages(session.id)
}

const loadMessages = async (sessionId) => {
  try {
    const res = await request.get(`/chat/messages/${sessionId}`)
    messages.value = res.data.messages || []
    scrollToBottom()
  } catch (e) {
  }
}

const sendMessage = async () => {
  if (!messageContent.value.trim() || !currentSession.value) return

  try {
    await request.post('/chat/message', {
      sessionId: currentSession.value.id,
      content: messageContent.value,
      type: 1
    })
    messageContent.value = ''
    await loadMessages(currentSession.value.id)
  } catch (e) {
  }
}

const scrollToBottom = () => {
  setTimeout(() => {
    if (messageList.value) {
      messageList.value.scrollTop = messageList.value.scrollHeight
    }
  }, 100)
}

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  currentUserId.value = user.id || 0
  loadSessions()
})
</script>

<style scoped>
.chat {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  height: calc(100vh - 40px);
}

.session-list {
  height: 100%;
}

.session-list h3 {
  margin-bottom: 20px;
}

.session-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.session-item:hover {
  background-color: #f5f5f5;
}

.session-item.active {
  background-color: #e8f4fd;
}

.session-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.session-last {
  font-size: 12px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-room {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.message-item {
  margin-bottom: 15px;
}

.message-item.self {
  text-align: right;
}

.message-content {
  display: inline-block;
  padding: 10px 15px;
  background-color: #667eea;
  color: white;
  border-radius: 10px;
  max-width: 70%;
}

.message-item.self .message-content {
  background-color: #f0f0f0;
  color: #333;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.chat-input {
  display: flex;
  gap: 10px;
  padding: 15px;
  border-top: 1px solid #eee;
}

.chat-input el-input {
  flex: 1;
}

.empty-chat {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
