<!-- ホーム 画面　-->
<script setup lang="ts">
import { RouterView, useRoute } from "vue-router";
import AppSidebar from "@/components/layout/AppSidebar.vue";
import AppToolbar from "@/components/layout/AppToolbar.vue";
import { ref } from "vue";

const route = useRoute();
const drawer = ref(window.innerWidth >= 1280);
</script>

<template>
  <VApp class="app">
    <!------Sidebar-------->
    <AppSidebar v-model:drawer="drawer" />
    <!------Header-------->
    <AppToolbar @toggle-menu="drawer = !drawer" />
    <!------Page-------->
    <VMain class="app-main">
      <VContainer class="app-container">
        <div class="page-wrapper">
          <RouterView />
        </div>
      </VContainer>
    </VMain>
  </VApp>
</template>

<style lang="scss">
.app-main {
  background-color: var(--v-theme-background, "#f6f6f6");

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
