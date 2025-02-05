import { ref } from 'vue';

interface Snackbar {
  isColorSnackbarVisible: boolean;
  message: string | Record<string, string[]>;
  color: 'error' | 'success' | 'info' | 'warning';
}

const snackbar = ref<Snackbar>({
  isColorSnackbarVisible: false,
  message: '',
  color: 'error',
});

export const useSnackbar = () => ({
  snackbar,
  showSnackbar(message: string, color: Snackbar['color'] = 'error') {
    snackbar.value.isColorSnackbarVisible = true;
    snackbar.value.message = message;
    snackbar.value.color = color;
  },
  closeSnackbar() {
    snackbar.value.isColorSnackbarVisible = false;
  },
});
