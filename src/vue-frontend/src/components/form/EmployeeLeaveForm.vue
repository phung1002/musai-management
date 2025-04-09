<!-- 社員休暇　フォーム -->
<script lang="ts" setup>
import { ref, Ref, onMounted, watch, nextTick, computed } from "vue";
import { useI18n } from "vue-i18n";
import { VSelect, VTab } from "vuetify/lib/components/index.mjs";
import { useValidator } from "@/utils/validation";
import { IEmployeeLeaves, ILeaveTypes } from "@/types/type";
import { addEmployeeLeave, updateEmployeeLeave } from "@/api/employeeLeave";
import { getLeavesTree } from "@/api/leave";
import { toast } from "vue3-toastify";
import { ELeaveType } from "@/constants/leaveType";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import EmployeeList from "@/components/ui/EmployeeSearchBox.vue";
import type { VForm } from "vuetify/lib/components/index.mjs";

const formRef = ref<InstanceType<typeof VForm> | null>(null);
const { t } = useI18n(); //日本語にローカル変更用
const emit = defineEmits(["form-cancel", "refetch-data"]);
const errors = ref<{ leave_type?: string; leave_name?: string }>({});
const props = defineProps<{
  employeeLeave?: IEmployeeLeaves;
  isEdit: boolean;
}>(); // 編集対象情報
const leaves = ref<ILeaveTypes[]>([]); // 休暇リスト
const validator = useValidator(t); // バリデーション
const isDialogVisible = ref(false); // 確認ダイアログ表示
const employeeListVisible = ref(false); // ユーザー一覧ポップアップの表示状態
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
const defaultEmployeeLeave = {
  id: null,
  leaveTypeId: 0,
  leaveTypeName: "",
  leaveTypeValue: "",
  employeeFullName: "",
  employeeId: 0,
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
const numberOptions = Array.from({ length: 26 }, (_, i) => i + 0);

// フォームデータの初期化
const formModel = ref<IEmployeeLeaves>(
  props.isEdit
    ? { ...defaultEmployeeLeave, ...props.employeeLeave }
    : { ...defaultEmployeeLeave }
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
  const specialLeave = publicLeave.value.children?.find(
    (child) => child.value === "SPECIAL_LEAVE" && child.children
  );
  const summerDay = specialLeave?.children?.find(
    (child) => child.value === "SUMMER_DAY"
  );
  // 名前とIDの両方を取得
  summerDayName.value = summerDay?.name || "";
  summerDayId.value = summerDay?.id ?? null;
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
  } else {
    formModel.value.leaveTypeId = summerDayId.value ?? 0;
    formModel.value.leaveTypeName = summerDayName.value;
  }
};
// フォーカス時にユーザー一覧ポップアップを表示
const showEmployeeList = () => {
  employeeListVisible.value = true;
};
// 子コンポーネントから受け取る処理
const handleUserSelect = (employee: {
  id: number;
  employeeId: number;
  fullName: string;
}) => {
  formModel.value.employeeId = employee.employeeId;
  formModel.value.employeeFullName = employee.fullName;
};
// 入力初期化
const handleResetForm = async () => {
  formModel.value = props.isEdit
    ? { ...defaultEmployeeLeave, ...props.employeeLeave }
    : { ...defaultEmployeeLeave };
  await nextTick();
  if (formRef.value?.resetValidation) {
    formRef.value.resetValidation();
  }
  // 追加際activeTab現在ままにする
  if (!props.isEdit) {
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

    publicLeave.value =
      leaves.value.find((item) => item.value === ELeaveType.PUBLIC_LEAVE) ||
      defaultLeave;
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
  if (!isValid?.valid) {
    toast.error(t("error.validation_error"));
    return;
  }
  isDialogVisible.value = true;
};
// 確認ダイアログで許可されたらイベント発火
const onConfirmed = async () => {
  if (!props.isEdit) {
    // 新規登録処理を実行
    setleaveTypeId(activeTab.value);
    try {
      await addEmployeeLeave(formModel.value);
      toast.success(t("message.add_success"));
      handleCancel();
      emit("refetch-data");
      isDialogVisible.value = false;
    } catch (error: any) {
      toast.error(t(error.message));
      return;
    }
  } else {
    // 更新処理を実行
    try {
      if (formModel.value.id == null) return;
      await updateEmployeeLeave(formModel.value.id, formModel.value);
      toast.success(t("message.update_success"));
      handleCancel();
      emit("refetch-data");
      isDialogVisible.value = false;
    } catch (error: any) {
      toast.error(t(error.message));
      return;
    }
  }
  handleCancel(); // フォームを閉じる
};
</script>

<template>
  <VCard class="v-card-form">
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
                    v-model="formModel.employeeFullName"
                    :rules="[validator.required]"
                    variant="outlined"
                    color="primary"
                    clearable
                    class="search"
                    name="employeeFullName"
                    :disabled="isEdit"
                    readonly
                  >
                    <template #append-inner>
                      <VBtn
                        icon="mdi-magnify"
                        variant="text"
                        @click="showEmployeeList"
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
    <VDialog v-model="employeeListVisible" width="auto" eager persistent>
      <EmployeeList
        v-if="employeeListVisible"
        :title="t('employee_lists')"
        @selectUser="handleUserSelect"
        @update:isVisible="employeeListVisible = $event"
    /></VDialog>
  </VCard>
</template>
