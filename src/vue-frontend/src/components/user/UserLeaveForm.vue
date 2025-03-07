<script lang="ts" setup>
import { ref, Ref, defineProps, onMounted, reactive, watch } from "vue";
import { useI18n } from "vue-i18n";
import { VSelect, VTab } from "vuetify/lib/components/index.mjs";
import { useValidator } from "@/utils/validation";
import { IUserLeaves, ILeaveTypes } from "@/types/type";
import { getLeavesTree, addUserLeave, updateUserLeave } from "@/api/leave";
import { showSnackbar } from "@/composables/useSnackbar";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import UserList from "@/components/user/UserList.vue";
const { t } = useI18n(); //日本語にローカル変更用
const emit = defineEmits(["form-cancel", "refetch-data"]);
const errors = ref<{ leave_type?: string; leave_name?: string }>({});
const props = defineProps<{ userLeave?: IUserLeaves; isEdit: boolean }>(); // 編集対象情報
const leaves = ref<ILeaveTypes[]>([]); // 休暇リスト
const validator = useValidator(t); // バリデーション
const isDialogVisible = ref(false); // 確認ダイアログ表示
const userListVisible = ref(false); // ユーザー一覧ポップアップの表示状態
const isLoading = ref(false); // ローディングフラグ
const isError = ref(false); // エラーフラグ
const activeTab = ref("paid"); // タブの初期値
const formRef = ref(null);
const formValid = ref(false);
// メイン休暇タブで分類
const tabs = ref([
  { title: "", icon: "mdi-gift-open", tab: "paid" },
  { title: "", icon: "mdi-pine-tree-box", tab: "public" },
]);
// デフォルト値
const defaultLeave = {
  id: null,
  leaveTypeId: null,
  leaveTypeName: "",
  userName: "",
  userId: null,
  remainedDay: null,
  totalDays: null,
  usedDays: null,
  validFrom: "",
  validTo: "",
  name: "",
  parentId: null,
};
// 10～30の配列を生成
const numberOptions = Array.from({ length: 21 }, (_, i) => i + 5);

// 各カテゴリーの items
const paid_leave: Ref<ILeaveTypes> = ref(defaultLeave);
const public_leave: Ref<ILeaveTypes> = ref(defaultLeave);

// フォームデータの初期化
const formModel = reactive<IUserLeaves>(
  props.isEdit ? { ...defaultLeave, ...props.userLeave } : { ...defaultLeave }
);
// コンポーネントがマウントされたときAPI呼び出し修理実行
onMounted(() => {
  fetchLeaveType();
});
// タブ変更時に `parentId` を設定
watch(activeTab, (seletedTab) => {
  setParentId(seletedTab);
});

