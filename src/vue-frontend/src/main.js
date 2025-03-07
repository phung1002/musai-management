import './assets/main.css'
import '@mdi/font/css/materialdesignicons.css';

import { createApp } from 'vue';
import App from './App.vue';
import vuetify from './plugins/vuetify';
import pinia from './plugins/pinia';
import router from './router';
import i18n from '@/plugins/i18n';
import ToastifyPlugin from './plugins/toastify';
// import { setupCalendar } from 'v-calendar';

const app = createApp(App);

app.use(vuetify);
app.use(pinia);
app.use(router);
app.use(i18n);
app.use(ToastifyPlugin);
// app.use(setupCalendar, {})
app.mount('#app');
