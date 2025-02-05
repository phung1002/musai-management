import { createRouter, createWebHistory } from 'vue-router';
import DefaultLayout from '../components/layout/DefaultLayout.vue';
import LoginView from '../components/auth/LoginView.vue';
import UnauthorizedView from '../views/UnauthorizedView.vue';
import ListUser from '@/views/lists/User.vue';
import { useUserStore } from '@/store/userStore';
import PasChangeView from '@/views/PasswordChange.vue';
import leaveRequstList from '@/views/LeaveRequestView.vue';
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
  // ホーム
  {
    path: '/home',
    name: 'home',
    component: DefaultLayout,
    meta: { requiresAuth: true },
  },
  // ログイン
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
   // ユーザー 管理
  {
    path: '/admin/user',
    name: 'admin-user-list',
    component: ListUser,
    meta: {
      requiresAuth: true,
      requiredRoles: [ERole.ADMIN]
    },
  },
  // 休暇 管理
  {
    path: '/leaveManagement',
    name: 'leaveManagement',
    component: LeaveManagementView,
  },
  // 申請 管理
  {
    path: '/userLeaveManagement',
    name: 'userLeaveManagement',
    component: UserLeaveManagementView,
  },
  // 申請 確認
  {
    path: '/requstConfirm',
    name: 'requstConfirm',
    component: RequstConfirmView,
  },
  // 休暇 申請
  {
    path: '/leaveRequstList',
    name: 'leaveRequstList',
    component: leaveRequstList,
  },
  // パスワード 変更
  {
    path: '/changePassword',
    name: 'changePassword',
    component: PasChangeView,
  },
  {
    path: '/admin/home',
    name: 'home-admin',
    component: DefaultLayout,
    meta: {
      requiresAuth: true,
      requiredRoles: [ERole.ADMIN]
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
