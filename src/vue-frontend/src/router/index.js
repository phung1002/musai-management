import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import UnauthorizedView from '../views/UnauthorizedView.vue';
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
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// Navigation guard
router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  const isAuthenticated = !!userStore.getAccessToken;

  if (to.meta.requiresAuth  && !isAuthenticated) {
    // Redirect to login if not login yet
    next({ name: 'login', query: { to: to.fullPath } });
  }
  else if (to.meta.requiredRoles && !to.meta.requiredRoles.some(role => userStore.hasRole(role))) {
    // Redirect to unauthorized screen if doesn't have role avaiable
    next({ name: 'unauthorized' });
  }
  else {
    // Continue if all avaiable
    next();
  }
});

export default router;
