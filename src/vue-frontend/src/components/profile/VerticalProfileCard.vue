<!-- プロファイル　表示 -->
<script setup lang="ts">

import { reactive, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useUserStore } from '@/store/userStore';
const { t } = useI18n();
const userStore = useUserStore();
const userRoles = computed(() => userStore.roles || []);
const emit = defineEmits(['form:cancel']);
const handleCancel = () => {
  emit('form:cancel');
};
</script>

<template>
  <VCard class="profile-card">
    <VToolbar tag="div" color="transparent">
      <VSpacer />
      <VCardActions> 
        <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
      </VCardActions>
    </VToolbar>
    <!-- <VDivider></VDivider> -->
    <VCardText class="text-center">
      <img src="@\assets\images\users\avatar-4.png"  size="160" />
      <div class="mt-5">
        <h3>user name</h3>
        <!-- <p>{{ user.role }}</p> -->
      </div>
    </VCardText>
    <VCardItem>
      <VList>
        <!-- <v-list-subheader>{{ t('profile') }}</v-list-subheader> -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-card-account-details-outline"></v-icon>
          </template>
          <VListItemTitle>id</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-email"></v-icon>
          </template>
          <VListItemTitle>email</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-account-credit-card-outline"></v-icon>
          </template>
          <VListItemTitle>department</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-human-male-female"></v-icon>
          </template>
          <VListItemTitle>sex</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-bank-transfer-in"></v-icon>
          </template>
          <VListItemTitle>in date</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-cake-variant-outline"></v-icon>
          </template>
          <VListItemTitle>birth day</VListItemTitle>
        </VListItem>
      </VList>
    </VCardItem>
  </VCard>
</template>
