import { createRouter, createWebHistory } from 'vue-router';
import DefaultLayout from '../views/DefaultLayout.vue';
import LoginView from '../views/LoginView.vue';
import UnauthorizedView from '../views/UnauthorizedView.vue';
import { useUserStore } from '@/store/userStore';
import PasChangeView from '@/views/PasChangeView.vue';
import UserManagementView from '@/views/UserManagementView.vue';
import LeaveApplyView from '@/views/LeaveApplyView.vue';
import LeaveManagementView from '@/views/LeaveManagementView.vue';
import UserLeaveManagementView from '@/views/UserLeaveManagementView.vue';
import RequstConfirmView from '@/views/RequstConfirmView.vue';
import ProfleView from '@/views/ProfleView.vue';
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
    path: '/userManagement',
    name: 'userManagement',
    component: UserManagementView,
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
    path: '/leaveApply',
    name: 'leaveApply',
    component: LeaveApplyView,
  },
  // パスワード 変更
  {
    path: '/changePassword',
    name: 'changePassword',
    component: PasChangeView,
  },
  // ユーザー プロフィル
  {
    path: '/profile',
    name: 'profile',
    component: ProfleView,
  },
  {
    path: '/admin/home',
    name: 'home-admin',
    component: DefaultLayout,
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
