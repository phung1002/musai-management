import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store/userStore";
import LoginVue from "@/components/auth/Login.vue";
import DefaultLayoutVue from "@/components/layout/DefaultLayout.vue";
import NotFoundVue from "@/components/auth/NotFound.vue";
import UnauthorizedVue from "@/components/auth/Unauthorized.vue";
import UserVue from "@/views/User.vue";
import { ERole } from "@/constants/role";

import UserLeaveManagementViewVue from "@/views/UserLeave.vue";
import RequestConfirmViewVue from "@/views/LeaveResponse.vue";
import PasswordChangeVue from "@/views/PasswordChange.vue";
import CalendarVue from "@/views/Calendar.vue";
import DocumentVue from "@/views/Document.vue";
import LeaveType from "@/views/LeaveType.vue";
import LeaveRequestVue from "@/views/LeaveRequest.vue";
import { logout, validateToken } from "@/api/auth";

const publicRoutes = [
  {
    path: "/login",
    name: "login",
    component: LoginVue,
  },
  {
    path: "/unauthorized",
    name: "unauthorized",
    component: UnauthorizedVue,
  },
];

const routes = {
  path: "/",
  name: "home",
  meta: {
    requiresAuth: true,
  },
  redirect: "/calendar",
  component: DefaultLayoutVue,
  children: [
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
      component: LeaveType,
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
      path: "/member/leave-applications",
      name: "member-leave-applications",
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
    ...publicRoutes,
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
  const isAuthenticated = userStore.authenticated;

  // Check if authentication is required or if the user is authenticated
  if (!to.meta.requiresAuth || isAuthenticated) {
    try {
      // Validate token if authenticated
      if (isAuthenticated && !(await validateToken())) {
        await logout();
      }
      // Check if user has required roles
      if (
        isAuthenticated &&
        !hasRequiredRoles(userStore.roles, to.meta.requiredRoles)
      ) {
        return next({ name: "unauthorized" });
      }
      return next();
    } catch (error) {
      console.error("Token validation failed:", error);
      await logout();
    }
  }
  return next({ name: "login", query: { to: to.fullPath } });
});

export default router;
