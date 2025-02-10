<!-- パスワード変更　画面 -->
<script setup lang="ts">
import AppSidebar from '@/components/layout/AppSidebar.vue';
import AppToolbar from '@/components/layout/AppToolbar.vue';
import { ref, reactive, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { IUser } from '@/types/type';
import ConfimDialogView from '@/components/common/ConfimDialog.vue';
import { changePassword } from '@/api/auth';
import SnackBar from '@/components/common/SnackBar.vue';
import { useSnackbar } from '@/composables/useSnackbar';
import { userValidator } from '@/utils/validation';
import { formRules, defaultUser } from '../../configs/userFormConfig';
// タイトルを日本語に変更用
const { t } = useI18n();

const { showSnackbar } = useSnackbar();
const validator = userValidator(t);
const props = defineProps<{ user?: IUser, isEdit: boolean }>();
const formModel = reactive<IUser>(
  props.isEdit ? { ...defaultUser, ...props.user } :
    { ...defaultUser }
);
// バリデーションルール
const formRulesConfig = formRules(validator, formModel);
const formValid = ref(false);

// フォームデータ
const password = ref("");
const newPassword = ref("");
const confirmPassword = ref("");

// ローディング状態 & メッセージ
const loading = ref(false);
const isDialogVisible = ref(false);
// 入力初期化
const handleResetFilter = () => {
  password.value = "";
  newPassword.value = "";
  confirmPassword.value = "";
}

// 登録処理
const handleSubmit = async (formValid: boolean) => {
  // エラーチェック
  if (!formValid) {
    showSnackbar('validation_error', 'error');
    return;
  }
  // if (Object.keys(errors.value).length > 0) {
  //   showSnackbar('validation_error', 'error');
  //   // 登録処理を中断する
  //   return;
  // } else if (Object.keys(errors.value).length < 0)
  // return;
  loading.value = true;
  isDialogVisible.value = true;
  // message.value = "";
}

// API 呼び出し
const onSubmit = async () => {
  console.log("password:", password.value);
  console.log("newPassword:", newPassword.value);
  console.log("許可されました");
  try {
    await changePassword({
      password: password.value,
      newPassword: newPassword.value,
    });
    showSnackbar('add_success', 'success');
    // フォームのリセット
    handleResetFilter();
  } catch (error) {
    showSnackbar('add_failure', 'error');
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <VCard flat elevation="0">
    <VToolbar tag="div">
      <VToolbarTitle>
        <VIcon>mdi-lock</VIcon>
        <span class="text-lg font-medium ml-2">{{ t('change_password') }}</span>
      </VToolbarTitle>
    </VToolbar>
    <VCardText>
      <VForm class="mt-2" v-model="formValid">
        <VRow>
          <VCol cols="12" md="6">
            <VTextField v-model="password" id="current-password" :placeholder="t('password')"
              :rules="formRulesConfig.password" type="password" />
          </VCol>
          <VCol cols="12" md="6">
          </VCol>
          <VCol cols="12" md="6">
            <VTextField v-model="newPassword" id="new-password" :placeholder="t('new_password')"
              :rules="formRulesConfig.password" type="password" />
          </VCol>
          <VCol cols="12" md="6">
            <VTextField v-model="confirmPassword" id="confirm-password" :placeholder="t('new_password_confirm')"
              :rules="formRulesConfig.confirmPassword" type="password" />
          </VCol>
        </VRow>
      </VForm>
    </VCardText>
    <VCardActions class="d-flex justify-end">
      <VBtn type="submit" variant="elevated" color="primary" @click="handleSubmit(formValid)">{{
        t('change_password') }}</VBtn>
      <VBtn type="reset" variant="tonal" @click="handleResetFilter"> {{ t('reset') }} </VBtn>
    </VCardActions>
    <VDialog v-model="isDialogVisible" width="auto" eager>
      <ConfimDialogView :title="t('confirm')" :message="t('password_change_confirm_message')"
        :isVisible="isDialogVisible" @update:isVisible="isDialogVisible = $event" @confirmed="onSubmit" />
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
