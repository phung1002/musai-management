<!-- ログイン　画面 -->
<script>
import { useUserStore } from '@/store/userStore';
import { reactive } from 'vue';
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { login } from '@/api/auth';
import logoImg from '@/assets/images/logo.png';
import { useLocale } from 'vuetify';


export default {
  name: 'LoginView',
  setup() {
    const { t } = useLocale();
    const userStore = useUserStore();
    const route = useRoute();
    const router = useRouter();
    const submiting = ref(false);
    const formValid = ref(false);
    const formModel = reactive({
      username: null,
      password: null
    });
    const handleSubmit = async () => {
      // if (formValid.value === true) {
      submiting.value = true;
      try {
        const { data } = await login(formModel);
        submiting.value = false;

        const { token, username, roles } = data;
        userStore.setToken(token);
        userStore.setUsername(username);
        userStore.setRoles(roles);
        router.replace(route.query.to ? String(route.query.to) : '/home');
      } catch (error) {
          submiting.value = false;
          console.log(error);
      }
      // }
    };
    return {
      t,
      handleSubmit,
      formValid,
      formModel,
      submiting,
      logoImg,
      };
  },
};
</script>

<template>
  <div class="auth">
    <div class="auth-wrapper d-flex flex-column align-center justify-center pt-10">
      <VCard rounded="md" elevation="10" class="login-card" max-width="500">
        <VCardItem class="pa-sm-8">
          <div class="d-flex flex-column align-center justify-center py-6">
            <v-img :src="logoImg" alt="logo" contain class="logo py-2" max-width="200"></v-img>
          </div>
          <VForm v-model="formValid" @submit.prevent>
            <VRow class="d-flex mb-3">
              <VCol cols="12">
                <VLabel class="mb-1">{{ $t('username') }}</VLabel>
                <VTextField
                  variant="outlined"
                  color="primary"
                  name="username"
                  v-model="formModel.username"
                />
              </VCol>
              <VCol cols="12">
                <VLabel class="mb-1">{{ $t('password') }}</VLabel>
                <VTextField
                  variant="outlined"
                  type="password"
                  color="primary"
                  v-model="formModel.password"
                />
              </VCol>
              <VCol cols="12" class="py-4">
                <VBtn :loading="submiting" type="submit" color="primary" block flat @click="handleSubmit">{{ $t('sign_in') }}</VBtn>
              </VCol>
            </VRow>
          </VForm>
        </VCardItem>
      </VCard>
    </div>
  </div>
</template>


