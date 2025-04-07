<!-- ログイン　画面 -->
<script>
import { reactive } from "vue";
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { login } from "@/api/auth";
import logoImg from "@/assets/images/logo.png";
import { useLocale } from "vuetify";

export default {
  name: "LoginView",
  setup() {
    const { t } = useLocale();
    const showPassword = ref(false);
    const route = useRoute();
    const router = useRouter();
    const submiting = ref(false);
    const formValid = ref(false);
    const errorMessage = ref("");
    const formModel = reactive({
      employeeId: "",
      password: "",
    });
    const handleSubmit = async () => {
      submiting.value = true;
      try {
        await login(formModel);
        errorMessage.value = ""; // Delete error message if login success
        const redirectPath = route.query.to
          ? String(route.query.to)
          : "/calendar";
        router.replace(redirectPath); // direct
      } catch (error) {
        // Catch error
        const status = error.response?.status || 500;
        errorMessage.value =
          status === 400
            ? "invalid_login_input"
            : error.response?.data?.message || "login_failed";
      } finally {
        submiting.value = false;
      }
    };
    return {
      t,
      handleSubmit,
      formValid,
      formModel,
      submiting,
      logoImg,
      errorMessage,
      showPassword,
    };
  },
};
</script>

<template>
  <div class="auth">
    <div
      class="auth-wrapper d-flex flex-column align-center justify-center pt-10"
    >
      <VCard rounded="md" elevation="10" class="login-card" width="500">
        <VCardItem class="pa-sm-8">
          <div class="d-flex flex-column align-center justify-center py-6">
            <v-img
              :src="logoImg"
              alt="logo"
              contain
              class="logo py-2"
              max-width="200"
            ></v-img>
          </div>
          <VForm v-model="formValid" @submit.prevent>
            <VRow class="d-flex mb-3">
              <VCol cols="12">
                <VLabel class="mb-1">{{ $t("employee_id") }}</VLabel>
                <VTextField
                  variant="outlined"
                  color="primary"
                  name="employee_id"
                  v-model="formModel.employeeId"
                />
              </VCol>
              <VCol cols="12">
                <VLabel class="mb-1">{{ $t("password") }}</VLabel>
                <VTextField
                  variant="outlined"
                  :type="showPassword ? 'text' : 'password'"
                  color="primary"
                  v-model="formModel.password"
                  :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  @click:append-inner="showPassword = !showPassword"
                />
              </VCol>
              <VCol>
                <VLabel v-if="errorMessage" class="error-message">{{
                  $t(errorMessage)
                }}</VLabel>
              </VCol>
              <VCol cols="12" class="py-4">
                <VBtn
                  :loading="submiting"
                  type="submit"
                  color="primary"
                  block
                  flat
                  @click="handleSubmit"
                  >{{ $t("sign_in") }}</VBtn
                >
              </VCol>
            </VRow>
          </VForm>
        </VCardItem>
      </VCard>
    </div>
  </div>
</template>
