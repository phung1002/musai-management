<script setup lang="ts">
import { ref, computed, nextTick } from "vue";
import { IEmployee } from "@/types/type";
import { useI18n } from "vue-i18n";
import { useValidator } from "@/utils/validation";
import {
  roles,
  defaultEmployee,
  formRules,
  genders,
} from "@/configs/EmployeeFormConfig";
import { createEmployee, updateEmployee } from "@/api/employee";
import { toast } from "vue3-toastify";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { useEmployeeStore } from "@/store/employeeStore";
import { handleLogout } from "@/api/auth";
import type { VForm } from "vuetify/lib/components/index.mjs";
import { fetchEmployeeProfile } from "@/api/auth";

const formRef = ref<InstanceType<typeof VForm> | null>(null);
const employeeStore = useEmployeeStore();
const formatDate = (date: string | null) =>
  date ? new Date(date).toISOString() : null;

const { t } = useI18n();
const submiting = ref(false);
const validator = useValidator(t);
const isDialogVisible = ref(false);
const activeTab = ref("detail_information");
const emit = defineEmits(["form-cancel", "refetch-data"]);
const props = defineProps<{ employee?: IEmployee; isEdit: boolean }>();

const formModel = ref<IEmployee>(
  props.isEdit
    ? { ...defaultEmployee, ...props.employee }
    : { ...defaultEmployee }
);

// Form validation rules
const formRulesConfig = formRules(validator, props.isEdit);
const confirmPassword = ref("");
const formValid = ref(false);

// trans title of roles
const translatedRoles = computed(() =>
  roles.map((role) => ({
    ...role,
    title: t(`roles.${role.value}`),
  }))
);

// trans title of genders
const translatedGenders = computed(() =>
  genders.map((gender) => ({
    ...gender,
    title: t(`${gender.value}`),
  }))
);
const convertDate = (date: string | null): string | null => {
  if (date == null) return null;
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};
const formattedBirthday = computed({
  get: () => convertDate(formModel.value.birthday),
  set: (newDate) => {
    formModel.value.birthday = newDate;
  },
});
const formattedJoinDate = computed({
  get: () => convertDate(formModel.value.joinDate),
  set: (newDate) => {
    formModel.value.joinDate = newDate;
  },
});

const toLogin = ref(false);
const fetchProfile = ref(false);
const messageConfirm = ref("");
const handleSubmit = async () => {
  // 入力バリデーション
  const isValid = await formRef.value?.validate();
  if (!isValid?.valid) {
    toast.error(t("error.validation_error"));
    return;
  }
  if (!props.isEdit) {
    messageConfirm.value = t("register_confirm_message");
    isDialogVisible.value = true;
    // 登録処理を実行
  } else {
    if (formModel.value.id == employeeStore.id) {
      if (
        formModel.value.password.length > 0 ||
        formModel.value.username != employeeStore.username
      ) {
        toLogin.value = true;
      } else fetchProfile.value = true;
    }

    messageConfirm.value = toLogin.value
      ? t("message.admin_change_their_password_or_login_id")
      : t("update_confirm_message");
    isDialogVisible.value = true;
  }
};

// 確認ダイアログで許可されたらイベント発火
const onConfirmed = async (toLogin: boolean) => {
  const payload: IEmployee = {
    ...(formModel.value as IEmployee),
    birthday: formatDate(formModel.value.birthday),
    joinDate: formatDate(formModel.value.joinDate),
  };
  submiting.value = true;
  if (!props.isEdit) {
    try {
      // if create employee
      await createEmployee(payload);
      toast.success(t("message.add_success"));
      emit("refetch-data");
      handleCancel();
    } catch (error: any) {
      toast.error(t(error.message));
      return;
    } finally {
      submiting.value = false;
    }
  } else {
    try {
      // if update employee
      if (formModel.value.id == null) return;
      await updateEmployee(formModel.value.id, payload);
      toast.success(t("message.update_success"));
      handleCancel();
      if (toLogin) {
        handleLogout();
      } else if (fetchProfile.value) {
        emit("refetch-data");
        await fetchEmployeeProfile();
      } else {
        emit("refetch-data");
      }
    } catch (error: any) {
      toast.error(t(error.message));
    } finally {
      submiting.value = false;
    }
  }
};
const resetForm = async () => {
  formModel.value = props.isEdit
    ? { ...defaultEmployee, ...props.employee }
    : { ...defaultEmployee };
  await nextTick();
  if (formRef.value?.resetValidation) {
    formRef.value.resetValidation();
  }
  toLogin.value = false;
  fetchProfile.value = false;
  confirmPassword.value = "";
  formValid.value = false;
  activeTab.value = "detail_information";
};
const handleCancel = () => {
  emit("form-cancel");
  resetForm();
};
</script>

