<!-- パスワード変更　フォーム -->
<script lang="ts" setup>
import { ref,reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import ConfimDialogView from '@/components/common/ConfimDialog.vue';
const { t } = useI18n();
const isDialogVisible = ref(false);

// エラーメッセージの型定義
interface Errors {
  password: string | null;
  confirmPassword: string | null;
}

// エラーメッセージ
const errors = ref<Errors>({
  password: null,
  confirmPassword: null,
})
// フォームデータ
const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
})

// フォームの送信処理
const handleSubmit = () => {
  // エラーをリセット
  errors.value = { password: null, confirmPassword: null}

  let valid = true

  // パスワードチェック: 必須
  if (form.password.valueOf() === '' ) {
    errors.value.password = t('pw_required_error')
    valid = false
  }

  // 再パスワードチェック: 必須
  if (form.confirmPassword.valueOf() == '') {
    errors.value.confirmPassword = t('confirm_pw_required_error')
    valid = false
  }
  // パスワードと再パスワードの一致チェック
  if (form.password.valueOf() !== form.confirmPassword.valueOf()) {
    errors.value.confirmPassword = t('pw_matching_error')
    valid = false
  }
  // バリデーション通過後、フォームデータを送信
  if (valid) {
    // 確認ポップアップを表示
      isDialogVisible.value = true;
    }
}
const onConfirmed = () => {
  console.log("許可されました");
  // ここに処理を追加
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
            <VTextField v-model="form.username" id = "username" :placeholder="t('username')"  readonly/>
          </VCol>
          <VCol cols="12" md="6">
            <VTextField v-model="form.email" id = "email" :placeholder="t('email')" readonly />
          </VCol>
          <VCol cols="12" md="6">
            <VTextField
              v-model="form.password"
              id = "password"
              :placeholder="t('password')"
              type="password" 
            />
            <span style="color: red;" v-if="errors.password" class="error">{{ errors.password }}</span>
          </VCol>
          <VCol cols="12" md="6">
            <VTextField
              v-model="form.confirmPassword"
              id ="confirmPassword"
              :placeholder="t('password_confrim')"
              type="password" 
            />
            <span style="color: red;" v-if="errors.confirmPassword" class="error">{{ errors.confirmPassword }}</span>
          </VCol>
        </VRow>
        <VDivider />
        <VCardText class="d-flex gap-4">
          <VBtn @click="handleSubmit" class="mr-4">{{ t('change_password') }}</VBtn>
          <VBtn type="reset" variant="tonal"> {{ t('reset') }} </VBtn>
        </VCardText>
        <VDialog v-model="isDialogVisible" width="auto" eager>
          <ConfimDialogView 
          :title="t('confrim')"
          :message="t('pass_change_con_msg')"
          :isVisible="isDialogVisible"
          @update:isVisible="isDialogVisible = $event"
          @confirmed="onConfirmed"
        />
        </VDialog>
      </VForm>
    </VCardText>
  </VCard>
</template>
