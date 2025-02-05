<template>
  <div>
    <VSnackbar v-model="snackbar.isColorSnackbarVisible" top center :color="snackbar.color">
      <!-- Render message type array -->
      <template v-if="isMessageObject">
        <div v-for="(messages, key) in snackbar.message" :key="key">
          <div v-for="message in messages" :key="message">
            {{ message }}
          </div>
        </div>
      </template>
      <!-- Render message type string -->
      <template v-else>
        {{ snackbar.message }}
      </template>

      <!-- Close button -->
      <template v-slot:action="{ attrs }">
        <VBtn color="white" text v-bind="attrs" @click="closeSnackbar">
          <VIcon>{{ icons.mdiClose }}</VIcon>
        </VBtn>
      </template>
    </VSnackbar>
  </div>
</template>

<script>
import { mdiClose } from '@mdi/js';
import { computed } from 'vue';
import { useSnackbar } from '@/composables/useSnackbar';

export default {
  setup() {
    const { snackbar, closeSnackbar } = useSnackbar();
    const isMessageObject = computed(() =>

      Object.prototype.toString.call(snackbar.value.message) === '[object Object]'
    );

    return {
      snackbar,
      closeSnackbar,
      icons: { mdiClose },
      isMessageObject,
    };
  },
};
</script>

<style>
.theme--light.v-data-table td {
  color: rgba(0, 0, 0, 0.87) !important;
}
.v-snackbar {
  align-items: flex-start !important;
}
</style>