<template>
  <VCard width="940px">
    <VToolbar tag="div">
      <VToolbarTitle v-if="!isEdit">{{ t("employee_register") }}</VToolbarTitle>
      <VToolbarTitle v-else>{{ t("employee_update") }}</VToolbarTitle>
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <VForm ref="formRef" v-model="formValid" lazy-validation="false">
      <VCardText>
        <VTabs v-model="activeTab" color="primary">
          <VTab value="detail_information">{{ t("detail_information") }}</VTab>
          <VTab value="account">{{ t("account") }}</VTab>
        </VTabs>
      </VCardText>
      <VCardText>
        <VWindow v-model="activeTab">
          <VWindowItem value="account" eager>
            <VRow>
              <VCol cols="6">
                <VLabel>{{ t("employee_id") }}</VLabel>
                <VTextField
                  v-model="formModel.employeeId"
                  :rules="formRulesConfig.employeeId"
                  variant="outlined"
                  color="primary"
                  name="employee_id"
                />
              </VCol>
              <VCol cols="6">
                <VLabel>{{ t("email") }}</VLabel>
                <VTextField
                  v-model="formModel.email"
                  :rules="formRulesConfig.email"
                  variant="outlined"
                  color="primary"
                  name="email"
                />
              </VCol>
              <VCol cols="6">
                <VLabel>{{ t("password") }}</VLabel>
                <VTextField
                  v-model="formModel.password"
                  :rules="formRulesConfig.password"
                  variant="outlined"
                  color="primary"
                  name="password"
                  type="password"
                />
              </VCol>
              <VCol cols="6">
                <VLabel>{{ t("password_confirm") }}</VLabel>
                <VTextField
                  v-model="confirmPassword"
                  :rules="[validator.checkEqual(formModel.password)]"
                  variant="outlined"
                  color="primary"
                  name="confirmPassword"
                  type="password"
                />
              </VCol>
              <VCol cols="6">
                <VLabel>{{ t("role") }}</VLabel>
                <VAutocomplete
                  v-model="formModel.roles"
                  :rules="[validator.required]"
                  :items="translatedRoles"
                  variant="outlined"
                  color="primary"
                  name="roles"
                  multiple
                  chips
                  clearable
                >
                </VAutocomplete>
              </VCol>
            </VRow>
          </VWindowItem>
          <VWindowItem value="detail_information" eager>
            <VRow class="d-flex mb-3">
              <VCol cols="6">
                <VLabel>{{ t("full_name") }}</VLabel>
                <VTextField
                  v-model="formModel.fullName"
                  :rules="formRulesConfig.fullName"
                  variant="outlined"
                  color="primary"
                  name="fullName"
                />
              </VCol>
              <VCol cols="6">
                <VLabel>{{ t("full_name_furigana") }}</VLabel>
                <VTextField
                  v-model="formModel.fullNameFurigana"
                  :rules="formRulesConfig.fullNameFurigana"
                  variant="outlined"
                  color="primary"
                  name="fullNameFurigana"
                />
              </VCol>
              <VCol cols="3">
                <VLabel>{{ t("birthday") }}</VLabel>
                <VTextField
                  v-model="formattedBirthday"
                  :rules="[validator.required]"
                  variant="outlined"
                  color="primary"
                  name="birthday"
                  input
                  type="date"
                />
              </VCol>
              <VCol cols="3">
                <VLabel>{{ t("gender") }}</VLabel>
                <VAutocomplete
                  v-model="formModel.gender"
                  :rules="[validator.required]"
                  :items="translatedGenders"
                  variant="outlined"
                  color="primary"
                  name="gender"
                >
                </VAutocomplete>
              </VCol>
              <VCol cols="6">
                <VLabel>{{ t("department") }}</VLabel>
                <VTextField
                  v-model="formModel.department"
                  :rules="[validator.required]"
                  variant="outlined"
                  color="primary"
                  name="department"
                />
              </VCol>
              <VCol cols="6">
                <VLabel>{{ t("work_place") }}</VLabel>
                <VTextField
                  v-model="formModel.workPlace"
                  :rules="[validator.required]"
                  variant="outlined"
                  color="primary"
                  name="workPlace"
                />
              </VCol>
              <VCol cols="3">
                <VLabel>{{ t("join_date") }}</VLabel>
                <VTextField
                  v-model="formattedJoinDate"
                  :rules="[validator.required]"
                  variant="outlined"
                  color="primary"
                  name="joinDate"
                  input
                  type="date"
                />
              </VCol>
            </VRow>
          </VWindowItem>
        </VWindow>
      </VCardText>
    </VForm>
    <VCardActions>
      <VBtn
        type="submit"
        variant="elevated"
        color="primary"
        @click="handleSubmit()"
      >
        {{ isEdit ? t("update") : t("register") }}
      </VBtn>
      <VBtn type="reset" variant="tonal" @click="resetForm">
        {{ t("reset") }}
      </VBtn>
    </VCardActions>
  </VCard>
  <VDialog v-model="isDialogVisible" width="auto" persistent>
    <ConfimDialogView
      :title="t('confirm')"
      :message="messageConfirm"
      :isVisible="isDialogVisible"
      @update:isVisible="isDialogVisible = $event"
      @confirmed="onConfirmed(toLogin)"
    />
  </VDialog>
</template>
