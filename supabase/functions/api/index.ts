import { serve } from 'https://deno.land/std@0.168.0/http/server.ts'

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
  'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
}

// 模拟数据
const mockUsers = [
  { id: 1, username: 'admin', password: 'admin123', role: 'admin', points: 1000, email: 'admin@example.com', createdAt: new Date().toISOString() },
  { id: 2, username: 'user1', password: '123456', role: 'user', points: 200, email: 'user1@example.com', createdAt: new Date().toISOString() },
  { id: 3, username: 'user2', password: '123456', role: 'user', points: 150, email: 'user2@example.com', createdAt: new Date().toISOString() }
]

const mockResources = [
  { id: 1, title: 'Java编程入门教程', description: '适合初学者的Java编程教程，包含基础语法和实战案例', uploaderId: 2, uploaderName: 'user1', price: 10, fileType: '.pdf', fileSize: 2097152, downloadCount: 45, status: 'approved', createdAt: '2024-01-15' },
  { id: 2, title: 'Spring Boot实战项目', description: '完整的Spring Boot电商项目源码', uploaderId: 2, uploaderName: 'user1', price: 20, fileType: '.zip', fileSize: 10485760, downloadCount: 32, status: 'approved', createdAt: '2024-02-20' },
  { id: 3, title: 'Vue3组件库开发指南', description: '从零开始学习Vue3组件库开发', uploaderId: 3, uploaderName: 'user2', price: 15, fileType: '.pdf', fileSize: 3145728, downloadCount: 28, status: 'approved', createdAt: '2024-03-10' },
  { id: 4, title: 'MySQL数据库优化笔记', description: 'MySQL性能优化实战经验总结', uploaderId: 2, uploaderName: 'user1', price: 5, fileType: '.docx', fileSize: 524288, downloadCount: 67, status: 'approved', createdAt: '2024-04-05' },
  { id: 5, title: 'Docker容器化部署教程', description: 'Docker入门到精通的完整教程', uploaderId: 3, uploaderName: 'user2', price: 12, fileType: '.pdf', fileSize: 4194304, downloadCount: 55, status: 'approved', createdAt: '2024-04-22' }
]

let nextUserId = 4
let nextResourceId = 6

const sessions = new Map()

serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders })
  }

  try {
    const url = new URL(req.url)
    const path = url.pathname
    const method = req.method

    console.log(`Request: ${method} ${path}`)

    let response

    // 统计
    if (path.endsWith('/statistics') && method === 'GET') {
      response = {
        code: 200,
        data: {
          resources: mockResources.length,
          users: mockUsers.length,
          downloads: mockResources.reduce((sum, r) => sum + r.downloadCount, 0)
        }
      }
    }
    // 登录
    else if (path.endsWith('/auth/login') && method === 'POST') {
      const body = await req.json()
      const user = mockUsers.find(u => u.username === body.username && u.password === body.password)
      
      if (user) {
        const token = 'token_' + Math.random().toString(36).substr(2)
        sessions.set(token, user)
        const { password, ...userWithoutPassword } = user
        response = {
          code: 200,
          data: { token, user: userWithoutPassword },
          message: '登录成功'
        }
      } else {
        response = { code: 400, message: '用户名或密码错误' }
      }
    }
    // 注册
    else if (path.endsWith('/auth/register') && method === 'POST') {
      const body = await req.json()
      const existingUser = mockUsers.find(u => u.username === body.username)
      
      if (existingUser) {
        response = { code: 400, message: '用户名已存在' }
      } else {
        const newUser = {
          id: nextUserId++,
          username: body.username,
          password: body.password,
          email: body.email || '',
          role: 'user',
          points: 100,
          createdAt: new Date().toISOString()
        }
        mockUsers.push(newUser)
        const token = 'token_' + Math.random().toString(36).substr(2)
        sessions.set(token, newUser)
        const { password, ...userWithoutPassword } = newUser
        response = {
          code: 200,
          data: { token, user: userWithoutPassword },
          message: '注册成功'
        }
      }
    }
    // 资源列表
    else if (path.endsWith('/resource/list') && method === 'GET') {
      const keyword = url.searchParams.get('keyword')
      let resources = mockResources.filter(r => r.status === 'approved')
      
      if (keyword) {
        resources = resources.filter(r => 
          r.title.toLowerCase().includes(keyword.toLowerCase()) ||
          r.description.toLowerCase().includes(keyword.toLowerCase())
        )
      }
      
      response = { code: 200, data: resources }
    }
    // 资源详情
    else if (path.match(/\/resource\/\d+$/) && method === 'GET') {
      const id = parseInt(path.split('/').pop() || '0')
      const resource = mockResources.find(r => r.id === id)
      if (resource) {
        response = { code: 200, data: resource }
      } else {
        response = { code: 404, message: '资源不存在' }
      }
    }
    // 我的资源
    else if (path.endsWith('/resource/my') && method === 'GET') {
      const token = req.headers.get('authorization')
      const user = sessions.get(token)
      if (!user) {
        response = { code: 401, message: '未登录' }
      } else {
        const myResources = mockResources.filter(r => r.uploaderId === user.id)
        response = { code: 200, data: myResources }
      }
    }
    // 创建资源
    else if (path.endsWith('/resource') && method === 'POST') {
      const token = req.headers.get('authorization')
      const user = sessions.get(token)
      if (!user) {
        response = { code: 401, message: '未登录' }
      } else {
        const body = await req.json()
        const newResource = {
          id: nextResourceId++,
          title: body.title,
          description: body.description,
          uploaderId: user.id,
          uploaderName: user.username,
          price: body.price || 5,
          fileType: body.fileType || '.pdf',
          fileSize: body.fileSize || 1024000,
          downloadCount: 0,
          status: 'pending',
          createdAt: new Date().toISOString()
        }
        mockResources.push(newResource)
        response = { code: 200, data: newResource, message: '资源上传成功，等待审核' }
      }
    }
    // 管理员获取所有资源
    else if (path.endsWith('/resource/admin/all') && method === 'GET') {
      response = { code: 200, data: mockResources }
    }
    // 审核通过
    else if (path.match(/\/resource\/approve\/\d+$/) && method === 'PUT') {
      const id = parseInt(path.split('/').pop() || '0')
      const resource = mockResources.find(r => r.id === id)
      if (resource) {
        resource.status = 'approved'
        response = { code: 200, message: '审核通过' }
      } else {
        response = { code: 404, message: '资源不存在' }
      }
    }
    // 审核拒绝
    else if (path.match(/\/resource\/reject\/\d+$/) && method === 'PUT') {
      const id = parseInt(path.split('/').pop() || '0')
      const resource = mockResources.find(r => r.id === id)
      if (resource) {
        resource.status = 'rejected'
        response = { code: 200, message: '已拒绝' }
      } else {
        response = { code: 404, message: '资源不存在' }
      }
    }
    // 下载资源
    else if (path.match(/\/resource\/download\/\d+$/) && method === 'POST') {
      const id = parseInt(path.split('/').pop() || '0')
      const resource = mockResources.find(r => r.id === id)
      if (resource) {
        resource.downloadCount++
        response = { code: 200, message: '下载成功' }
      } else {
        response = { code: 404, message: '资源不存在' }
      }
    }
    // 用户列表
    else if (path.endsWith('/user/list') && method === 'GET') {
      const users = mockUsers.map(({ password, ...u }) => u)
      response = { code: 200, data: users }
    }
    // 积分记录
    else if (path.endsWith('/points/record') && method === 'GET') {
      response = {
        code: 200,
        data: [
          { id: 1, type: 'register', amount: 100, description: '注册奖励', createdAt: '2024-01-01' },
          { id: 2, type: 'upload', amount: 10, description: '上传资源奖励', createdAt: '2024-01-15' },
          { id: 3, type: 'download', amount: -10, description: '下载资源消耗', createdAt: '2024-02-01' }
        ]
      }
    }
    // 其他情况
    else {
      response = { code: 404, message: '接口不存在' }
    }

    return new Response(JSON.stringify(response), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: response.code === 200 ? 200 : 400
    })

  } catch (error) {
    console.error('Error:', error)
    return new Response(JSON.stringify({ code: 500, message: '服务器错误' }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 500
    })
  }
})
