<script lang="ts" setup>
import { Ref, ref, reactive, onMounted, watch } from "vue";
import { useI18n } from "vue-i18n";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { VTab } from "vuetify/lib/components/index.mjs";
import { useLeaveTypesStore } from "@/store/leaveTypesStore";
import { getLeavesTree } from "@/api/leave";
import { applyLeaveApplication } from "@/api/leaveApplication";
import { ILeaveTypes, ILeaveApplication } from "@/types/type";
import { useValidator } from "@/utils/validation";
import { showSnackbar } from "@/composables/useSnackbar";

const isDialogVisible = ref(false);
const leaves = ref<ILeaveTypes[]>([]); // 休暇リスト
const { t } = useI18n();
const isLoading = ref(false);
const isError = ref(false);
const emit = defineEmits(["form:cancel", "refetch-data"]);
const validator = useValidator(t);
const formRef = ref(null);
const formValid = ref(false);
const props = defineProps<{
  application?: ILeaveApplication;
  isEdit: boolean;
}>();

const defaultApplication: ILeaveApplication = {
  id: null,
  leaveTypeId: null,
  leaveTypeName: "",
  startDate: null,
  endDate: null,
  reason: "",
  status: "",
};
const formModel = reactive<ILeaveApplication>(
  props.isEdit
    ? { ...defaultApplication, ...props.application }
    : { ...defaultApplication }
);
// メイン休暇タブで分類
const activeTab = ref("paid");
const tabs = ref([
  { title: "", icon: "mdi-gift-open", tab: "paid" },
  { title: "", icon: "mdi-pine-tree-box", tab: "public" },
]);

const defaultLeave = {
  id: null,
  name: "",
  parentId: null,
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
      leaves.value.find((item) => item.name === t("paid_leave")) ||
      defaultLeave;
    publicLeave.value =
      leaves.value.find((item) => item !== paidLeave.value) || defaultLeave;

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
const publicLeaveChilden: Ref<ILeaveTypes> = ref(defaultLeave);
//when change value public leave in drop down box
const onPublicLeaveChange = (newValue) => {
  if (publicLeave.value.children) {
    for (const child of publicLeave.value.children) {
      if (child.id == newValue && child.children)
        publicLeaveChilden.value = child;
    }
  }
  childBox.value = null;
  getLeaveTypeId();
};
const getLeaveTypeId = () => {
  if (activeTab.value == "paid" && paidLeave.value.id) {
    formModel.leaveTypeId = paidBox.value;
  } else {
    if (childBox.value == null || childBox.value == "") {
      formModel.leaveTypeId = publicBox.value;
    } else formModel.leaveTypeId = childBox.value;
  }
  console.log(formModel.leaveTypeId);
};
watch(activeTab, (newTab) => {
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
    try {
      await applyLeaveApplication(formModel);
      showSnackbar("add_success", "success");
      emit("refetch-data");
      handleCancel();
    } catch (error) {
      showSnackbar("add_failure", "error");
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

    if (dayOfWeek !== 0 && dayOfWeek !== 6) {
      count++;
    }
    currentDate.setDate(currentDate.getDate() + 1);
  }
  return count;
};


const messageConfirm = ref("")
const onConfirm = async() => {
  const isValid = await formRef.value?.validate();
  if (!isValid.valid) {
    showSnackbar("validation_error", "error");
    return;
  }
  let requestDays = calculateWorkingDays(parseDate(formModel.startDate), parseDate(formModel.endDate));
  messageConfirm.value = t('message.confirm_leave_application', requestDays);

  isDialogVisible.value = true;
};

const resetForm = () => {
  Object.assign(
    formModel,
    props.isEdit
      ? { ...defaultApplication, ...props.application }
      : { ...defaultApplication }
  );
  if (formRef.value && formRef.value.resetValidation) {
    formRef.value.resetValidation();
  }
  paidBox.value = null;
  publicBox.value = null;
  childBox.value = null;
  publicLeaveChilden.value = defaultLeave;
  formValid.value = false;
  activeTab.value = "paid";
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
        <VTable>
          <VTabs v-model="activeTab" color="primary">
            <VTab v-for="item in tabs" :key="item.icon" :value="item.tab">
              <VIcon size="20" start :icon="item.icon" />
              {{ item.title }}
            </VTab>
          </VTabs>
          <VCardText>
            <VWindow v-model="activeTab">
              <VWindowItem value="paid">
                <VCardText>
                  <VRow>
                    <VCol cols="4" class="dropdown-box">
                      <VAutocomplete
                        v-model="paidBox"
                        :items="paidLeave.children"
                        :label="paidLeave.name"
                        :rules="
                          activeTab === 'paid' ? [validator.required] : []
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
              <VWindowItem value="public">
                <VCardText>
                  <VRow>
                    <VCol cols="4" class="dropdown-box">
                      <VAutocomplete
                        v-model="publicBox"
                        :items="publicLeave.children"
                        :label="publicLeave.name"
                        :rules="
                          activeTab === 'public' ? [validator.required] : []
                        "
                        item-title="name"
                        item-value="id"
                        clearable
                        @update:modelValue="onPublicLeaveChange"
                      />
                    </VCol>
                    <!-- 特別休暇選択場合　特別休暇リストのみ表示設定 -->
                    <VCol cols="4" class="dropdown-box">
                      <VAutocomplete
                        v-model="childBox"
                        :items="publicLeaveChilden.children"
                        :label="publicLeaveChilden.name"
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
      <VBtn
        @click="onConfirm"
        type="submit"
        variant="elevated"
        color="primary"
        >{{ t("submit") }}</VBtn
      >
      <VBtn @click="resetForm" type="reset" variant="tonal">{{
        t("reset")
      }}</VBtn>
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
