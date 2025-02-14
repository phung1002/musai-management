<script lang="ts" setup>
import { ref, reactive } from "vue";
import { useI18n } from "vue-i18n";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { VTab } from "vuetify/lib/components/index.mjs";
import { useLeaveTypesStore } from "@/store/leaveTypesStore";
import { userValidator } from "@/utils/validation";
import { useSnackbar } from "@/composables/useSnackbar";
const { t } = useI18n();
const leaveUserStore = useLeaveTypesStore();
const errors = ref<{ leave_type?: string; leave_name?: string }>({});
const isDialogVisible = ref(false);
const emit = defineEmits(["form:cancel"]);
const validator = userValidator(t);
const { showSnackbar } = useSnackbar();
const formValid = ref(false);
const filters = ref({
  paid_leave: "",
  public_leave: "",
  special_occasions_leave: "",
  special_day_leave: "",
  leave_name: "",
});
// 入力初期化
const handleResetFilter = () => {
  filters.value = {
    paid_leave: "",
    public_leave: "",
    special_occasions_leave: "",
    special_day_leave: "",
    leave_name: "",
  };
};
// エラーメッセージ初期化
const resetErrors = () => {
  errors.value = {
    leave_type: "",
    leave_name: "",
  };
};
const handleCancel = () => {
  handleResetFilter(); // 入力をリセット
  resetErrors(); // エラー��ッセージ初期化
  emit("form:cancel");
};
const handleSubmit = async () => {
  // エラーチェック

  // if (Object.keys(errors.value).length > 0) {
  //   showSnackbar('validation_error', 'error');
  //   // 登録処理を中断する
  //   return;
  // } else if (Object.keys(errors.value).length < 0)
  // return;
  isDialogVisible.value = true;
  // message.value = "";
};
const onConfirmed = () => {
  console.log("許可されました");
  // ここに処理を追加
  // レスポンスOKになったら入力値初期化し、フォーム閉じろ
  handleCancel(); //フォーム閉じる
};
// メイン休暇タブで分類
const activeTab = ref("personal-info");
const tabs = [
  { title: t("paid_leave"), icon: "mdi-gift-open", tab: "paid" },
  { title: t("public_leave"), icon: "mdi-pine-tree-box", tab: "public" },
];
// 休暇区分情報取得
const paid_leave = leaveUserStore.getPaidLeave;
const public_leave = leaveUserStore.getPublicLeave;
const special_day_leave = leaveUserStore.getSpecialDayLeave;
const special_occasions_leave = leaveUserStore.getSpecialOccasionsLeave;
</script>

<template>
  <VCard class="leave_form">
    <VToolbar tag="div">
      <VToolbarTitle>
        <VIcon icon="mdi-lead-pencil" />{{ t("leave_request") }}
      </VToolbarTitle>
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <VForm VForm ref="formRef" @submit.prevent="() => {}">
      <v-container>
        <VTable>
          <VTabs v-model="activeTab" color="primary">
            <VTab v-for="item in tabs" :key="item.icon" :value="item.tab">
              <VIcon size="20" start :icon="item.icon" />
              {{ item.title }}
            </VTab>
          </VTabs>
          <VCardText>
            <VWindow v-model="activeTab">
              <span v-if="errors.leave_type" class="error" style="color: red">{{
                errors.leave_type
              }}</span>
              <VWindowItem value="paid">
                <VCardText>
                  <VRow>
                    <VCol :cols="4" class="dropdown-box">
                      <VTextField
                        v-model="filters.leave_name"
                        input
                        type="text"
                        :label="t('leave_name')"
                        :rules="[validator.required]"
                      />
                    </VCol>
                  </VRow>
                </VCardText>
              </VWindowItem>
              <VWindowItem value="public">
                <VCardText>
                  <VRow>
                    <VCol :cols="4" class="dropdown-box">
                      <VAutocomplete
                        v-model="filters.public_leave"
                        :items="public_leave"
                        :label="t('public_leave')"
                      />
                    </VCol>
                  </VRow>
                </VCardText>
                <VCardText>
                  <VRow>
                    <VCol :cols="4" class="dropdown-box">
                      <VTextField
                        v-model="filters.leave_name"
                        input
                        type="text"
                        :label="t('leave_name')"
                        :rules="[validator.required]"
                      />
                    </VCol>
                  </VRow>
                </VCardText>
              </VWindowItem>
              <VDivider />
            </VWindow>
          </VCardText>
        </VTable>
        <VDivider />
      </v-container>
    </VForm>
    <VCardActions>
      <VBtn
        @click="handleSubmit"
        type="submit"
        variant="elevated"
        color="primary"
        >{{ t("submit") }}</VBtn
      >
      <VBtn @click="handleResetFilter" type="reset" variant="tonal">{{
        t("reset")
      }}</VBtn>
    </VCardActions>
    <!-- 確認ダイアログ表示 -->
    <VDialog v-model="isDialogVisible" width="auto" eager>
      <ConfimDialogView
        :title="t('confirm')"
        :message="t('leave_apply_confirm_message')"
        :isVisible="isDialogVisible"
        @update:isVisible="isDialogVisible = $event"
        @confirmed="onConfirmed"
      />
    </VDialog>
  </VCard>
</template>
