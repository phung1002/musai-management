<script setup lang="ts">
import { computed } from 'vue';
import { snackbar, closeSnackbar } from '@/composables/useSnackbar';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const isMessageObject = computed(() =>
  Object.prototype.toString.call(snackbar.value.message) === '[object Object]'
);
</script>

<template>
  <div>
    <VSnackbar v-model="snackbar.isColorSnackbarVisible" top center :color="snackbar.color">
      <!-- Render message type array -->
      <template v-if="isMessageObject">
        <div v-for="(messages, key) in snackbar.message" :key="key">
          <div v-for="message in messages" :key="message">
            {{  t(`message.${message}`) }}
          </div>
        </div>
      </template>
      <!-- Render message type string -->
      <template v-else>
        {{  t(`message.${snackbar.message}`) }}
      </template>
      <!-- Close button -->
      <template v-slot:actions="{ attrs }">
        <VBtn color="white" text v-bind="attrs" @click="closeSnackbar">
          <VIcon>mdi-close</VIcon>
        </VBtn>
      </template>
    </VSnackbar>
  </div>
</template>

<style>
.theme--light.v-data-table td {
  color: rgba(0, 0, 0, 0.87) !important;
}
.v-snackbar {
  align-items: flex-start !important;
}
</style>
