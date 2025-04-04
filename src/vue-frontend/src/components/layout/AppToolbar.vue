<script setup lang="ts">
import ProfileDropdown from '@/components/ui/ProfileDropdown.vue';
import { useUserStore } from '@/store/userStore';
import { computed } from 'vue';

const userStore = useUserStore();
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
