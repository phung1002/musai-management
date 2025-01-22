<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { reactive, computed } from 'vue';
import { useUserStore } from '@/store/userStore';

const userStore = useUserStore();
const userRoles = computed(() => userStore.roles || []);
const { t } = useI18n();
const items = [
  {
    title: t('profile'),
    props: {
      prependIcon: 'mdi-account',
      link: true,
      to: '/profile',
      exact: true
    },
    value: '/profile',
  },
];
const router = useRouter();

const handleLogout = () => {
  router.push({
    path: '/login'
  });
  sessionStorage.clear();
};
</script>

<template>
  <VMenu :close-on-content-click="false">
    <template v-slot:activator="{ props }">
      <VCardTitle>user</VCardTitle>
      <VBtn class="custom-hover-primary mr-3" variant="text" v-bind="props" icon>
        <VBadge dot color="success" offset-x="0" offset-y="3">
          
          <VAvatar size="35">
          
            <img src="@\assets\images\users\avatar-4.png" height="35" alt="user" title="{{ username }}"/>
          </VAvatar>
        </VBadge>
      </VBtn>
    </template>
    <VSheet rounded="md" width="200" elevation="10" class="mt-2">
      <VList :items="items" lines="one" density="compact" class="pa-0" color="primary" />
      <div class="pt-4 pb-4 px-5 text-center">
        <VBtn color="primary" variant="outlined" block @click="handleLogout">ログアウト</VBtn>
      </div>
    </VSheet>
  </VMenu>
</template>
