<script setup lang="ts">
import ProfileDropdown from '@/components/dropdown/ProfileDropdown.vue';
import { useUserStore } from '@/store/userStore';

const userStore = useUserStore();
const fullName = userStore.fullName;
</script>

<template>
  <!------Header-------->
  <VAppBar :elevation="10">
    <VSpacer />
    <VCardTitle>{{ fullName }}</VCardTitle>
    <ProfileDropdown />
  </VAppBar>
</template>
