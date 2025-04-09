<script setup lang="ts">
import ProfileDropdown from "@/components/ui/ProfileDropdown.vue";
import { useEmployeeStore } from "@/store/employeeStore";
import { computed } from "vue";
import { shortenFileName } from "@/utils/stringUtils";

const userStore = useEmployeeStore();
const fullName = computed(() => userStore.fullName);
</script>

<template>
  <!------Header-------->
  <VAppBar :elevation="10">
    <VSpacer />
    <VCardTitle>{{ shortenFileName(fullName) }}</VCardTitle>
    <ProfileDropdown />
  </VAppBar>
</template>
