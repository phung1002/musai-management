<!--削除 確認　メッセージ -->
<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const emit = defineEmits(['form:cancel']);
const handleSubmit = (e) => {
  e.preventDefault();
};
const handleCancel = () => {
  emit('form:cancel');
};
</script>

<template>
  <VCard width="640px">
    <VToolbar tag="div">
      <VToolbarTitle><VIcon icon= "mdi-alert"/>{{ t('delete_con_msg') }}</VToolbarTitle>
      <VBtn icon="mdi-close" @click="$emit('form:cancel')"></VBtn>
    </VToolbar>
    <!-- 確認内容を表示 -->
    <VCardActions>
      <VBtn type="submit" variant="outlined" color="primary" @click="handleSubmit">{{ t('yes') }}</VBtn>
      <VBtn @click="handleCancel">{{ t('no') }}</VBtn>
    </VCardActions>
  </VCard>
</template>
