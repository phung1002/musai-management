import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import UnauthorizedView from '../views/UnauthorizedView.vue';
import ListUser from '@/views/user/ListUser.vue';
import axiosIns from '@/plugins/axios';

import { useUserStore } from '@/store/userStore';

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
  {
    path: '/unauthorized',
    name: 'unauthorized',
    component: UnauthorizedView,
  },
  {
    path: '/home',
    name: 'home',
    component: HomeView,
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/home',
    name: 'home-admin',
    component: HomeView,
    meta: {
      requiresAuth: true,
      requiredRoles: ['ROLE_ADMIN']
    },
  },
  {
    path: '/admin/user',
    name: 'admin-user',
    component: ListUser,
    meta: {
      requiresAuth: true,
      requiredRoles: ['ROLE_ADMIN']
    },
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// Navigation guard
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  // If need to check auth
  if (to.meta.requiresAuth) {
    if (userStore.authenticated) {
      // If user don't have role
      if (to.meta.requiredRoles && !userStore.roles.some(role => to.meta.requiredRoles.includes(role))) {
        next({ name: 'unauthorized' });
      }
      next();
    } else {
      // Case not authenticated yet
      try {
        // Call API validate token with timeout
        const response = await axiosIns.get('/auth/validate', { timeout: 5000 }); // Timeout 5s
        // Check status == 200 is success
        if (response.status === 200) {
          if (response.data.authenticated) {
            // Check auth and role
            if (to.meta.requiredRoles && !response.data.roles.some(role => to.meta.requiredRoles.includes(role))) {
              next({ name: 'unauthorized' });
            } else {
              next();
            }
          } else {
            // If not authenticated, direct to login
            next({ name: 'login', query: { to: to.fullPath } });
          }
        } else {
          // If status != 200 is error, direct to login
          next({ name: 'login', query: { to: to.fullPath } });
        }
      } catch (error) {
        // If error when call API (timeout/connect), direct to login
        console.error('Error during token validation:', error);
        next({ name: 'login', query: { to: to.fullPath } });
      }
    }
  } else {
    // If don't need to authenticated
    next();
  }
});

export default router;
