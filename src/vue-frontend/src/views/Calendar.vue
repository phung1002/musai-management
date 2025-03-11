<!-- カレンダー　画面 -->
<script setup lang="ts">
import { VCalendar } from "vuetify/labs/VCalendar";
import { useI18n } from "vue-i18n";
const { t } = useI18n();
</script>

<template>
  <VRow>
    <VCol cols="12">
      <VContainer class="app-container">
        <VCard flat elevation="0">
          <VToolbar tag="div">
            <VToolbarTitle
              ><VIcon icon="mdi-calendar-month-outline" />
              {{ t("calendar") }}
            </VToolbarTitle>
          </VToolbar>
          <VDivider />
          <VCalendar :model-value="new Date()" :highlight-dates="[new Date()]">
          </VCalendar>
        </VCard>
      </VContainer>
    </VCol>
  </VRow>
</template>

<style>
.v-calendar-weekly__day-label .bg-primary {
  background-color: inherit !important;
  color: #000 !important;
}
.bg-primary.v-calendar-weekly__day-label__today {
  background-color: #1867c0 !important;
  color: white !important;
  border-radius: 50% !important;
}
</style>
