<script setup lang="ts">
import { ref, computed, nextTick } from "vue";
import { IUser } from "@/types/type";
import { useI18n } from "vue-i18n";
import { useValidator } from "@/utils/validation";
import {
  roles,
  defaultUser,
  formRules,
  genders,
} from "../../configs/userFormConfig";
import { createUser, updateUser } from "@/api/user";
import { toast } from "vue3-toastify";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { useUserStore } from "@/store/userStore";
import { handleLogout } from "@/api/auth";
import type { VForm } from "vuetify/lib/components/index.mjs";

const formRef = ref<InstanceType<typeof VForm> | null>(null);
const userStore = useUserStore();
const formatDate = (date: string | null) =>
  date ? new Date(date).toISOString() : null;

const { t } = useI18n();
const submiting = ref(false);
const validator = useValidator(t);
const isDialogVisible = ref(false);
const activeTab = ref("detail_information");
const emit = defineEmits(["form-cancel", "refetch-data"]);
const props = defineProps<{ user?: IUser; isEdit: boolean }>();

const formModel = ref<IUser>(
  props.isEdit ? { ...defaultUser, ...props.user } : { ...defaultUser }
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

const isChangeYourPassword = async () => {
  const toLogin = ref(false);
  if (
    formModel.value.id == userStore.id &&
    formModel.value.password.length > 0
  ) {
    isDialogVisible.value = true;
    toLogin.value = true;
  } else {
    handleSubmit(toLogin.value);
  }
};

const handleSubmit = async (toLogin: boolean) => {
  // 入力バリデーション
  const isValid = await formRef.value?.validate();
  if (!isValid?.valid) {
    toast.error(t("error.validation_error"));
    return;
  }
  const payload: IUser = {
    ...(formModel.value as IUser),
    birthday: formatDate(formModel.value.birthday),
    joinDate: formatDate(formModel.value.joinDate),
  };
  submiting.value = true;
  if (!props.isEdit) {
    try {
      // if create user
      await createUser(payload);
      toast.success(t("message.add_success"));
      emit("refetch-data");
      handleCancel();
    } catch (error: any) {
      toast.error(t(error.message));
    } finally {
      submiting.value = false;
    }
  } else {
    try {
      // if update user
      if (formModel.value.id == null) return;
      await updateUser(formModel.value.id, payload);
      toast.success(t("message.update_success"));
      handleCancel();
      if (toLogin) {
        handleLogout();
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
    ? { ...defaultUser, ...props.user }
    : { ...defaultUser };
  await nextTick();
  if (formRef.value?.resetValidation) {
    formRef.value.resetValidation();
  }
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
        <VWindow v-model="activeTab" eager>
          <VWindowItem value="account">
            <VRow>
              <VCol cols="6">
                <VLabel>{{ t("login_id") }}</VLabel>
                <VTextField
                  v-model="formModel.username"
                  :rules="formRulesConfig.username"
                  variant="outlined"
                  color="primary"
                  name="username"
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
        @click="isChangeYourPassword()"
      >
        {{ isEdit ? t("update") : t("register") }}
      </VBtn>
      <VBtn type="reset" variant="tonal" @click="resetForm">
        {{ t("reset") }}
      </VBtn>
    </VCardActions>
  </VCard>
  <VDialog v-model="isDialogVisible" width="auto">
    <ConfimDialogView
      :title="t('confirm')"
      :message="t('message.admin_change_their_password')"
      :isVisible="isDialogVisible"
      @update:isVisible="isDialogVisible = $event"
      @confirmed="handleSubmit(true)"
    />
  </VDialog>
</template>
