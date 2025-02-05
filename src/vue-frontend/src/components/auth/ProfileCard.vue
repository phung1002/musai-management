<!-- プロファイル　表示 -->
<script setup lang="ts">

import { reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { useUserStore } from '@/store/userStore';
// import { getProfile } from '@/api/user';
const userStore = useUserStore();
const fullName = userStore.fullName;
const { t } = useI18n();
const emit = defineEmits(['form:cancel']);
const handleCancel = () => {
  emit('form:cancel');
};

const filters = reactive({
  userid: '',
  username: '',
  email: '',
  department: '',
  gender: '',
  join_date: '',
  birth_day: '',
});
</script>

<template>
  <VCard class="profile-card">
    <VToolbar tag="div" color="transparent">
      <VSpacer />
      <VCardActions> 
        <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
      </VCardActions>
    </VToolbar>
    <!-- 性別　男性の場合写真表示設定 -->
    <VCardText class="text-center" v-if="filters.gender.valueOf() === 'male'">
      <img src="@\assets\images\users\avatar-4.png"  size="160" />
    </VCardText>
    <!-- 性別　女性の場合写真表示設定 -->
    <VCardText class="text-center" v-if="filters.gender.valueOf() === 'female'">
      <img src="@\assets\images\users\avatar-2.png"  size="160" />
    </VCardText>
    <!-- 性別　未登録の場合写真表示設定 -->
    <VCardText class="text-center" v-if="filters.gender.valueOf() === ''">
      <img src="@\assets\images\users\user.png"  size="160" />
    </VCardText>
    <VCardText class="text-center">
      <div class="mt-5">
        <h3>{{ fullName }}</h3>
      </div>
    </VCardText>
    <VCardItem>
      <VList>
        <!-- <v-list-subheader>{{ t('profile') }}</v-list-subheader> -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-card-account-details-outline"></v-icon>
          </template>
          <VListItemTitle></VListItemTitle>
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
          <VListItemTitle>gender</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-bank-transfer-in"></v-icon>
          </template>
          <VListItemTitle>join_date</VListItemTitle>
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
