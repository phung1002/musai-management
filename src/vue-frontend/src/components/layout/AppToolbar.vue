<script setup lang="ts">
import ProfileDropdown from "@/components/ui/ProfileDropdown.vue";
import { useEmployeeStore } from "@/store/employeeStore";
import { computed } from "vue";

const userStore = useEmployeeStore();
const fullName = computed(() => userStore.fullName);
</script>

<template>
  <!------Header-------->
  <VAppBar :elevation="10">
    <VSpacer />
    <VCardTitle>{{ fullName }}</VCardTitle>
    <ProfileDropdown />
  </VAppBar>
</template>
