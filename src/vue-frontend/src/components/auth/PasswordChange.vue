<!-- パスワード変更　画面 -->
<script setup lang="ts">
import AppSidebar from '@/components/layout/AppSidebar.vue';
import AppToolbar from '@/components/layout/AppToolbar.vue';
// import PasswordChangeTabForm from '@/components/auth/PasswordChangeForm.vue'
import { ref, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import ConfimDialogView from '@/components/common/ConfimDialog.vue';
const { t } = useI18n();
const isDialogVisible = ref(false);

// エラーメッセージの型定義
interface Errors {
  newPassword: string | null;
  password: string | null;
  passwordCheck: string | null;
  newPasswordConfrim: string | null;
}

// エラーメッセージ
const errors = ref<Errors>({
  newPassword: null,
  password: null,
  passwordCheck: null,
  newPasswordConfrim: null,
})
// フォームデータ
const form = reactive({
  newPassword: '',
  password: '',
  newPasswordConfrim: '',
})
// 入力初期化
const handleResetFilter = () => {
  form.newPassword = '';
  form.password = '';
  form.newPasswordConfrim = '';
}
// フォームの送信処理
const handleSubmit = () => {
  // エラーをリセット
  errors.value = { newPassword: null, password: null, passwordCheck: null, newPasswordConfrim: null }

  let valid = true

  // パスワードチェック: 必須
  if (form.password.valueOf() === '') {
    errors.value.password = t('password_required_error')
    valid = false
  }
  // 新パスワードチェック: 必須
  if (form.newPassword.valueOf() === '') {
    errors.value.newPassword = t('new_password_required_error')
    valid = false
  }
  // 再パスワードチェック: 必須
  if (form.newPasswordConfrim.valueOf() == '') {
    errors.value.newPasswordConfrim = t('confirm_password_required_error')
    valid = false
  }
  // 現在のパスワードと入力パスワードの一致チェック
  if (form.newPassword.valueOf() == form.password.valueOf()) {
    errors.value.passwordCheck = t('password_matching_error')
    valid = false
  }
  // 新パスワードと再パスワードの一致チェック
  if (form.newPassword.valueOf() !== form.newPasswordConfrim.valueOf()) {
    errors.value.newPasswordConfrim = t('new_password_matching_error')
    valid = false
  }
  // ��リデーション通過後、フォームデータを送信
  if (valid) {
    // 確認ポップアップを表示
    isDialogVisible.value = true;
  }
  // 新パスワードと再パスワードの一致チェック
  if (form.newPassword.valueOf() !== form.newPasswordConfrim.valueOf()) {
    errors.value.newPasswordConfrim = t('password_matching_error')
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
  // レスポンスOKになったら入力値初期化
  handleResetFilter();
};
</script>

<template>
  <VCard flat elevation="0">
    <VToolbar tag="div">
      <VToolbarTitle>
        <VIcon icon="mdi-lock" />{{ t('change_password') }}
      </VToolbarTitle>
    </VToolbar>
    <VCardText>
      <VForm class="mt-2" @submit.prevent="() => { }">
        <VRow>
          <VCol cols="12" md="6">
            <VTextField v-model="form.password" id="password" :placeholder="t('password')" type="password" />
            <span style="color: red;" v-if="errors.password" class="error">{{ errors.password }}</span>
          </VCol>
          <VCol cols="12" md="6">
          </VCol>
          <VCol cols="12" md="6">
            <VTextField v-model="form.newPassword" id="newPassword" :placeholder="t('new_password')" type="password" />
            <span style="color: red;" v-if="errors.newPassword" class="error">{{ errors.newPassword }}</span>
            <span style="color: red;" v-if="errors.passwordCheck" class="error">{{ errors.passwordCheck }}</span>
          </VCol>
          <VCol cols="12" md="6">
            <VTextField v-model="form.newPasswordConfrim" id="newPasswordConfrim"
              :placeholder="t('new_password_confirm')" type="password" />
            <span style="color: red;" v-if="errors.newPasswordConfrim" class="error">{{ errors.newPasswordConfrim
              }}</span>
          </VCol>
        </VRow>
        <VDivider />
      </VForm>
    </VCardText>
    <VCardActions class="d-flex justify-end">
      <VBtn type="submit" variant="elevated" color="primary" @click="handleSubmit">{{ t('change_password') }}</VBtn>
      <VBtn type="reset" variant="tonal" @click="handleResetFilter"> {{ t('reset') }} </VBtn>
    </VCardActions>
    <VDialog v-model="isDialogVisible" width="auto" eager>
      <ConfimDialogView :title="t('confirm')" :message="t('password_change_confirm_message')"
        :isVisible="isDialogVisible" @update:isVisible="isDialogVisible = $event" @confirmed="onConfirmed" />
    </VDialog>
  </VCard>
</template>

<style scoped>
.page-wrapper {
  padding: 20px;
}

.search {
  flex-grow: 1;
}

.text-error {
  color: red;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  background-color: white;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 8px;
}

.action-btn:hover {
  background-color: #f5f5f5;
}
</style>
