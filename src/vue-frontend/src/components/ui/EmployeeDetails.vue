<!-- 社員詳細　画面 -->
<script setup lang="ts">
import { defineProps, defineEmits } from "vue";
import { useI18n } from "vue-i18n";
import { IEmployee } from "@/types/type";
import { ERole } from "@/constants/role";

const { t } = useI18n();
const props = defineProps<{ employee: IEmployee }>();
const emit = defineEmits(["form:cancel"]); // イベントを明示的に宣言

const handleCancel = () => {
  console.log("Cancel button clicked");

  emit("form:cancel");
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
</script>

<template>
  <VCard class="v-card-form">
    <VToolbar tag="div">
      <VCardTitle>{{ t("detail_information") }}</VCardTitle>
      <VSpacer />
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <VCardItem>
      <VList>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-account" class="icon-spacing" />
          </template>
          <VListItemTitle> {{ employee.fullName }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-account" class="icon-spacing" />
          </template>
          <VListItemTitle> {{ employee.fullNameFurigana }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon
              icon="mdi-card-account-details-outline"
              class="icon-spacing"
            />
          </template>
          <VListItemTitle> {{ employee.employeeId }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-account-key" class="icon-spacing" />
          </template>
          <VListItemTitle>
            <VChipGroup column active-class="bg-primary text-white">
              <VChip
                v-for="(role, index) in employee.roles"
                :style="{ color: getRoleColor(role) }"
                :key="index"
                text-color="white"
              >
                {{ t(`roles.${role}`) }}
              </VChip>
            </VChipGroup>
          </VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-email" class="icon-spacing" />
          </template>
          <VListItemTitle> {{ employee.email }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-cellphone" class="icon-spacing" />
          </template>
          <VListItemTitle> {{ employee.mobile }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon
              icon="mdi-account-credit-card-outline"
              class="icon-spacing"
            />
          </template>
          <VListItemTitle> {{ employee.department }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-briefcase" class="icon-spacing" />
          </template>
          <VListItemTitle> {{ employee.workPlace }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-human-male-female" class="icon-spacing" />
          </template>
          <VListItemTitle> {{ employee.gender }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-cake-variant-outline" class="icon-spacing" />
          </template>
          <VListItemTitle> {{ employee.birthday }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-bank-transfer-in" class="icon-spacing" />
          </template>
          <VListItemTitle> {{ employee.joinDate }}</VListItemTitle>
        </VListItem>
      </VList>
    </VCardItem>
  </VCard>
</template>

<style>
.icon-spacing {
  margin-right: 8px; /* 8px のスペースを追加 */
}
</style>
