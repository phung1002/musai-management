<script setup lang="ts">
import { reactive, ref, watch, computed } from "vue";
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
import { showSnackbar } from "@/composables/useSnackbar";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { useUserStore } from "@/store/userStore";
import { logout } from "@/api/auth";
import router from "@/router";

const userStore = useUserStore();
const formatDate = (date: string | null) =>
  date ? new Date(date).toISOString() : null;

const { t } = useI18n();
const submiting = ref(false);
const validator = useValidator(t);
const formRef = ref(null);
const isDialogVisible = ref(false);
const activeTab = ref("account");
const emit = defineEmits(["form-cancel", "refetch-data"]);
const props = defineProps<{ user?: IUser; isEdit: boolean }>();

const formModel = reactive<IUser>(
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
  get: () => convertDate(formModel.birthday),
  set: (newDate) => {
    formModel.birthday = newDate;
  },
});
const formattedJoinDate = computed({
  get: () => convertDate(formModel.joinDate),
  set: (newDate) => {
    formModel.joinDate = newDate;
  },
});

const isChangeYourPassword = async () => {
  const toLogin = ref(false);
  if (formModel.id == userStore.id && formModel.password.length > 0) {
    isDialogVisible.value = true;
    toLogin.value = true;
  } else {
    handleSubmit(toLogin.value);
  }
};

const handleSubmit = async (toLogin: boolean) => {
  const isValid = await formRef.value?.validate();
  if (!isValid.valid) {
    showSnackbar("validation_error", "error");
    return;
  }
  const payload: IUser = {
    ...formModel,
    birthday: formatDate(formModel.birthday),
    joinDate: formatDate(formModel.joinDate),
  };
  submiting.value = true;
  if (!props.isEdit) {
    try {
      // if create user
      await createUser(payload);
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
      submiting.value = false;
    }
  } else {
    try {
      // if update user
      if (formModel.id == null) return;
      await updateUser(formModel.id, payload);
      showSnackbar("update_success", "success");
      handleCancel();
      if (toLogin) {
        logout();
        router.push({
          path: "/login",
        });
      } else {
        emit("refetch-data");
      }
    } catch (error: any) {
      const errorMessage = ["update_failure"];
      if (error.status === 400) {
        errorMessage.push("user_exists");
      } else if (error.status == 403) {
        errorMessage.push("cannot_remove_own_admin_role");
      }
      showSnackbar(errorMessage, "error");
    } finally {
      submiting.value = false;
    }
  }
};

const resetForm = () => {
  Object.assign(
    formModel,
    props.isEdit ? { ...defaultUser, ...props.user } : { ...defaultUser }
  );
  if (formRef.value && formRef.value.resetValidation) {
    formRef.value.resetValidation();
  }
  confirmPassword.value = "";
  formValid.value = false;
  activeTab.value = "account";
};
const handleCancel = () => {
  emit("form-cancel");
  resetForm();
};
</script>

<template>
  <VCard width="940px">
    <VToolbar tag="div">
      <VToolbarTitle v-if="!isEdit">{{ t("create_user") }}</VToolbarTitle>
      <VToolbarTitle v-else>{{ t("update_user") }}</VToolbarTitle>
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <VForm ref="formRef" v-model="formValid" lazy-validation="false">
      <VTabs v-model="activeTab" color="primary">
        <VTab value="account">{{ t("account") }}</VTab>
        <VTab value="security">{{ t("detail_information") }}</VTab>
      </VTabs>
      <VCardText>
        <VWindow v-model="activeTab" eager>
          <VWindowItem value="account">
            <VRow>
              <VCol cols="6">
                <VLabel>{{ t("username") }}</VLabel>
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
          <VWindowItem value="security" eager>
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
        >{{ t("register") }}</VBtn
      >
      <VBtn type="reset" variant="tonal" @click="resetForm">{{
        t("reset")
      }}</VBtn>
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
