<!-- 休暇申請　フォーム -->
<script lang="ts" setup>
import { Ref, ref, onMounted, watch, nextTick } from "vue";
import { useI18n } from "vue-i18n";
import { toast } from "vue3-toastify";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { VTab } from "vuetify/lib/components/index.mjs";
import { getLeavesTree } from "@/api/leave";
import { requestLeave, updateLeaveRequest } from "@/api/request";
import { ILeaveTypes, ILeaveRequest } from "@/types/type";
import { useValidator } from "@/utils/validation";
import { ELeaveType } from "@/constants/leaveType";
import type { VForm } from "vuetify/lib/components/index.mjs";

const formRef = ref<InstanceType<typeof VForm> | null>(null);
const isDialogVisible = ref(false);
const leaves = ref<ILeaveTypes[]>([]); // 休暇リスト
const { t } = useI18n();

const isLoading = ref(false);
const isError = ref(false);
const emit = defineEmits(["form:cancel", "refetch-data", "refetch-userleave"]);
const validator = useValidator(t);
const formValid = ref(false);
const props = defineProps<{
  application?: ILeaveRequest;
  isEdit: boolean;
}>();

const defaultRequest: ILeaveRequest = {
  id: null,
  leaveTypeId: null,
  leaveTypeName: "",
  startDate: null,
  endDate: null,
  reason: "",
  status: "",
};
const formModel = ref<ILeaveRequest>(
  props.isEdit
    ? { ...defaultRequest, ...props.application }
    : { ...defaultRequest }
);
// メイン休暇タブで分類
const activeTab = ref(ELeaveType.PAID_LEAVE);
const tabs = ref([
  { title: "", icon: "mdi-gift-open", tab: ELeaveType.PAID_LEAVE },
  { title: "", icon: "mdi-pine-tree-box", tab: ELeaveType.PUBLIC_LEAVE },
]);

const defaultLeave = {
  id: null,
  name: "",
  parentId: null,
  value: "",
  children: [],
};
const paidLeave: Ref<ILeaveTypes> = ref(defaultLeave);
const publicLeave: Ref<ILeaveTypes> = ref(defaultLeave);
const paidBox = ref(null);
const publicBox = ref(null);
const childBox = ref(null);
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
const publicLeaveChilden: Ref<ILeaveTypes | null> = ref(defaultLeave);
//when change value public leave in drop down box
const onPublicLeaveChange = (newValue) => {
  publicLeaveChilden.value =
    publicLeave.value.children?.find(
      (child) => child.id === newValue && child.children
    ) || null;
  childBox.value = null;
  getLeaveTypeId();
};

const getLeaveTypeId = () => {
  formModel.value.leaveTypeId =
    activeTab.value === ELeaveType.PAID_LEAVE && paidLeave.value.id
      ? paidBox.value
      : childBox.value || publicBox.value;
};

watch(activeTab, () => {
  getLeaveTypeId();
});
// コンポーネントがマウントされたときAPI呼び出し修理実行
onMounted(() => {
  fetchLeaveType();
});
// validate and call api
const submiting = ref(false);
const handleSubmit = async () => {
  submiting.value = true;
  if (!props.isEdit) {
    // create
    try {
      await requestLeave(formModel.value);
      toast.success(t("message.add_success"));
      emit("refetch-data");
      emit("refetch-userleave");
      handleCancel();
    } catch (error: any) {
      toast.error(t(error.message));
    }
  } else {
    //update
    try {
      // if update user
      if (formModel.value.id == null) return;
      await updateLeaveRequest(formModel.value.id, formModel.value);
      toast.success(t("message.update_success"));
      handleCancel();
      emit("refetch-data");
      emit("refetch-userleave");
    } catch (error: any) {
      toast.error(t(error.message));
    } finally {
      submiting.value = false;
    }
  }
};
const parseDate = (dateString) => {
  return dateString ? new Date(dateString + "T00:00:00") : null;
};
const calculateWorkingDays = (startDate, endDate) => {
  let count = 0;
  let currentDate = new Date(startDate);
  while (currentDate <= endDate) {
    const dayOfWeek = currentDate.getDay();
    if (dayOfWeek !== 0 && dayOfWeek !== 6) count++;
    currentDate.setDate(currentDate.getDate() + 1);
  }
  return count;
};
const messageConfirm = ref("");
const onConfirm = async () => {
  let halfDayId: number | null = 0;
  let requestDays = 0;
  if (paidLeave.value.children) {
    for (const child of paidLeave.value.children) {
      if (child.value == ELeaveType.HALF_DAY) {
        halfDayId = child.id;
      }
    }
  }
  if (halfDayId == paidBox.value) {
    if (formModel.value.startDate != formModel.value.endDate) {
      toast.error(t("error.half_day_date_must_match"));
      return;
    }
    requestDays = 0.5;
  } else
    requestDays = calculateWorkingDays(
      parseDate(formModel.value.startDate),
      parseDate(formModel.value.endDate)
    );
  // 入力バリデーション
  const isValid = await formRef.value?.validate();
  if (!isValid?.valid) {
    toast.error(t("error.validation_error"));
    return;
  }
  messageConfirm.value = t("message.confirm_leave_application", requestDays);

  isDialogVisible.value = true;
};

