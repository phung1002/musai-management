<script setup lang="ts">
import ProfileDropdown from "@/components/ui/ProfileDropdown.vue";
import { useEmployeeStore } from "@/store/employeeStore";
import { computed } from "vue";

const emit = defineEmits(["toggle-menu"]);
const employeeStore = useEmployeeStore();
const fullName = computed(() => employeeStore.fullName);
</script>

<template>
  <!------Header-------->
  <VAppBar :elevation="10">
    <VBtn
      class="mobile-menu"
      color="inherit"
      variant="text"
      @click="emit('toggle-menu')"
    >
      <VIcon size="30" icon="mdi-menu" />
    </VBtn>
    <VSpacer />
    <VCardTitle>{{ fullName }}</VCardTitle>
    <ProfileDropdown />
  </VAppBar>
</template>

<style scoped>
@media (min-width: 1280px) {
  .mobile-menu {
    display: none;
  }
}
</style>
