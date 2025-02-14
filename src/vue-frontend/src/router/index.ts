import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store/userStore";
import { validate } from "@/api/auth";
import LoginVue from "@/components/auth/Login.vue";
import DefaultLayoutVue from "@/components/layout/DefaultLayout.vue";
import NotFoundVue from "@/components/auth/NotFound.vue";
import UserVue from "@/views/User.vue";
import LeaveRequestVue from "@/views/LeaveRequest.vue";
import { ERole } from "@/constants/role";
import LeaveManagementViewVue from "@/views/LeaveManagementView.vue";
import UserLeaveManagementViewVue from "@/views/UserLeaveManagementView.vue";
import RequestConfirmViewVue from "@/views/RequestConfirmView.vue";
import PasswordChangeVue from "@/components/auth/PasswordChange.vue";
import CalendarVue from "@/views/Calendar.vue";
import DocumentVue from "@/views/Document.vue";

const publicRoutes = {
  path: "/login",
  name: "login",
  component: LoginVue,
};

const routes = {
  path: "/",
  name: "home",
  meta: {
    requiresAuth: true,
  },
  redirect: "/calendar",
  component: DefaultLayoutVue,
  children: [
    {
      path: "/not-found",
      name: "not-found",
      component: NotFoundVue,
    },
    // Admin routes
    {
      path: "/admin/users",
      name: "admin-user-list",
      component: UserVue,
      meta: {
        requiresAuth: true,
        requiredRoles: [ERole.ADMIN],
      },
    },
    {
      path: "/admin/leave-management",
      name: "admin-leave-management",
      component: LeaveManagementViewVue,
      meta: {
        requiresAuth: true,
        requiredRoles: [ERole.ADMIN],
      },
    },
    {
      path: "/manager/user-leave-management",
      name: "manager-user-leave-management",
      component: UserLeaveManagementViewVue,
      meta: {
        requiresAuth: true,
        requiredRoles: [ERole.MANAGER],
      },
    },
    {
      path: "/manager/request-confirm",
      name: "manager-request-confirm",
      component: RequestConfirmViewVue,
      meta: {
        requiresAuth: true,
        requiredRoles: [ERole.MANAGER],
      },
    },
    {
      path: "/member/leave-requests",
      name: "member-leave-requests",
      component: LeaveRequestVue,
      meta: {
        requiresAuth: true,
        requiredRoles: [ERole.MEMBER],
      },
    },
    {
      path: "/calendar",
      name: "calendar",
      component: CalendarVue,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: "/document",
      name: "document",
      component: DocumentVue,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: "/change-password",
      name: "change-password",
      component: PasswordChangeVue,
      meta: {
        requiresAuth: true,
      },
    },
  ],
};

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(to) {
    if (to.hash) return { el: to.hash, behavior: "smooth", top: 60 };

    return { top: 0 };
  },
  routes: [
    {
      path: "/:pathMatch(.*)*",
      component: NotFoundVue,
    },
    routes,
    publicRoutes,
  ],
});

// Helper function: Check user roles
const hasRequiredRoles = (userRoles, requiredRoles) => {
  return requiredRoles
    ? userRoles.some((role) => requiredRoles.includes(role))
    : true;
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
      return next({ name: "unauthorized" });
    }
    return next();
  }
  // If not authenticated yet -> Call API validate token with timeout
  try {
    const response = await validate();
    const { status, data } = response;

    if (status === 200 && data.authenticated) {
      if (!hasRequiredRoles(data.roles, to.meta.requiredRoles)) {
        return next({ name: "unauthorized" });
      }
      return next();
    }
  } catch (error) {
    console.error("Error during token validation:", error);
  }
  // If error when call API (timeout/connect), direct to login
  next({ name: "login", query: { to: to.fullPath } });
});

export default router;
