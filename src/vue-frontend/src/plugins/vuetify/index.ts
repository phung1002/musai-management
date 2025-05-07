import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";
import theme from "./theme";
import defaults from "./defaults";
import "vuetify/styles";
import "@mdi/font/css/materialdesignicons.css";

import { ja } from "vuetify/locale";
import jaMessages from "@/plugins/i18n/locales/ja.json";
export default createVuetify({
  components,
  directives,
  defaults,
  theme,
  locale: {
    locale: "ja",
    fallback: "en",
    messages: {
      ja: {
        ...ja,
        ...jaMessages,
      },
    },
  },
});
