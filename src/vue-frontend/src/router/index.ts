import { createRouter, createWebHistory } from "vue-router";
import { useEmployeeStore } from "@/store/employeeStore";
import LoginVue from "@/components/auth/Login.vue";
import DefaultLayoutVue from "@/components/layout/DefaultLayout.vue";
import NotFoundVue from "@/components/auth/NotFound.vue";
import UnauthorizedVue from "@/components/auth/Unauthorized.vue";
import EmployeeVue from "@/views/Employee.vue";
import { ERole } from "@/constants/role";

import EmployeeManagementVue from "@/views/EmployeeLeave.vue";
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
      path: "/admin/employees",
      name: "admin-employee-list",
      component: EmployeeVue,
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
      path: "/manager/employee-leave-management",
      name: "manager-employee-leave-management",
      component: EmployeeManagementVue,
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

// Helper function: Check employee roles
const hasRequiredRoles = (employeeRoles, requiredRoles) => {
  return requiredRoles
    ? employeeRoles.some((role) => requiredRoles.includes(role))
    : true;
};

// Navigation guard
router.beforeEach(async (to, from, next) => {
  const employeeStore = useEmployeeStore();
  const isAuthenticated = employeeStore.authenticated;

  // Check if authentication is required or if authenticated
  if (!to.meta.requiresAuth || isAuthenticated) {
    try {
      // Validate token if authenticated
      if (isAuthenticated && !(await validateToken())) {
        await logout();
      }
      // Check if required roles
      if (
        isAuthenticated &&
        !hasRequiredRoles(employeeStore.roles, to.meta.requiredRoles)
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
