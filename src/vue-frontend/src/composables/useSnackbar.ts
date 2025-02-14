import { ref } from 'vue';

const snackbar = ref({
  isColorSnackbarVisible: false,
  message: '' as string | Record<string, string[]>,
  color: 'error'
});

const showSnackbar = (messages: (string | string[]) | string, color: string = 'error') => {
  snackbar.value.message = Array.isArray(messages) ? { default: messages } : messages;
  snackbar.value.color = color;
  snackbar.value.isColorSnackbarVisible = true;
};

const closeSnackbar = () => {
  snackbar.value.isColorSnackbarVisible = false;
};

export { snackbar, showSnackbar, closeSnackbar };
