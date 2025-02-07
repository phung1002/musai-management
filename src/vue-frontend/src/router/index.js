import { createRouter, createWebHistory } from 'vue-router';
import DefaultLayout from '../components/layout/DefaultLayout.vue';
import LoginView from '../components/auth/LoginView.vue';
import UnauthorizedView from '../views/UnauthorizedView.vue';
import ListUser from '@/views/lists/User.vue';
import { useUserStore } from '@/store/userStore';
import PasChangeView from '@/components/auth/PasswordChangeForm.vue';
import leaveRequstList from '@/views/lists/LeaveRequest.vue';
import LeaveManagementView from '@/views/LeaveManagementView.vue';
import UserLeaveManagementView from '@/views/UserLeaveManagementView.vue';
import RequstConfirmView from '@/views/RequstConfirmView.vue';
import { validate } from '@/api/auth';
import { ERole } from '@/constants/role';

const routes = [
  {
    path: '/unauthorized',
    name: 'unauthorized',
    component: UnauthorizedView,
  },
  {
    path: '/home',
    name: 'home',
    component: DefaultLayout,
    meta: { requiresAuth: true },
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
  // Admin routes
  {
    path: '/admin/users',
    name: 'admin-user-list',
    component: ListUser,
    meta: {
      requiresAuth: true,
      requiredRoles: [ERole.ADMIN],
    },
  },
  {
    path: '/admin/leave-management',
    name: 'admin-leave-management',
    component: LeaveManagementView,
    meta: {
      requiresAuth: true,
      requiredRoles: [ERole.ADMIN],
    },
  },
  {
    path: '/manager/user-leave-management',
    name: 'manager-user-leave-management',
    component: UserLeaveManagementView,
    meta: {
      requiresAuth: true,
      requiredRoles: [ERole.MANAGER],
    },
  },
  {
    path: '/manager/request-confirm',
    name: 'manager-request-confirm',
    component: RequstConfirmView,
    meta: {
      requiresAuth: true,
      requiredRoles: [ERole.MANAGER],
    },
  },
  {
    path: '/member/leave-requests',
    name: 'member-leave-requests',
    component: leaveRequstList,
    meta: {
      requiresAuth: true,
      requiredRoles: [ERole.MEMBER],
    },
  },
  {
    path: '/account/change-password',
    name: 'change-password',
    component: PasChangeView,
    meta: {
      requiresAuth: true,
    },
  },
  {
    path: '/admin/home',
    name: 'home-admin',
    component: DefaultLayout,
    meta: {
      requiresAuth: true,
      requiredRoles: [ERole.ADMIN],
    },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// Helper function: Check user roles
const hasRequiredRoles = (userRoles, requiredRoles) => {
  return requiredRoles ? userRoles.some(role => requiredRoles.includes(role)) : true;
};

// Navigation guard
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  // If don't need to check auth
  if (!to.meta.requiresAuth) {
    return next();
  }
  // If authenticated
  if (userStore.authenticated) {
    if (!hasRequiredRoles(userStore.roles, to.meta.requiredRoles)) {
      return next({ name: 'unauthorized' });
    }
    return next();
  }
  // If not authenticated yet -> Call API validate token with timeout
  try {
    const response = await validate();
    const { status, data } = response;

    if (status === 200 && data.authenticated) {
      if (!hasRequiredRoles(data.roles, to.meta.requiredRoles)) {
        return next({ name: 'unauthorized' });
      }
      return next();
    }
  } catch (error) {
    console.error('Error during token validation:', error);
  }
  // If error when call API (timeout/connect), direct to login
  next({ name: 'login', query: { to: to.fullPath } });
});

export default router;
