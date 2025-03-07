<!-- プロファイル　表示 -->
<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { profile } from "@/api/auth";
import { IUser } from "@/types/type";
const { t } = useI18n();

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

const infor = ref<IUser>({} as IUser);
const getProfile = async () => {
  try {
    const response = await profile();
    if (!response || !response.data) {
      console.log("No profile data received");
      return;
    }
    infor.value = response.data;
  } catch (error) {
    console.log(error);
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
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-card-account-details-outline"></v-icon>
          </template>
          <VListItemTitle>{{ infor.username }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-email"></v-icon>
          </template>
          <VListItemTitle>{{ infor.email }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-account-credit-card-outline"></v-icon>
          </template>
          <VListItemTitle>{{ infor.department }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-human-male-female"></v-icon>
          </template>
          <VListItemTitle>{{ t(`${infor.gender}`) }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-bank-transfer-in"></v-icon>
          </template>
          <VListItemTitle>{{ convertDate(infor.joinDate) }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <v-icon icon="mdi-cake-variant-outline"></v-icon>
          </template>
          <VListItemTitle>{{ convertDate(infor.birthday) }}</VListItemTitle>
        </VListItem>
      </VList>
    </VCardItem>
  </VCard>
</template>
