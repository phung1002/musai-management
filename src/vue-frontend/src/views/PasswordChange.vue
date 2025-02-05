<!-- パスワード変更　画面 -->
<script setup lang="ts">
import AppSidebar from '@/components/layout/AppSidebar.vue';
import AppToolbar from '@/components/layout/AppToolbar.vue';
import PasswordChangeTabForm from '@/components/auth/PasswordChangeForm.vue'
</script>

<template>
  <VApp class="app">
    <!------Sidebar-------->
    <AppSidebar />
    <!------Header-------->
    <AppToolbar />
    <!------Page-------->
    <VMain class="app-main">
      <VContainer class="app-container">
        <div class="page-wrapper">
          <PasswordChangeTabForm />
          <RouterView />
        </div>
      </VContainer>
    </VMain>
  </VApp>
</template>

<style lang="scss">
.app-main {
  background-color: var(--v-theme-background, '#f6f6f6');
  .app-container {
    max-width: 1440px;
    .page-title {
      color: #3f434a;
      font-size: 28px;
      font-weight: 700;
      padding: 30px 0;
    }
  }
}
</style>
