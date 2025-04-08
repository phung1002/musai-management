<!-- プロファイル　表示 -->
<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { profile } from "@/api/auth";
import { IEmployee } from "@/types/type";
const { t } = useI18n();
import { ERole } from "@/constants/role";
const emit = defineEmits(["form:cancel"]);
const handleCancel = () => {
  emit("form:cancel");
};

const convertDate = (date: string | null): string | null => {
  if (date == null) return null;
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const infor = ref<IEmployee>({} as IEmployee);
const getProfile = async () => {
  try {
    const response = await profile();
    if (!response) {
      return;
    }
    infor.value = response;
  } catch (error) {
    console.error(error);
  }
};
const getRoleColor = (roles: string) => {
  switch (roles) {
    case ERole.ADMIN:
      return "red";
    case ERole.MANAGER:
      return "orange";
    default:
      return "green";
  }
};
// Call API when component is mounted
onMounted(() => {
  getProfile();
});
</script>

<template>
  <VCard class="profile-card" min-width="600">
    <VToolbar tag="div" color="transparent">
      <VSpacer />
      <VCardActions>
        <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
      </VCardActions>
    </VToolbar>
    <!-- 性別　男性の場合写真表示設定 -->
    <VCardText class="text-center" v-if="infor.gender === 'male'">
      <img src="@\assets\images\users\avatar-4.png" size="160" />
    </VCardText>
    <!-- 性別　女性の場合写真表示設定 -->
    <VCardText class="text-center" v-if="infor.gender === 'female'">
      <img src="@\assets\images\users\avatar-2.png" size="160" />
    </VCardText>
    <!-- 性別　未登録の場合写真表示設定 -->
    <VCardText class="text-center" v-if="infor.gender === ''">
      <img src="@\assets\images\users\user.png" size="160" />
    </VCardText>
    <VCardText class="text-center">
      <div class="mt-5">
        <h3>{{ infor.fullName }}</h3>
      </div>
    </VCardText>
    <VCardItem>
      <VList>
        <!-- 社員番号 -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-card-account-details-outline"></v-icon>
          </template>
          <VListItemTitle>{{ infor.employeeId }}</VListItemTitle>
        </VListItem>
        <!-- 氏名（フリガナ） -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-account"></v-icon>
          </template>
          <VListItemTitle>{{ infor.fullNameFurigana }}</VListItemTitle>
        </VListItem>
        <!-- 権限 -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-account-key"></v-icon>
          </template>
          <VListItemTitle>
            <VChipGroup column active-class="bg-primary text-white">
              <VChip
                v-for="(role, index) in infor.roles"
                :style="{ color: getRoleColor(role) }"
                :key="index"
                text-color="white"
              >
                {{ t(`roles.${role}`) }}
              </VChip>
            </VChipGroup>
          </VListItemTitle>
        </VListItem>
        <!-- メールアドレス -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-email"></v-icon>
          </template>
          <VListItemTitle>{{ infor.email }}</VListItemTitle>
        </VListItem>
        <!-- 性別 -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-human-male-female"></v-icon>
          </template>
          <VListItemTitle>{{ t(`${infor.gender}`) }}</VListItemTitle>
        </VListItem>
        <!-- 生年月日 -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-cake-variant-outline"></v-icon>
          </template>
          <VListItemTitle>{{ convertDate(infor.birthday) }}</VListItemTitle>
        </VListItem>
        <!-- 入社日 -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-bank-transfer-in"></v-icon>
          </template>
          <VListItemTitle>{{ convertDate(infor.joinDate) }}</VListItemTitle>
        </VListItem>
        <!-- 部署 -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-account-credit-card-outline"></v-icon>
          </template>
          <VListItemTitle>{{ infor.department }}</VListItemTitle>
        </VListItem>
        <!-- 勤務先 -->
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-briefcase"></v-icon>
          </template>
          <VListItemTitle>{{ infor.workPlace }}</VListItemTitle>
        </VListItem>
      </VList>
    </VCardItem>
  </VCard>
</template>
