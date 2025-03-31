<!-- 社員休暇　フォーム -->
<script lang="ts" setup>
import { ref, Ref, onMounted, watch, nextTick, computed } from "vue";
import { useI18n } from "vue-i18n";
import { VSelect, VTab } from "vuetify/lib/components/index.mjs";
import { useValidator } from "@/utils/validation";
import { IUserLeaves, ILeaveTypes } from "@/types/type";
import { addUserLeave, updateUserLeave } from "@/api/userLeave";
import { getLeavesTree } from "@/api/leave";
import { toast } from "vue3-toastify";
import { ELeaveType } from "@/constants/leaveType";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import UserList from "@/components/ui/UserSearchBox.vue";
import type { VForm } from "vuetify/lib/components/index.mjs";

const formRef = ref<InstanceType<typeof VForm> | null>(null);
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
const formValid = ref(false);
// メイン休暇タブで分類
const activeTab = ref(ELeaveType.PAID_LEAVE); // タブの初期値
const tabs = ref([
  { title: "", icon: "mdi-gift-open", tab: ELeaveType.PAID_LEAVE },
  { title: "", icon: "mdi-pine-tree-box", tab: ELeaveType.PUBLIC_LEAVE },
]);
// デフォルト値
const defaultUserLeave = {
  id: null,
  leaveTypeId: 0,
  leaveTypeName: "",
  leaveTypeValue: "",
  userFullName: "",
  userId: 0,
  remainedDays: 0,
  totalDays: 0,
  usedDays: 0,
  validFrom: "",
  validTo: "",
  name: "",
};
const defaultLeave = {
  id: null,
  name: "",
  parentId: null,
  value: "",
  children: [],
};
// 各カテゴリーの items
const paidLeave: Ref<ILeaveTypes> = ref(defaultLeave);
const publicLeave: Ref<ILeaveTypes> = ref(defaultLeave);
const paidBox = ref(null);
const publicBox = ref(null);
const childBox = ref(null);
// 10～30の配列を生成
const numberOptions = Array.from({ length: 21 }, (_, i) => i + 5);

// フォームデータの初期化
const formModel = ref<IUserLeaves>(
  props.isEdit
    ? { ...defaultUserLeave, ...props.userLeave }
    : { ...defaultUserLeave }
);
// `validFrom` を動的に取得して `validTo` のバリデーションを実行
const checkDateRange = computed(
  () => (value: string) =>
    validator.checkDateRange(formModel.value.validFrom)(value)
);
// コンポーネントがマウントされたときAPI呼び出し修理実行
onMounted(() => {
  fetchLeaveType();
});
const publicLeaveChilden: Ref<ILeaveTypes | null> = ref(defaultLeave);
const summerDayName = ref(""); // "SUMMER_DAY" の名前を格納する
const summerDayId = ref<number | null>(null); // 初期値はnullが安全
const onPublicLeaveChange = () => {
  console.log("publicLeave.value.children?", publicLeave.value.children);

  const specialLeave = publicLeave.value.children?.find(
    (child) => child.value === "SPECIAL_LEAVE" && child.children
  );
  console.log("specialLeave", specialLeave?.id);
  const summerDay = specialLeave?.children?.find(
    (child) => child.value === "SUMMER_DAY"
  );
  console.log("summerDay", summerDay?.id);
  // 名前とIDの両方を取得
  summerDayName.value = summerDay?.name || "";
  summerDayId.value = summerDay?.id ?? null;
  console.log("SUMMER_DAY Name:", summerDayName.value);

  publicLeaveChilden.value = specialLeave || null;
  childBox.value = null;
  getLeaveTypeId();
};
const getLeaveTypeId = () => {
  formModel.value.leaveTypeId =
    activeTab.value === ELeaveType.PAID_LEAVE && paidLeave.value.id
      ? paidBox.value ?? 0
      : (childBox.value ?? 0) || (publicBox.value ?? 0);
};
watch(activeTab, () => {
  getLeaveTypeId();
  onPublicLeaveChange(); // 自動的にSUMMER_DAYを表示
});

