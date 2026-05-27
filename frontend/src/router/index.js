import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import ResourceList from '../views/ResourceList.vue'
import ResourceDetail from '../views/ResourceDetail.vue'
import MyResources from '../views/MyResources.vue'
import UploadResource from '../views/UploadResource.vue'
import PointsRecord from '../views/PointsRecord.vue'
import Chat from '../views/Chat.vue'
import UserProfile from '../views/UserProfile.vue'
import AdminResources from '../views/AdminResources.vue'
import AdminUsers from '../views/AdminUsers.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/resources', name: 'ResourceList', component: ResourceList },
  { path: '/resource/:id', name: 'ResourceDetail', component: ResourceDetail },
  { path: '/my-resources', name: 'MyResources', component: MyResources, meta: { requiresAuth: true } },
  { path: '/upload', name: 'UploadResource', component: UploadResource, meta: { requiresAuth: true } },
  { path: '/points', name: 'PointsRecord', component: PointsRecord, meta: { requiresAuth: true } },
  { path: '/chat', name: 'Chat', component: Chat, meta: { requiresAuth: true } },
  { path: '/profile', name: 'UserProfile', component: UserProfile, meta: { requiresAuth: true } },
  { path: '/admin/resources', name: 'AdminResources', component: AdminResources, meta: { requiresAuth: true } },
  { path: '/admin/users', name: 'AdminUsers', component: AdminUsers, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