const resetForm = async () => {
  formModel.value = props.isEdit
    ? { ...defaultRequest, ...props.application }
    : { ...defaultRequest };
  await nextTick();
  if (formRef.value?.resetValidation) {
    formRef.value.resetValidation();
  }
  paidBox.value = null;
  publicBox.value = null;
  childBox.value = null;
  publicLeaveChilden.value = defaultLeave;
  formValid.value = false;
  activeTab.value = ELeaveType.PAID_LEAVE;
};

const handleCancel = () => {
  emit("form:cancel");
  resetForm();
};
</script>

<template>
  <VCard class="leave_form">
    <VToolbar tag="div">
      <VToolbarTitle>
        <VIcon icon="mdi-lead-pencil" />{{ t("leave_request") }}
      </VToolbarTitle>
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <VForm ref="formRef" v-model="formValid" @submit.prevent="() => {}">
      <VContainer>
        <VTable v-if="!isEdit">
          <VTabs v-model="activeTab" color="primary">
            <VTab v-for="item in tabs" :key="item.icon" :value="item.tab">
              <VIcon size="20" start :icon="item.icon" />
              {{ item.title }}
            </VTab>
          </VTabs>
          <VCardText>
            <VWindow v-model="activeTab">
              <VWindowItem :value="ELeaveType.PAID_LEAVE">
                <VCardText>
                  <VRow>
                    <VCol cols="4" class="dropdown-box">
                      <VAutocomplete
                        v-model="paidBox"
                        :items="paidLeave.children"
                        :label="paidLeave.name"
                        :rules="
                          activeTab === ELeaveType.PAID_LEAVE
                            ? [validator.required]
                            : []
                        "
                        item-title="name"
                        item-value="id"
                        clearable
                        @update:modelValue="getLeaveTypeId"
                      />
                    </VCol>
                  </VRow>
                </VCardText>
              </VWindowItem>
              <VWindowItem :value="ELeaveType.PUBLIC_LEAVE">
                <VCardText>
                  <VRow>
                    <VCol cols="4" class="dropdown-box">
                      <VAutocomplete
                        v-model="publicBox"
                        :items="publicLeave.children"
                        :label="publicLeave.name"
                        :rules="
                          activeTab === ELeaveType.PUBLIC_LEAVE
                            ? [validator.required]
                            : []
                        "
                        item-title="name"
                        item-value="id"
                        clearable
                        @update:modelValue="onPublicLeaveChange"
                      />
                    </VCol>
                    <!-- 特別休暇選択場合　特別休暇リストのみ表示設定 -->
                    <VCol
                      cols="4"
                      class="dropdown-box"
                      v-if="
                        publicLeaveChilden?.children &&
                        publicLeaveChilden.children.length > 0
                      "
                    >
                      <VAutocomplete
                        v-model="childBox"
                        :items="publicLeaveChilden?.children"
                        :label="publicLeaveChilden?.name"
                        :rules="[validator.required]"
                        item-title="name"
                        item-value="id"
                        clearable
                        @update:modelValue="getLeaveTypeId"
                      />
                    </VCol>
                  </VRow>
                </VCardText>
              </VWindowItem>
            </VWindow>
          </VCardText>
        </VTable>
        <VTable v-if="isEdit">
          <VCardText>
            <VRow>
              <VCol cols="3" class="d-flex align-center">
                <VLabel>{{ t("leave_type") }}</VLabel>
              </VCol>
              <VCol cols="9" class="d-flex align-center">
                <VTextField v-model="formModel.leaveTypeName" readonly />
              </VCol>
            </VRow>
          </VCardText>
        </VTable>
        <VDivider />
        <VTable>
          <VCardText>
            <VRow>
              <VCol cols="3" class="d-flex align-center">
                <VLabel class="mr-2">{{ t("leave_duration_from") }}</VLabel>
              </VCol>
              <VCol cols="9">
                <VTextField
                  v-model="formModel.startDate"
                  :rules="[validator.required]"
                  input
                  type="date"
                />
              </VCol>
            </VRow>
            <VRow>
              <VCol cols="3" class="d-flex align-center">
                <VLabel class="mr-2">{{ t("leave_duration_to") }}</VLabel>
              </VCol>
              <VCol cols="9">
                <VTextField
                  v-model="formModel.endDate"
                  :rules="[validator.required]"
                  input
                  type="date"
                />
              </VCol>
            </VRow>
            <VRow>
              <VCol cols="3" class="d-flex align-center">
                <VLabel class="mr-2">{{ t("leave_reason") }}</VLabel>
              </VCol>
              <VCol cols="9">
                <VTextField
                  v-model="formModel.reason"
                  :rules="[validator.required]"
                  type="text"
                />
              </VCol>
            </VRow>
          </VCardText>
        </VTable>
        <VDivider />
      </VContainer>
    </VForm>
    <VCardActions>
      <VBtn @click="onConfirm" type="submit" variant="elevated" color="primary">
        {{ isEdit ? t("update") : t("submit") }}
      </VBtn>
      <VBtn @click="resetForm" type="reset" variant="tonal">
        {{ t("reset") }}
      </VBtn>
    </VCardActions>
    <!-- 確認ダイアログ表示 -->
    <VDialog v-model="isDialogVisible" width="auto" eager>
      <ConfimDialogView
        :title="t('confirm')"
        :message="messageConfirm"
        :isVisible="isDialogVisible"
        @update:isVisible="isDialogVisible = $event"
        @confirmed="handleSubmit"
      />
    </VDialog>
  </VCard>
</template>