// 親IDを設定
const setParentId = (selectedTab: string) => {
  if (selectedTab === "paid") {
    formModel.leaveTypeId = paid_leave.value.id;
    formModel.leaveTypeName = paid_leave.value.name;
    console.log("setParentId", formModel.leaveTypeId, paid_leave.value.id);
    console.log(
      "leaveTypeName",
      formModel.leaveTypeName,
      paid_leave.value.name
    );
  } else {
    formModel.leaveTypeId = public_leave.value.id;
    formModel.leaveTypeName = public_leave.value.name;
    console.log("setParentId", formModel.leaveTypeId, public_leave.value.id);
    console.log(
      "leaveTypeName",
      formModel.leaveTypeName,
      public_leave.value.name
    );
  }
};
// フォーカス時にユーザー一覧ポップアップを表示
const showUserList = () => {
  userListVisible.value = true;
};
// 子コンポーネントから受け取る処理
const handleUserSelect = (user: { id: number; name: string }) => {
  formModel.userId = user.id;
  formModel.userName = user.name;
};
// 入力初期化
const handleResetForm = () => {
  Object.assign(
    formModel,
    props.isEdit ? { ...defaultLeave, ...props.userLeave } : { ...defaultLeave }
  );
  // if (formRef.value && formRef.value.resetValidation) {
  //   formRef.value.resetValidation();
  if (formRef.value && formRef.value) {
    formRef.value;
  }
  // 追加際activeTab現在ままにする
  if (!props.isEdit) {
    console.log("isEdit", props.isEdit, leaves);
    activeTab.value = "paid";
    formValid.value = false;
  }
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
  } catch (error) {
    isError.value = true;
    console.error("Error fetching leaves:", error);
  } finally {
    isLoading.value = false;
  }
};
// フォーム送信処理
const handleSubmit = async () => {
  if (!props.isEdit) {
    setParentId(activeTab.value);
    console.log("新しいデータを登録します...");
    // 登録処理を実行
    try {
      console.log(formModel);
      await addUserLeave(formModel);
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
    await updateUserLeave(formModel.id, formModel);
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
        <VIcon icon="mdi-lead-pencil" />{{ t("employee_leave_register") }}
      </VToolbarTitle>
      <!-- 編集再タイトルの表示 -->
      <VToolbarTitle v-else>
        <VIcon icon="mdi-lead-pencil" />{{ t("employee_leave_edit") }}
      </VToolbarTitle>
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <!-- 新規登録際表示 -->
    <VForm
      ref="formRef"
      v-model="formValid"
      lazy-validation="false"
      @submit.prevent="() => {}"
    >
      <v-container>
        <VTable>
          <VTabs v-model="activeTab" color="primary">
            <VTab
              v-for="item in tabs"
              :key="item.icon"
              :value="item.tab"
              :disabled="isEdit"
            >
              <VIcon size="20" start :icon="item.icon" />
              {{ item.title }}
            </VTab>
          </VTabs>
          <VCardText>
            <VWindow v-model="activeTab">
              <span v-if="errors.leave_type" class="error" style="color: red">{{
                errors.leave_type
              }}</span>
              <VWindowItem value="paid"> </VWindowItem>
              <VWindowItem value="public"> </VWindowItem>
            </VWindow>
            <VRow>
              <VCol cols="12">
                <VLabel>{{ t("employee_name") }}</VLabel>
                <VTextField
                  v-model="formModel.userName"
                  :rules="[validator.required]"
                  variant="outlined"
                  color="primary"
                  name="userName"
                  @focus="showUserList"
                  :disabled="isEdit"
                />
              </VCol>
              <VCol cols="12" v-if="!isEdit">
                <VLabel>{{ t("user_id") }}</VLabel>
                <VTextField
                  v-model="formModel.userId"
                  variant="outlined"
                  color="primary"
                  name="userId"
                  :disabled="!isEdit"
                />
              </VCol>
              <VCol cols="4">
                <VLabel>{{ t("valid_leaves") }}</VLabel>
                <VSelect
                  v-model="formModel.totalDays"
                  :items="numberOptions"
                  variant="outlined"
                  color="primary"
                  name="totalDays"
                />
              </VCol>
              <VCol cols="4">
                <VLabel>{{ t("leave_start") }}</VLabel>
                <VTextField
                  v-model="formModel.validFrom"
                  :rules="[validator.required]"
                  variant="outlined"
                  color="primary"
                  name="validFrom"
                  input
                  type="date"
                />
              </VCol>
              <VCol cols="4">
                <VLabel>{{ t("leave_expired") }}</VLabel>
                <VTextField
                  v-model="formModel.validTo"
                  :rules="[validator.required]"
                  variant="outlined"
                  color="primary"
                  name="validTo"
                  input
                  type="date"
                />
              </VCol>
            </VRow>
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
        >{{ isEdit ? t("update") : t("register") }}
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
    <!-- ユーザー一覧ポップアップ -->
    <VDialog v-model="userListVisible" width="auto" eager>
      <UserList
        v-if="userListVisible"
        :title="t('user_lists')"
        @selectUser="handleUserSelect"
        @update:isVisible="userListVisible = $event"
    /></VDialog>
  </VCard>
</template>
