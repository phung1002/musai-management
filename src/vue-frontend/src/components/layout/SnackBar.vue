<template>
  <div>
    <VSnackbar
      v-model="snackbar.isColorSnackbarVisible"
      top
      center
      :color="snackbar.color"
    >
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
        <VBtn
          color="white"
          text
          v-bind="attrs"
          @click="snackbar.isColorSnackbarVisible = false"
        >
          <VIcon>{{ icons.mdiClose }}</VIcon>
        </VBtn>
      </template>
    </VSnackbar>
  </div>
</template>

<script>
import { mdiClose } from '@mdi/js';
import { computed } from 'vue';

export default {
  props: {
    snackbar: {
      type: Object,
      required: true,
      default: () => ({
        isColorSnackbarVisible: false,
        message: null,
        color: 'error',
      }),
    },
  },
  setup(props) {
    const isMessageObject = computed(() =>
      Object.prototype.toString.call(props.snackbar.message) === '[object Object]'
    );

    return {
      icons: { mdiClose },
      isMessageObject,
    };
  },
};
</script>

<style>
.theme--light.v-data-table td {
  color: rgba(0,0,0,.87) !important;
}
.v-snackbar{
  align-items: flex-start !important;
}
</style>
