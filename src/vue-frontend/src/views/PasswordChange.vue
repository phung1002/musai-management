<!-- パスワード変更　画面 -->
<script setup lang="ts">
import { ref, reactive } from "vue";
import { useI18n } from "vue-i18n";
import { IPasswordChange } from "@/types/type";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { changePassword } from "@/api/employee"; // API追加
import { useValidator } from "@/utils/validation";
import { formRules } from "@/configs/EmployeeFormConfig";
import { toast } from "vue3-toastify";
import { handleLogout } from "@/api/auth";
const { t } = useI18n();
const validator = useValidator(t);
// formModel を IPasswordChange 型として一元管理
const formModel = reactive<IPasswordChange>({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});
const formRulesConfig = formRules(validator, true); // 編集モードの場合
const formValid = ref(false);
// const formRef = ref<VForm | null>(null);
// フォームデータ
const currentPassword = ref("");
const newPassword = ref("");
const confirmPassword = ref("");

// ローディング状態 & メッセージ
const loading = ref(false);
const isDialogVisible = ref(false);

// 入力初期化
const handleResetFilter = () => {
  currentPassword.value = "";
  newPassword.value = "";
  confirmPassword.value = "";
};

// 登録処理
const handleSubmit = async (formValid: boolean) => {
  if (!formValid) {
    toast.error(t("error.validation_error"));
    return;
  }
  loading.value = true;
  isDialogVisible.value = true;
};

//データ統合： formModel 内のデータを使用する
const onSubmit = async () => {
  try {
    await changePassword({
      password: formModel.currentPassword,
      newPassword: formModel.newPassword,
    });
    toast.success(t("message.add_success"));
    handleResetFilter();
    handleLogout();
  } catch (error: any) {
    toast.error(t(error.message));
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <VRow>
    <VCol cols="12">
      <VContainer class="app-container">
        <VCard flat elevation="0">
          <VToolbar tag="div">
            <VToolbarTitle>
              <VIcon>mdi-lock</VIcon>
              <span class="text-lg font-medium ml-2">{{
                t("change_password")
              }}</span>
            </VToolbarTitle>
          </VToolbar>
          <VCardText>
            <VForm ref="formRef" class="mt-2" v-model="formValid">
              <VRow>
                <VCol cols="12" md="6">
                  <VTextField
                    v-model="formModel.currentPassword"
                    :rules="[validator.required]"
                    id="current-currentPassword"
                    :placeholder="t('current_password')"
                    type="password"
                  />
                </VCol>
                <VCol cols="12" md="6"> </VCol>
                <VCol cols="12" md="6">
                  <VTextField
                    v-model="formModel.newPassword"
                    id="new-password"
                    :placeholder="t('new_password')"
                    :rules="formRulesConfig.password"
                    type="password"
                  />
                </VCol>
                <VCol cols="12" md="6">
                  <VTextField
                    v-model="formModel.confirmPassword"
                    id="confirm-password"
                    :placeholder="t('new_password_confirm')"
                    :rules="[validator.checkEqual(formModel.newPassword)]"
                    type="password"
                  />
                </VCol>
              </VRow>
            </VForm>
          </VCardText>
          <VCardActions class="d-flex justify-end">
            <VBtn
              type="submit"
              variant="elevated"
              color="primary"
              @click="handleSubmit(formValid)"
              >{{ t("change_password") }}</VBtn
            >
            <VBtn type="reset" variant="tonal" @click="handleResetFilter">
              {{ t("reset") }}
            </VBtn>
          </VCardActions>
          <VDialog v-model="isDialogVisible" width="auto" eager persistent>
            <ConfimDialogView
              :title="t('confirm')"
              :message="t('message.password_change_confirm_message')"
              :isVisible="isDialogVisible"
              @update:isVisible="isDialogVisible = $event"
              @confirmed="onSubmit"
            />
          </VDialog>
        </VCard>
      </VContainer>
    </VCol>
  </VRow>
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
