<template>
  <div class="admin-users-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="user-count">
        总用户数：{{ total }}
      </div>
    </div>

    <div class="search-bar">
      <input 
        type="text" 
        v-model="searchKeyword" 
        placeholder="搜索用户名或昵称"
        @keyup.enter="searchUsers"
      />
      <button @click="searchUsers" :disabled="loading">搜索</button>
      <button @click="resetSearch" :disabled="loading">重置</button>
    </div>

    <div class="user-table" v-if="!loading">
      <div v-if="users.length === 0" class="empty-state">
        <div class="empty-icon">👥</div>
        <p>暂无用户数据</p>
        <button @click="fetchUsers(1)" class="refresh-btn">刷新数据</button>
      </div>
      <table v-else>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>昵称</th>
            <th>手机号</th>
            <th>积分</th>
            <th>角色</th>
            <th>状态</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.nickname }}</td>
            <td>{{ user.phone }}</td>
            <td>{{ user.points }}</td>
            <td>
              <span :class="['role-tag', user.role]">{{ user.role === 'admin' ? '管理员' : '普通用户' }}</span>
            </td>
            <td>
              <span :class="['status-tag', user.status === 1 ? 'active' : 'disabled']">
                {{ user.status === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>{{ formatTime(user.createTime) }}</td>
            <td>
              <button 
                @click="toggleUserStatus(user)" 
                :class="['action-btn', user.status === 1 ? 'danger' : 'success']"
                :disabled="loading"
              >
                {{ user.status === 1 ? '禁用' : '启用' }}
              </button>
              <button 
                @click="resetUserPassword(user)" 
                class="action-btn warning"
                :disabled="loading"
              >
                重置密码
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div class="pagination" v-if="totalPages > 1 && !loading">
      <button @click="prevPage" :disabled="currentPage === 1 || loading">上一页</button>
      <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
      <button @click="nextPage" :disabled="currentPage === totalPages || loading">下一页</button>
    </div>

    <div v-if="showPasswordModal" class="modal-overlay" @click="closePasswordModal">
      <div class="modal-content" @click.stop>
        <h3>重置密码</h3>
        <p>确定要将用户 "{{ resetTargetUser?.username }}" 的密码重置为默认密码吗？</p>
        <div class="modal-actions">
          <button @click="confirmResetPassword" :disabled="loading">确定</button>
          <button @click="closePasswordModal" class="cancel" :disabled="loading">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from '../api/axios';

export default {
  name: 'AdminUsers',
  setup() {
    const users = ref([]);
    const total = ref(0);
    const currentPage = ref(1);
    const totalPages = ref(1);
    const searchKeyword = ref('');
    const showPasswordModal = ref(false);
    const resetTargetUser = ref(null);
    const loading = ref(false);
    const isSearching = ref(false);

    const fetchUsers = async (pageNum = 1, pageSize = 10) => {
      loading.value = true;
      isSearching.value = false;
      try {
        const response = await axios.get('/user/list', {
          params: { pageNum, pageSize }
        });
        users.value = response.data.list || [];
        total.value = response.data.total || 0;
        currentPage.value = response.data.pageNum || 1;
        totalPages.value = response.data.totalPages || 1;
      } catch (error) {
        console.error('获取用户列表失败:', error);
        alert(error.response?.data?.message || '获取用户列表失败');
      } finally {
        loading.value = false;
      }
    };

    const searchUsers = async () => {
      if (!searchKeyword.value.trim()) {
        fetchUsers(1);
        return;
      }
      
      loading.value = true;
      isSearching.value = true;
      try {
        const response = await axios.get('/user/search', {
          params: { 
            keyword: searchKeyword.value.trim(),
            pageNum: 1,
            pageSize: 10
          }
        });
        users.value = response.data.list || [];
        total.value = response.data.total || 0;
        currentPage.value = response.data.pageNum || 1;
        totalPages.value = response.data.totalPages || 1;
      } catch (error) {
        console.error('搜索失败:', error);
        alert(error.response?.data?.message || '搜索失败');
      } finally {
        loading.value = false;
      }
    };

    const resetSearch = () => {
      searchKeyword.value = '';
      isSearching.value = false;
      fetchUsers(1);
    };

    const prevPage = () => {
      if (currentPage.value > 1) {
        if (isSearching.value) {
          searchUsersWithPage(currentPage.value - 1);
        } else {
          fetchUsers(currentPage.value - 1);
        }
      }
    };

    const nextPage = () => {
      if (currentPage.value < totalPages.value) {
        if (isSearching.value) {
          searchUsersWithPage(currentPage.value + 1);
        } else {
          fetchUsers(currentPage.value + 1);
        }
      }
    };

    const searchUsersWithPage = async (pageNum) => {
      loading.value = true;
      try {
        const response = await axios.get('/user/search', {
          params: { 
            keyword: searchKeyword.value.trim(),
            pageNum,
            pageSize: 10
          }
        });
        users.value = response.data.list || [];
        total.value = response.data.total || 0;
        currentPage.value = response.data.pageNum || 1;
        totalPages.value = response.data.totalPages || 1;
      } catch (error) {
        console.error('搜索失败:', error);
        alert(error.response?.data?.message || '搜索失败');
      } finally {
        loading.value = false;
      }
    };

    const toggleUserStatus = async (user) => {
      const action = user.status === 1 ? 'disable' : 'enable';
      const actionText = user.status === 1 ? '禁用' : '启用';
      
      loading.value = true;
      try {
        const response = await axios.put(`/user/${action}/${user.id}`);
        if (response.data.code === 200) {
          user.status = user.status === 1 ? 0 : 1;
          alert(`用户已${actionText}`);
        } else {
          alert(response.data.message || '操作失败');
        }
      } catch (error) {
        console.error('操作失败:', error);
        alert(error.response?.data?.message || '操作失败');
      } finally {
        loading.value = false;
      }
    };

    const resetUserPassword = (user) => {
      resetTargetUser.value = user;
      showPasswordModal.value = true;
    };

    const closePasswordModal = () => {
      showPasswordModal.value = false;
      resetTargetUser.value = null;
    };

    const confirmResetPassword = async () => {
      if (!resetTargetUser.value) return;
      
      loading.value = true;
      try {
        const response = await axios.put(`/user/resetPassword/${resetTargetUser.value.id}`, {
          newPassword: '123456'
        });
        if (response.data.code === 200) {
          alert('密码已重置为 123456');
          closePasswordModal();
        } else {
          alert(response.data.message || '重置密码失败');
        }
      } catch (error) {
        console.error('重置密码失败:', error);
        alert(error.response?.data?.message || '重置密码失败');
      } finally {
        loading.value = false;
      }
    };

    const formatTime = (time) => {
      if (!time) return '-';
      return new Date(time).toLocaleString('zh-CN');
    };

    onMounted(() => {
      fetchUsers(1);
    });

    return {
      users,
      total,
      currentPage,
      totalPages,
      searchKeyword,
      showPasswordModal,
      resetTargetUser,
      loading,
      fetchUsers,
      searchUsers,
      resetSearch,
      prevPage,
      nextPage,
      toggleUserStatus,
      resetUserPassword,
      closePasswordModal,
      confirmResetPassword,
      formatTime
    };
  }
};
</script>

<style scoped>
.admin-users-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.user-count {
  font-size: 16px;
  color: #666;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-bar input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-bar button {
  padding: 10px 20px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-bar button:hover {
  background-color: #67B8F8;
}

.user-table {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.user-table table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th,
.user-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.user-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #666;
}

.user-table tr:hover {
  background-color: #f9fafc;
}

.role-tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.role-tag.admin {
  background-color: #E6A23C;
  color: white;
}

.role-tag.user {
  background-color: #67C23A;
  color: white;
}

.status-tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-tag.active {
  background-color: #67C23A;
  color: white;
}

.status-tag.disabled {
  background-color: #F56C6C;
  color: white;
}

.action-btn {
  padding: 4px 12px;
  margin-right: 8px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.action-btn.danger {
  background-color: #F56C6C;
  color: white;
}

.action-btn.success {
  background-color: #67C23A;
  color: white;
}

.action-btn.warning {
  background-color: #E6A23C;
  color: white;
}

.action-btn:hover {
  opacity: 0.8;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:hover:not(:disabled) {
  background-color: #f5f7fa;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 24px;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
}

.modal-content h3 {
  margin: 0 0 16px 0;
  color: #333;
}

.modal-content p {
  color: #666;
  margin-bottom: 20px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-actions button {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.modal-actions button:not(.cancel) {
  background-color: #409EFF;
  color: white;
}

.modal-actions button.cancel {
  background-color: #f5f7fa;
  color: #666;
}

.modal-actions button:hover {
  opacity: 0.8;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  margin-bottom: 20px;
}

.refresh-btn {
  padding: 10px 24px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.refresh-btn:hover {
  background-color: #67B8F8;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 16px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #409EFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  font-size: 16px;
  color: #666;
}
</style>
