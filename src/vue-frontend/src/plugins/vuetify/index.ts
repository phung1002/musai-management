import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import theme from './theme';
import defaults from './defaults';
import 'vuetify/styles';

export default createVuetify({
  components,
  directives,
  defaults,
  theme
});
