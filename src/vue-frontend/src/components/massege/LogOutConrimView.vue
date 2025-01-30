<!-- ログアウト　確認　メッセージ -->
<script setup lang="ts">
import { logout } from '@/api/auth';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { ref } from 'vue';
const submiting = ref(false);
const { t } = useI18n();
const emit = defineEmits(['form:cancel']);
const route = useRoute();
const router = useRouter();
// テスト用
const handleLogout = () => {
  router.push({
    path: '/login'
  });
  sessionStorage.clear();
};
const handleCancel = () => {
  emit('form:cancel');
};
// ログアウト確認修理
// const handleLogout = async () => {
//       try {
//         const { data } = await logout();
//         router.replace(route.query.to ? String(route.query.to) : '/login');
//       } catch (error) {
//           submiting.value = false;
//           console.log(error);
//       }
//       // }
// };
</script>

<template>
  <VCard width="640px">
    <VToolbar tag="div">
      <VToolbarTitle><VIcon icon= "mdi-alert"/>{{ t('log_out_con_msg') }}</VToolbarTitle>
      <VBtn icon="mdi-close" @click="$emit('form:cancel')"></VBtn>
    </VToolbar>
    <!-- 確認内容を表示 -->
    <VCardActions>
      <VBtn type="submit" variant="outlined" color="primary" @click="handleLogout">{{ t('yes') }}</VBtn>
      <VBtn @click="handleCancel">{{ t('no') }}</VBtn>
    </VCardActions>
  </VCard>
</template>
