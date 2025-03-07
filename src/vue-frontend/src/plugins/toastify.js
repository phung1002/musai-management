import  VueToastify  from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

export default {
  install: (app) => {
    app.use(VueToastify, {
      position: 'top-center', // Default position for all toasts
      autoClose: 5000,       // Auto-close after 5 seconds
      hideProgressBar: false, // Show progress bar by default
      closeOnClick: true,     // Allow closing toast on click
      pauseOnHover: true,     // Pause toast on hover
      draggable: true,        // Allow dragging of toast
    });
  },
};
