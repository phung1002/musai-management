<script setup lang="ts">
import logoImg from "@/assets/images/logo.png";
import { reactive, computed } from "vue";
import { useI18n } from "vue-i18n";
import { useEmployeeStore } from "@/store/employeeStore";
import { ERole } from "@/constants/role";

const employeeStore = useEmployeeStore();
const filteredItems = computed(() => {
  return items.filter((item) => {
    if (!item.roles) return true;
    return item.roles.some((role) => employeeRoles.value.includes(role));
  });
});
const employeeRoles = computed(() => employeeStore.roles || []);
const { t } = useI18n();
const items = [
  { type: "divider", roles: [ERole.ADMIN] },
  { type: "subheader", title: t("roles.ADMIN"), roles: [ERole.ADMIN] },
  {
    title: t("employee_management"),
    props: {
      prependIcon: "mdi-account-box-multiple-outline",
      link: true,
      to: "/admin/employees",
      exact: false,
    },
    value: "/admin/employees",
    roles: [ERole.ADMIN],
  },
  {
    title: t("leave_management"),
    props: {
      prependIcon: "mdi-calendar-star-outline",
      link: true,
      to: "/admin/leave-management",
      exact: false,
    },
    value: "/admin/leave-management",
    roles: [ERole.ADMIN],
  },
  { type: "divider", roles: [ERole.MANAGER] },
  { type: "subheader", title: t("roles.MANAGER"), roles: [ERole.MANAGER] },
  {
    title: t("employee_leave_management"),
    props: {
      prependIcon: "mdi-badge-account-horizontal-outline",
      link: true,
      to: "/manager/employee-leave-management",
      exact: true,
    },
    value: "/manager/employee-leave-management",
    roles: [ERole.MANAGER],
  },
  {
    title: t("request_confirm"),
    props: {
      prependIcon: "mdi-message-check-outline",
      link: true,
      to: "/manager/request-confirm",
      exact: true,
    },
    value: "/manager/request-confirm",
    roles: [ERole.MANAGER],
  },
  { type: "divider", roles: [ERole.MEMBER] },
  { type: "subheader", title: t("roles.MEMBER"), roles: [ERole.MEMBER] },
  {
    title: t("leave_request"),
    props: {
      prependIcon: "mdi-email-arrow-right-outline",
      link: true,
      to: "/member/leave-applications",
      exact: true,
    },
    value: "/member/leave-applications",
    roles: [ERole.MEMBER],
  },
  { type: "divider" },
  {
    title: t("submit_document"),
    props: {
      prependIcon: "mdi-invoice-text-multiple-outline",
      link: true,
      to: "/document",
      exact: true,
    },
    value: "/document",
    roles: [ERole.MEMBER, ERole.MANAGER, ERole.ADMIN],
  },
  {
    title: t("calendar"),
    props: {
      prependIcon: "mdi-calendar-month-outline",
      link: true,
      to: "/calendar",
      exact: true,
    },
    value: "/calendar",
    roles: [ERole.MEMBER, ERole.MANAGER, ERole.ADMIN],
  },
  {
    title: t("change_password"),
    props: {
      prependIcon: "mdi-account-convert",
      link: true,
      to: "/change-password",
      exact: true,
    },
    value: "/change-password",
    roles: [ERole.MEMBER, ERole.MANAGER, ERole.ADMIN],
  },
];

const drawerProps = reactive({
  rail: false,
  railWidth: 256,
  icon: "mdi-arrow-left",
});

const handleDrawerWidth = () => {
  const rail = drawerProps.rail;
  const railWidth = drawerProps.railWidth;
  drawerProps.rail = !rail;
  drawerProps.railWidth = railWidth == 64 ? 256 : 64;
  drawerProps.icon =
    drawerProps.railWidth === 256
      ? "mdi-arrow-expand-left  "
      : "mdi-arrow-expand-right";
};
</script>

<template>
  <VNavigationDrawer
    :rail-width="drawerProps.railWidth"
    :rail="drawerProps.rail"
    :border="true"
    :elevation="1"
  >
  <div>
    <VToolbar color="transparent" class="mr-2">
      <VImg class="logo-img" :src="logoImg" alt="logo" contain></VImg>
    </VToolbar>
  </div>
    <div class="app-drawer__inner">
      <VList
        :items="filteredItems"
        color="primary"
        class="menu-list pt-0"
        nav
        :slim="true"
      />
    </div>
    <VBtn
      class="btn-collapse"
      rounded="lg"
      color="white"
      size="x-small"
      :icon="drawerProps.icon"
      @click="handleDrawerWidth"
      :style="{ left: drawerProps.railWidth - 12 + 'px' }"
    />
  </VNavigationDrawer>
</template>

<style lang="scss" scoped>
.logo-img {
  margin: 4px 2px !important;
  // width: 60px !important;
}
.btn-collapse {
  position: absolute;
  inset-block-start: 50%;
  transform: translateY(-50%);
}

.menu-list {
  .v-list-subheader__text {
    text-transform: uppercase;
  }

  .v-list-item__prepend > .v-icon {
    margin-inline-end: 16px;
  }

  .v-list-item-title {
    text-transform: capitalize;
  }
}

.v-navigation-drawer--border {
  border-width: inherit;
  box-shadow: none;
}
</style>
