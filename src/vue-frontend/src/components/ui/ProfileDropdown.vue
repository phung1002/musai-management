<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import {reactive,ref } from 'vue';
import ConfimDialogView from '@/components/common/ConfimDialog.vue';
import ProfileDropdown from '@/components/auth/ProfileCard.vue';
const isDialogVisible = ref(false);
const showProfileView = ref(false);
const { t } = useI18n();
const router = useRouter();
const handleSubmit = () => {
  isDialogVisible.value = true;
};
const showProfile = () => {
  showProfileView.value = true;
};
const filters = reactive({
  userid: '',
  username: '',
  gender: '',
});
const onConfirmed = () => {
  console.log("許可されました");
    // ここから処理
  router.push({
    path: '/login'
  });
  sessionStorage.clear();
};
</script>

<template>
  <VMenu :close-on-content-click="false">
    <template v-slot:activator="{ props }">
      <VBtn class="custom-hover-primary mr-3" variant="text" v-bind="props" icon>
        <VBadge dot color="success" offset-x="0" offset-y="3">
          <!-- 性別　男性の場合写真表示設定 -->
          <VAvatar size="35"  v-if="filters.gender.valueOf() === 'male'">
            <img src="@\assets\images\users\avatar-4.png" height="35" alt="user"/>
          </VAvatar>
          <!-- 性別　女性の場合写真表示設定 -->
          <VAvatar size="35"  v-if="filters.gender.valueOf() === 'female'">
            <img src="@\assets\images\users\avatar-4.png" height="35" alt="user"/>
          </VAvatar>
          <!-- 性別　未登録の場合写真表示設定 -->
          <VAvatar size="35"  v-if="filters.gender.valueOf() === ''">
            <img src="@\assets\images\users\user.png" height="35" alt="user"/>
          </VAvatar>
        </VBadge>
      </VBtn>
    </template>
    <VSheet rounded="md" width="200" elevation="10" class="mt-2">
      <div class="pt-4 pb-4 px-5 text-center">
        <VBtn color="primary" variant="outlined" block @click="showProfile" ><v-icon icon="mdi-account" start></v-icon>{{ t('profile') }}</VBtn>
        <VDialog v-model="showProfileView" width="auto" eager>
          <ProfileDropdown  @form:cancel="showProfileView = false" />
        </VDialog>
      </div>
      <VDivider />
      <div class="pt-4 pb-4 px-5 text-center">
      <VBtn color="primary" block @click="handleSubmit" class="mr-4"><v-icon icon="mdi-logout" start></v-icon>{{ t('logout') }}</VBtn>
      <VDialog v-model="isDialogVisible" width="auto" eager>
        <ConfimDialogView 
          :title="t('confrim')"
          :message="t('log_out_con_msg')"
          :isVisible="isDialogVisible"
          @update:isVisible="isDialogVisible = $event"
          @confirmed="onConfirmed" 
        />
      </VDialog>
    </div>
    </VSheet>
  </VMenu>
</template>