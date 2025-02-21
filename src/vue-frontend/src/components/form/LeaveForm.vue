<script lang="ts" setup>
import { ref, Ref, defineProps, onMounted, reactive, watch } from "vue";
import { useI18n } from "vue-i18n";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { VTab } from "vuetify/lib/components/index.mjs";
import { useValidator } from "@/utils/validation";
import { ILeaveTypes } from "@/types/type";
import { getLeavesTree, addLeave, updateLeave } from "@/api/leave";
import { showSnackbar } from "@/composables/useSnackbar";
const { t } = useI18n(); //日本語にローカル変更用
const emit = defineEmits(["form-cancel", "refetch-data"]);
const errors = ref<{ leave_type?: string; leave_name?: string }>({});
const props = defineProps<{ leave?: ILeaveTypes; isEdit: boolean }>();
const leaves = ref<ILeaveTypes[]>([]); // 休暇リスト
const validator = useValidator(t); // バリデーション
const isDialogVisible = ref(false); // 確認ダイアログ表示
const isLoading = ref(false); // ローディングフラグ
const isError = ref(false); // エラーフラグ
const activeTab = ref("paid"); // タブの初期値
const formRef = ref(null);
const formValid = ref(false);
// デフォルト値
const defaultLeave = {
  id: null,
  name: "",
  parentId: null,
  children: [],
};
// 各カテゴリーの items
const parentPublicLeave = ref(null);
const paid_leave: Ref<ILeaveTypes> = ref(defaultLeave);
const public_leave: Ref<ILeaveTypes> = ref(defaultLeave);
// メイン休暇タブで分類
const tabs = ref([
  { title: "", icon: "mdi-gift-open", tab: "paid" },
  { title: "", icon: "mdi-pine-tree-box", tab: "public" },
]);
// フォームデータの初期化
const formModel = reactive<ILeaveTypes>({ ...defaultLeave });
onMounted(() => {
  if (props.isEdit && props.leave) {
    Object.assign(formModel, props.leave);
  }
  fetchLeaveType();
});

// タブ変更時に `parentId` を設定
watch(activeTab, (seletedTab) => {
  setParentId(seletedTab);
});
watch(parentPublicLeave, (item) => {
  setParentId(activeTab.value);
});

const setParentId = (selectedTab: string) => {
  if (selectedTab === "paid") {
    formModel.parentId = paid_leave.value.id;
  } else {
    if (parentPublicLeave.value == undefined) {
      formModel.parentId = public_leave.value.id;
    } else formModel.parentId = parentPublicLeave.value;
  }
};
// 入力初期化
const handleResetForm = () => {
  Object.assign(
    formModel,
    props.isEdit ? { ...defaultLeave, ...props.leave } : { ...defaultLeave }
  );
  if (formRef.value && formRef.value.resetValidation) {
    formRef.value.resetValidation();
  }
  formValid.value = false;
  parentPublicLeave.value = null;
  activeTab.value = "paid";

};
// エラーメッセージ初期化
const resetErrors = () => {
  errors.value = {
    leave_type: "",
    leave_name: "",
  };
};
const handleCancel = () => {
  handleResetForm();
  resetErrors();
  emit("form-cancel");
};

