<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { computed, ref } from "vue";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import ProfileDropdown from "@/components/auth/ProfileCard.vue";
import { handleLogout } from "@/api/auth";
import { useUserStore } from "@/store/userStore";

const userStore = useUserStore();
const isDialogVisible = ref(false);
const showProfileView = ref(false);
const { t } = useI18n();
const handleSubmit = () => {
  isDialogVisible.value = true;
};
const showProfile = () => {
  showProfileView.value = true;
};
const gender = computed(() => userStore.gender);
const onConfirmed = () => {
  handleLogout();
  sessionStorage.clear();
};
</script>

<template>
  <VMenu :close-on-content-click="false">
    <template v-slot:activator="{ props }">
      <VBtn
        class="custom-hover-primary mr-3"
        variant="text"
        v-bind="props"
        icon
      >
        <VBadge dot color="success" offset-x="0" offset-y="3">
          <!-- 性別　男性の場合写真表示設定 -->
          <VAvatar size="35" v-if="gender === 'male'">
            <img
              src="@\assets\images\users\avatar-4.png"
              height="35"
              alt="user"
            />
          </VAvatar>
          <!-- 性別　女性の場合写真表示設定 -->
          <VAvatar size="35" v-if="gender === 'female'">
            <img
              src="@\assets\images\users\avatar-2.png"
              height="35"
              alt="user"
            />
          </VAvatar>
          <!-- 性別　未登録の場合写真表示設定 -->
          <VAvatar size="35" v-if="gender === ''">
            <img src="@\assets\images\users\user.png" height="35" alt="user" />
          </VAvatar>
        </VBadge>
      </VBtn>
    </template>
    <VSheet rounded="md" width="200" elevation="10" class="mt-2">
      <div class="pt-4 pb-4 px-5 text-center">
        <VBtn color="primary" variant="outlined" block @click="showProfile"
          ><v-icon icon="mdi-account" start></v-icon>{{ t("profile") }}</VBtn
        >
      </div>
      <VDivider />
      <div class="pt-4 pb-4 px-5 text-center">
        <VBtn color="primary" block @click="handleSubmit" class="mr-4"
          ><v-icon icon="mdi-logout" start></v-icon>{{ t("logout") }}</VBtn
        >
      </div>
    </VSheet>
  </VMenu>
  <VDialog v-model="isDialogVisible" width="auto" persistent>
    <ConfimDialogView
      :title="t('confirm')"
      :message="t('logout_confirm_message')"
      :isVisible="isDialogVisible"
      @update:isVisible="isDialogVisible = $event"
      @confirmed="onConfirmed"
    />
  </VDialog>
  <VDialog v-model="showProfileView" width="auto" persistent>
    <ProfileDropdown @form:cancel="showProfileView = false" />
  </VDialog>
</template>
