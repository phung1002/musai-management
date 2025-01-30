<!-- パスワード変更　フォーム -->
<script lang="ts" setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import PassChangeConrimView from '../massege/PassChangeConrimView.vue';
const username = ref('');
const email = ref('');
const password = ref('');
const cPassword = ref('');
const isPasswordVisible = ref(false);
const isCPasswordVisible = ref(false);
const showDialog = ref(false);
const { t } = useI18n();
const handleSubmit = () => {
  showDialog.value = true;
};
</script>

<template>
  <VDivider />
  <VCard flat elevation="0">
    <VToolbar tag="div">
       <VToolbarTitle><VIcon icon= "mdi-lock"/>{{ t('change_password') }}</VToolbarTitle>
    </VToolbar>
    <VCardText>
      <VForm class="mt-2" @submit.prevent="() => {}">
        <VRow>
          <VCol cols="12" md="6">
            <VTextField v-model="username" :placeholder="t('username')"  readonly/>
          </VCol>
          <VCol cols="12" md="6">
            <VTextField v-model="email" :placeholder="t('email')" readonly />
          </VCol>
          <VCol cols="12" md="6">
            <VTextField
              v-model="password"
              :placeholder="t('password')"
              :type="isPasswordVisible ? 'text' : 'password'"
              @click:append-inner="isPasswordVisible = !isPasswordVisible"
            />
          </VCol>
          <VCol cols="12" md="6">
            <VTextField
              v-model="cPassword"
              :placeholder="t('password_confrim')"
              :type="isCPasswordVisible ? 'text' : 'password'"
              :append-inner-icon="isCPasswordVisible ? 'tabler-eye' : 'tabler-eye-off'"
              @click:append-inner="isCPasswordVisible = !isCPasswordVisible"
            />
          </VCol>
        </VRow>
        <VDivider />
        <VCardText class="d-flex gap-4">
          <VBtn @click="handleSubmit" class="mr-4">{{ t('change_password') }}</VBtn>
          <VBtn type="reset" variant="tonal"> {{ t('cancel') }} </VBtn>
        </VCardText>
        <VDialog v-model="showDialog" width="auto" eager>
          <PassChangeConrimView @form:cancel="showDialog = false" />
        </VDialog>
      </VForm>
    </VCardText>
  </VCard>
</template>
