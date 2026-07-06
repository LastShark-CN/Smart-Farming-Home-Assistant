import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/HomePage.vue'),
    meta: {
      requireAuth: false
    }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginRegister.vue'),
    meta: {
      requireAuth: false
    }
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: {
      requireAuth: true,
      roles: ['super_admin', 'farmer']
    }
  },
  {
    path: '/admin/users',
    name: 'adminUsers',
    component: () => import('../views/admin/UserManagement.vue'),
    meta: {
      requireAuth: true,
      roles: ['super_admin']
    }
  },
  {
    path: '/admin/settings',
    name: 'adminSettings',
    component: () => import('../views/admin/Settings.vue'),
    meta: {
      requireAuth: true,
      roles: ['super_admin']
    }
  },
  {
    path: '/farmer/my-farm',
    name: 'myFarm',
    component: () => import('../views/farmer/MyFarm.vue'),
    meta: {
      requireAuth: true,
      roles: ['farmer']
    }
  },
  {
    path: '/farmer/devices',
    name: 'devices',
    component: () => import('../views/farmer/DeviceManagement.vue'),
    meta: {
      requireAuth: true,
      roles: ['farmer']
    }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('../views/Profile.vue'),
    meta: {
      requireAuth: true,
      roles: ['super_admin', 'farmer']
    }
  },
  {
    path: '/farmer/crops',
    name: 'crops',
    component: () => import('../views/farmer/CropManagement.vue'),
    meta: {
      requireAuth: true,
      roles: ['farmer']
    }
  },
  {
    path: '/ai-chat',
    name: 'aiChat',
    component: () => import('../views/AIChat.vue'),
    meta: {
      requireAuth: true,
      roles: ['super_admin', 'farmer']
    }
  },
  {
    path: '/farmer/plant-records',
    name: 'plantRecords',
    component: () => import('../views/PlantRecord.vue'),
    meta: {
      requireAuth: true,
      roles: ['farmer']
    }
  },
  {
    path: '/easter-egg',
    name: 'easterEgg',
    component: () => import('../views/EasterEgg.vue'),
    meta: {
      requireAuth: false
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn
  const userRole = userStore.role

  if (to.meta.requireAuth) {
    if (!isLoggedIn) {
      next('/login')
      return
    }

    if (to.meta.roles && to.meta.roles.length > 0) {
      if (!to.meta.roles.includes(userRole)) {
        next('/dashboard')
        return
      }
    }

    next()
  } else {
    if (isLoggedIn && to.path === '/login') {
      next('/dashboard')
      return
    }
    next()
  }
})

export default router

export { router }
