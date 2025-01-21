<script lang="ts" setup>
import { ref } from 'vue';

import PasswordChangeForm from '@/components/forms/PasswordChangeForm.vue';

const activeTab = ref('personal-info');
const tabs = [{ title: 'パスワード変更', icon: 'mdi-lock', tab: 'security' }];
</script>

<template>
  <VTabs v-model="activeTab" color="primary">
    <VTab v-for="item in tabs" :key="item.icon" :value="item.tab">
      <VIcon size="20" start :icon="item.icon" />
      {{ item.title }}
    </VTab>
  </VTabs>
  <VDivider />
  <VCard flat elevation="0">
    <VCardText>
      <VWindow v-model="activeTab">
        <VWindowItem value="security">
          <PasswordChangeForm />
        </VWindowItem>
      </VWindow>
    </VCardText>
    <VDivider />
    <VCardText class="d-flex gap-4">
      <VBtn class="mr-4">変更</VBtn>
      <VBtn variant="tonal"> キャンセル </VBtn>
    </VCardText>
  </VCard>
</template>