// 休暇リスト取得　API呼び出し
const fetchLeaveType = async () => {
  isLoading.value = true;
  isError.value = false;
  try {
    const response = await getLeavesTree();
    if (!response || response.length < 2) {
      throw new Error("Invalid leaves data");
    }
    leaves.value = response;

    paid_leave.value =
      leaves.value.find((item) => item.name === t("paid_leave")) ||
      defaultLeave;
    public_leave.value =
      leaves.value.find((item) => item !== paid_leave.value) || defaultLeave;

    if (!paid_leave.value || !public_leave.value) {
      throw new Error("Missing required leave types");
    }

    tabs.value[0].title = paid_leave.value.name;
    tabs.value[1].title = public_leave.value.name;

    // カテゴリーごとに分類
    // categorizeLeaves(response);
  } catch (error) {
    isError.value = true;
    console.error("Error fetching leaves:", error);
  } finally {
    isLoading.value = false;
  }
};
// コンポーネントがマウントされたときAPI呼び出し修理実行
// フォーム送信処理
const handleSubmit = async () => {
  if (!props.isEdit) {
    setParentId(activeTab.value);

    console.log("新しいデータを登録します...");
    // 登録処理を実行
    try {
      // if create user
      await addLeave(formModel);
      showSnackbar("add_success", "success");
      emit("refetch-data");
      handleCancel();
    } catch (error: any) {
      const errorMessage = ["add_failure"];
      if (error.status === 400) {
        errorMessage.push("user_exists");
      }
      showSnackbar(errorMessage, "error");
    } finally {
      isDialogVisible.value = false;
    }
  } else {
    isDialogVisible.value = true;
    console.log("データを更新しますか？...");
  }
};
// 確認ダイアログで許可されたらイベント発火
const onConfirmed = async () => {
  console.log("データを更新します...");
  try {
    if (formModel.id == null) return;
    await updateLeave(formModel.id, formModel);
    showSnackbar("update_success", "success");
    handleCancel();
    emit("refetch-data");
  } catch (error: any) {
    const errorMessage = ["update_failure"];
    if (error.status === 400) {
      errorMessage.push("user_exists");
    } else if (error.status == 403) {
      errorMessage.push("cannot_remove_own_admin_role");
    }
    showSnackbar(errorMessage, "error");
  } finally {
    isDialogVisible.value = false;
  }
  handleCancel(); // フォームを閉じる
};

</script>

<template>
  <VCard class="leave_form">
    <VToolbar tag="div">
      <!-- 新規登録際タイトルの表示 -->
      <VToolbarTitle v-if="!isEdit">
        <VIcon icon="mdi-lead-pencil" />{{ t("leave_request") }}
      </VToolbarTitle>
      <!-- 編集再タイトルの表示 -->
      <VToolbarTitle v-else>
        <VIcon icon="mdi-lead-pencil" />{{ t("update_leave") }}
      </VToolbarTitle>
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <!-- 新規登録際表示 -->
    <VForm ref="formRef" v-model="formValid" lazy-validation="false" @submit.prevent="() => {}" v-if="!isEdit">
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
                    <VCol :cols="12" class="dropdown-box">
                      <VTextField
                        v-model="formModel.name"
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
                    <VCol :cols="12" class="dropdown-box">
                      <VAutocomplete
                        v-model="parentPublicLeave"
                        :items="public_leave.children"
                        :label="t('public_leave')"
                        item-title="name"
                        item-value="id"
                        clearable
                      />
                    </VCol>
                  </VRow>
                </VCardText>
                <VCardText>
                  <VRow>
                    <VCol :cols="12">
                      <VTextField
                        v-model="formModel.name"
                        input
                        type="text"
                        :label="t('leave_name')"
                        :rules="[validator.required]"
                      />
                    </VCol>
                  </VRow>
                </VCardText>
              </VWindowItem>
            </VWindow>
          </VCardText>
        </VTable>
        <VDivider />
      </v-container>
    </VForm>
    <!-- 編集際表示 -->
    <VForm VForm ref="formRef" V-if="isEdit" @submit.prevent="() => {}" v-else>
      <v-container>
        <VRow v-if="formModel.parentId !== null">
          <VCol :cols="12" class="dropdown-box">
            <VTextField
              v-model="formModel.parentId"
              input
              type="text"
              :label="t('parent_id')"
              :rules="[validator.required]"
            />
          </VCol>
        </VRow>
        <VRow>
          <VCol :cols="12">
            <VTextField
              v-model="formModel.name"
              input
              type="text"
              :label="t('leave_name')"
              :rules="[validator.required]"
            />
          </VCol>
        </VRow>
      </v-container>
    </VForm>
    <VCardActions>
      <VBtn
        @click="handleSubmit"
        type="submit"
        variant="elevated"
        color="primary"
        >{{ isEdit ? t("update") : t("submit") }}
      </VBtn>
      <VBtn @click="handleResetForm" type="reset" variant="tonal">{{
        t("reset")
      }}</VBtn>
    </VCardActions>
    <!-- 確認ダイアログ表示 -->
    <VDialog v-model="isDialogVisible" width="auto" eager>
      <ConfimDialogView
        :title="t('confirm')"
        :message="
          isEdit
            ? t('leave_update_confirm_message')
            : t('leave_apply_confirm_message')
        "
        :isVisible="isDialogVisible"
        @update:isVisible="isDialogVisible = $event"
        @confirmed="onConfirmed"
      />
    </VDialog>
  </VCard>
</template>
