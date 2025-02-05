<script setup lang="ts">
import logoImg from '@/assets/images/logo.png';
import { reactive, computed } from 'vue';
//import { useLocale } from 'vuetify';
import { useI18n } from 'vue-i18n';
import { useUserStore } from '@/store/userStore';

const userStore = useUserStore();
const filteredItems = computed(() => {
  return items.filter((item) => {
        if (!item.roles) return true;
        return item.roles.some((role) => userRoles.value.includes(role));
      });
});
const userRoles = computed(() => userStore.roles || []);
const { t } = useI18n();
const items = [
  { type: 'divider' },
  { type: 'subheader', title: t('admin'), roles:['ROLE_ADMIN']},
  { type: 'divider' },
  // {
  //   title: t('attendees_input'),
  //   props: {
  //     prependIcon: 'mdi-view-dashboard-edit',
  //     link: true,
  //     to: '/attendanceInput',
  //     exact: true
  //   },
  //   value: '/attendanceInput',
  //   roles: ['ROLE_MEMBER']
  // },
  // 管理者画面
  {
    title: t('user_management'),
    props: {
      prependIcon: 'mdi-account-box-plus-outline',
      link: true,
      to: '/admin/user',
      exact: false
    },
    value: '/admin-user-list',
    roles: ['ROLE_ADMIN']
  },
  {
    title: t('leave_management'),
    props: {
      prependIcon: 'mdi-view-dashboard-edit',
      link: true,
      to: '/leaveManagement',
      exact: false
    },
    value: '/leaveManagement',
    roles: ['ROLE_ADMIN']
  },
  // 担当者メニュー
  { type: 'divider' },
  { type: 'subheader', title: t('management'), roles:['ROLE_MANAGER']},
  { type: 'divider' },
  {
    title: t('user_requst_management'),
    props: {
      prependIcon: 'mdi-comment-edit',
      link: true,
      to: '/userLeaveManagement',
      exact: true
    },
    value: '/userLeaveManagement',
    roles: ['ROLE_MANAGER']
  },
  {
    title: t('requst_confirm'),
    props: {
      prependIcon: 'mdi-message-alert',
      link: true,
      to: '/requstConfirm',
      exact: true
    },
    value: '/requstConfirm',
    roles: ['ROLE_MANAGER']
  },
  // ユーザーメニュー
  { type: 'divider' },
  { type: 'subheader', title: t('member'), roles:['ROLE_MEMBER']},
  { type: 'divider' },
  {
    title: t('leave_apply_list'),
    props: {
      prependIcon: 'mdi-email-arrow-right',
      link: true,
      to: '/leaveApply',
      exact: true
    },
    value: '/leaveApply',
    roles: ['ROLE_MEMBER'],
  },
  { type: 'divider' },
  // 共通メニュー
  {
    title: t('change_password'),
    props: {
      prependIcon: 'mdi-account-convert',
      link: true,
      to: '/changePassword',
      exact: true
    },
    value: '/changePassword',
    // roles: ['ROLE_ADMIN']
  },
  // {
  //   title: t('logout'),
  //   props: {
  //     prependIcon: 'mdi-account-arrow-right-outline',
  //     link: true,
  //     to: '/login',
  //     exact: true
  //   },
  //   value: '/login',
  //   // value: showDialog
  //   // roles: ['ROLE_ADMIN']
  // }

];

const drawerProps = reactive({
  rail: false,
  railWidth: 256,
  icon: 'mdi-arrow-left'
});

const handleDrawerWidth = () => {
  const rail = drawerProps.rail;
  const railWidth = drawerProps.railWidth;
  drawerProps.rail = !rail;
  drawerProps.railWidth = railWidth == 64 ? 256 : 64;
  drawerProps.icon = drawerProps.railWidth === 256 ? 'mdi-arrow-expand-left  ' : 'mdi-arrow-expand-right';
};

const menus = computed(() => {
  console.log(userStore.roles);

  if (drawerProps.railWidth === 256) {
    return items;
  } else {
    return items.filter((item) => {
      return item.type !== 'subheader';
    });
  }
});
</script>

<template>
  <VNavigationDrawer :rail-width="drawerProps.railWidth" :rail="drawerProps.rail" :border="true" :elevation="1">
    <VToolbar class="px-3" color="transparent">
      <v-img :src="logoImg" alt="logo" contain class="logo py-2" :height="200"></v-img>
    </VToolbar>
    <div class="app-drawer__inner">
      <VList :items="filteredItems" color="primary" class="menu-list" nav :slim="true" />
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

<style lang="scss">
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
</style>