// 親IDを設定
const setleaveTypeId = (selectedTab: string) => {
  if (selectedTab === ELeaveType.PAID_LEAVE) {
    formModel.value.leaveTypeId = paidLeave.value.id ?? 0;
    formModel.value.leaveTypeName = paidLeave.value.name;
    console.log(
      "setleaveTypeId",
      formModel.value.leaveTypeId,
      paidLeave.value.id
    );
    console.log(
      "leaveTypeName",
      formModel.value.leaveTypeName,
      paidLeave.value.name
    );
  } else {
    formModel.value.leaveTypeId = summerDayId.value ?? 0;
    formModel.value.leaveTypeName = summerDayName.value;
    console.log(
      "setleaveTypeId",
      formModel.value.leaveTypeId,
      publicLeave.value.id
    );
    console.log(
      "leaveTypeName",
      formModel.value.leaveTypeName,
      publicLeave.value.name
    );
  }
};
// フォーカス時にユーザー一覧ポップアップを表示
const showUserList = () => {
  userListVisible.value = true;
};
// 子コンポーネントから受け取る処理
const handleUserSelect = (user: { id: number; name: string }) => {
  formModel.value.userId = user.id;
  formModel.value.userFullName = user.name;
};
// 入力初期化
const handleResetForm = async () => {
  formModel.value = props.isEdit
    ? { ...defaultUserLeave, ...props.userLeave }
    : { ...defaultUserLeave };
  await nextTick();
  if (formRef.value?.resetValidation) {
    formRef.value.resetValidation();
  }
  // 追加際activeTab現在ままにする
  if (!props.isEdit) {
    console.log("isEdit", props.isEdit, leaves);
    activeTab.value = ELeaveType.PAID_LEAVE;
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
    leaves.value = response;

    // カテゴリーごとに分類
    paidLeave.value =
      leaves.value.find((item) => item.value === ELeaveType.PAID_LEAVE) ||
      defaultLeave;
    console.log("paidLeave.value", paidLeave.value);

    publicLeave.value =
      leaves.value.find((item) => item.value === ELeaveType.PUBLIC_LEAVE) ||
      defaultLeave;
    console.log("publicLeave.value", publicLeave.value.children);
    if (!paidLeave.value || !publicLeave.value) {
      throw new Error("Missing required leave types");
    }
    tabs.value[0].title = paidLeave.value.name;
    tabs.value[1].title = publicLeave.value.name;
  } catch (error) {
    isError.value = true;
    console.error("Error fetching leaves:", error);
  } finally {
    isLoading.value = false;
  }
};
// フォーム送信処理
const handleSubmit = async () => {
  // 入力バリデーション
  const isValid = await formRef.value?.validate();
  console.log("isValid", isValid);
  if (!isValid?.valid) {
    toast.error(t("error.validation_error"));
    return;
  }
  if (!props.isEdit) {
    isDialogVisible.value = true;
    console.log("新しいデータを登録します...");
    // 登録処理を実行
  } else {
    isDialogVisible.value = true;
    console.log("データを更新しますか？...");
  }
};
// 確認ダイアログで許可されたらイベント発火
const onConfirmed = async () => {
  if (!props.isEdit) {
    // 新規登録処理を実行
    setleaveTypeId(activeTab.value);
    console.log("新しいデータを登録します...");
    try {
      console.log(formModel.value);
      await addUserLeave(formModel.value);
      toast.success(t("message.add_success"));
      handleCancel();
      emit("refetch-data");
    } catch (error: any) {
      toast.error(t(error.message));
    } finally {
      isDialogVisible.value = false;
    }
  } else {
    // 更新処理を実行
    console.log("データを更新します...");
    try {
      if (formModel.value.id == null) return;
      console.log("formModel.value", formModel.value);
      console.log("formModel.value ID", formModel.value.id);
      await updateUserLeave(formModel.value.id, formModel.value);
      toast.success(t("message.update_success"));
      handleCancel();
      emit("refetch-data");
    } catch (error: any) {
      toast.error(t(error.message));
    } finally {
      isDialogVisible.value = false;
    }
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
              <VWindowItem :value="ELeaveType.PAID_LEAVE"> </VWindowItem>
              <!-- <VWindowItem value="public"> </VWindowItem> -->
              <VWindowItem :value="ELeaveType.PUBLIC_LEAVE">
                <VLabel>{{ t("leave_type") }}</VLabel>

                <VRow class="mb-4">
                  <VCol>
                    <VTextField
                      v-model="summerDayName"
                      variant="outlined"
                      color="primary"
                      class="search"
                      name="publicLeaveName"
                      readonly
                  /></VCol>
                </VRow>
              </VWindowItem>
            </VWindow>
            <VLabel>{{ t("employee_name") }}</VLabel>
            <VRow>
              <VCol>
                <VToolbar tag="div" color="transparent" flat>
                  <VTextField
                    v-model="formModel.userFullName"
                    :rules="[validator.required]"
                    variant="outlined"
                    color="primary"
                    clearable
                    class="search"
                    name="userFullName"
                    :disabled="isEdit"
                    readonly
                  >
                    <template #append-inner>
                      <VBtn
                        icon="mdi-magnify"
                        variant="text"
                        @click="showUserList"
                        :disabled="isEdit"
                        density="comfortable"
                      />
                    </template>
                  </VTextField>
                </VToolbar>
              </VCol>
            </VRow>
            <VRow>
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
                  :rules="[validator.required, checkDateRange]"
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
    <VDialog v-model="isDialogVisible" width="auto" eager persistent>
      <ConfimDialogView
        :title="t('confirm')"
        :message="
          isEdit ? t('update_confirm_message') : t('register_confirm_message')
        "
        :isVisible="isDialogVisible"
        @update:isVisible="isDialogVisible = $event"
        @confirmed="onConfirmed"
      />
    </VDialog>
    <!-- ユーザー一覧ポップアップ -->
    <VDialog v-model="userListVisible" width="auto" eager persistent>
      <UserList
        v-if="userListVisible"
        :title="t('user_lists')"
        @selectUser="handleUserSelect"
        @update:isVisible="userListVisible = $event"
    /></VDialog>
  </VCard>
</template>
