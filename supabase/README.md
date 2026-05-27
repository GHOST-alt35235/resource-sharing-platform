# Supabase Edge Functions 部署指南

## 📋 前置准备

1. 已创建 Supabase 项目
2. 已安装 Supabase CLI 或使用 Supabase Dashboard

## 🚀 部署步骤

### 方法一：通过 Supabase Dashboard 部署（推荐，最简单）

1. 打开 [Supabase Dashboard](https://supabase.com/dashboard)
2. 进入你的项目 → **Edge Functions**
3. 点击 **New Function**
4. 函数名称：`api`
5. 将 `supabase/functions/api/index.ts` 的代码复制进去
6. 点击 **Deploy**

### 方法二：使用 Supabase CLI 部署

1. 安装 Supabase CLI
   ```bash
   # Windows (PowerShell)
   iwr -useb https://supabase.com/cli/install.ps1 | iex

   # macOS/Linux
   brew install supabase/tap/supabase
   ```

2. 登录 Supabase
   ```bash
   supabase login
   ```

3. 链接你的项目
   ```bash
   supabase link --project-ref YOUR_PROJECT_REF
   ```

4. 部署函数
   ```bash
   supabase functions deploy api
   ```

## 🔑 获取 API URL

部署成功后，你的 Edge Function URL 格式为：
```
https://YOUR_PROJECT_REF.supabase.co/functions/v1/api
```

## 📝 测试账号

- **管理员账号**
  - 用户名：`admin`
  - 密码：`admin123`

- **普通用户**
  - 用户名：`user1`
  - 密码：`123456`
  - 用户名：`user2`
  - 密码：`123456`

## ⚙️ 配置前端

将获取到的 Edge Function URL 配置到前端的 `frontend/src/api/axios.js`：

```javascript
const instance = axios.create({
  baseURL: 'https://YOUR_PROJECT_REF.supabase.co/functions/v1',
  timeout: 10000
})
```

## 📚 已实现的 API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /statistics | 获取统计数据 |
| POST | /auth/login | 用户登录 |
| POST | /auth/register | 用户注册 |
| GET | /resource/list | 获取资源列表 |
| GET | /resource/:id | 获取资源详情 |
| GET | /resource/my | 获取我的资源（需登录） |
| POST | /resource | 上传资源（需登录） |
| GET | /resource/admin/all | 管理员获取所有资源 |
| PUT | /resource/approve/:id | 审核通过资源 |
| PUT | /resource/reject/:id | 审核拒绝资源 |
| POST | /resource/download/:id | 下载资源 |
| GET | /user/list | 获取用户列表 |
| GET | /points/record | 获取积分记录 |

## 📌 注意事项

1. 本方案使用内存存储，重启后数据会重置（仅用于演示）
2. 如需持久化存储，请使用 Supabase Database
3. CORS 已配置允许所有来源访问
