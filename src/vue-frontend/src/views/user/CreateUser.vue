<script setup lang="ts">
import { reactive, watch } from 'vue';
import { useUserStore } from '@/store/userStore';
import { IUser } from '@/api/type';
import { useI18n } from 'vue-i18n';
import { ref } from 'vue';
import { createUser, getAllUsers } from '@/api/user';
import { ERole } from '@/constants/role';
import SnackBar from '@/components/layout/SnackBar.vue';

const { t } = useI18n();
const props = defineProps<{ user?: IUser, isEdit: boolean }>();
const emit = defineEmits(['form:cancel']);
const roles = Object.keys(ERole).map(key => ({
  title: t(key.toLocaleLowerCase()),
  value: ERole[key as keyof typeof ERole]
}));
const formRules = reactive({
  username: [
    (value: string) => {
      if (value) return true;
      return t('username')+ t('requied');
    }
  ],
  email: [
    (value: string) => {
      if (value) return true;
      return t('email')+ t('requied');
    }
  ],
  password: [
    (value: string) => {
      if (value) return true;
      return t('password')+ t('requied');
    }
  ],
  confirmPassword: [
    (value: string) => {
      if (value) return true;
      return t('password_confrim')+ t('requied');
    }
  ],
  fullName: [
    (value: string) => {
      if (value) return true;
      return t('full_name')+ t('requied');
    }
  ],
  fullNameFurigana: [
    (value: string) => {
      if (value) return true;
      return t('full_name_furigana')+ t('requied');
    }
  ],
  birthday: [
    (value: string) => {
      if (value) return true;
      return t('full_name_furigana')+ t('requied');
    }
  ],
  roles: [
    (value: string[]) => {
      console.log(value);

      if (value) return true;
      return t('role')+ t('requied');
    }
  ],
  department: [
    (value: string) => {
      if (value) return true;
      return t('department')+ t('requied');
    }
  ],
  workPlace: [
    (value: string) => {
      if (value) return true;
      return t('work_place')+ t('requied');
    }
  ],
  joinDate: [
    (value: string) => {
      if (value) return true;
      return t('join_date')+ t('requied');
    }
  ],
  gender: [
    (value: string) => {
      if (value) return true;
      return t('gender')+ t('requied');
    }
  ],
});
const defaultUser: IUser = {
  id: null,
  username: '',
  email: '',
  password: '',
  fullName: '',
  fullNameFurigana: '',
  birthday: null,
  roles: [],
  department: '',
  workPlace: '',
  joinDate: null,
  gender: '',
};
const formModel = reactive<IUser>(
  props.isEdit ? { ...defaultUser, ...props.user } :
  { ...defaultUser }
);

const confirmPassword =ref('');
const submiting = ref(false);
const formValid = ref(false);
const errorMessage = ref('');

// SnackBar
const snackbar = ref({
  isColorSnackbarVisible : false,
  message : '',
  color : 'error'
});

//Submit
const handleSubmit = async () => {
  if (!formValid.value) {
    snackbar.value.isColorSnackbarVisible = true
    snackbar.value.color = 'error'
    snackbar.value.message = t('message.add_failure')
    return;
  }
  const payload : IUser = {
    ...formModel,
    birthday: formModel.birthday ? new Date(formModel.birthday).toISOString() : null,
    joinDate: formModel.joinDate ? new Date(formModel.joinDate).toISOString() : null,
  };
  submiting.value = true;
  try {
    await createUser(payload);
    errorMessage.value = '';
    getAllUsers();
    handleCancel();
    snackbar.value.isColorSnackbarVisible = true
    snackbar.value.color = 'success'
    snackbar.value.message = t('message.add_success')
  } catch (error) {

  } finally {
    submiting.value = false;
  }
};
const handleCancel = () => {
  emit('form:cancel');
};

watch(props, () => {
  if (props.isEdit) {
    Object.assign(formModel, props.user);
  }
});
const activeTab =  ref('account');
</script>

<template>
  <SnackBar :snackbar="snackbar"></SnackBar>
  <VCard width="940px">
    <VToolbar tag="div">
      <VToolbarTitle>{{ t('create_user') }}</VToolbarTitle>
      <VBtn icon="mdi-close" @click="$emit('form:cancel')"></VBtn>
    </VToolbar>
    <!-- <VCardText> -->
      <VForm>
        <VLabel>{{errorMessage}}</VLabel>
        <VTabs v-model="activeTab" color="primary">
          <VTab value="account">{{ t('account') }}</VTab>
          <VTab value="security">{{ t('detail_information') }}</VTab>
        </VTabs>
        <VCardText>
            <VWindow v-model="activeTab" >
              <VWindowItem value="account">
                <VRow>
                  <VCol cols="6">
                    <VLabel>{{t('username')}}</VLabel>
                    <VTextField
                      v-model="formModel.username"
                      :rules="formRules.username"
                      variant="outlined"
                      color="primary"
                      name="username"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('email')}}</VLabel>
                    <VTextField
                      v-model="formModel.email"
                      :rules="formRules.email"
                      variant="outlined"
                      color="primary"
                      name="email"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('password')}}</VLabel>
                    <VTextField
                      v-model="formModel.password"
                      :rules="formRules.password"
                      variant="outlined"
                      color="primary"
                      name="password"
                      type="password"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('password_confrim')}}</VLabel>
                    <VTextField
                      v-model="confirmPassword"
                      :rules="formRules.confirmPassword"
                      variant="outlined"
                      color="primary"
                      name="confirmPassword"
                      type="password"
                    />
                  </VCol>
                </VRow>
              </VWindowItem>
              <VWindowItem value="security">
                <VRow class="d-flex mb-3">
                  <VCol cols="6">
                    <VLabel>{{t('full_name')}}</VLabel>
                    <VTextField
                      v-model="formModel.fullName"
                      :rules="formRules.fullName"
                      variant="outlined"
                      color="primary"
                      name="fullName"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('full_name_furigana')}}</VLabel>
                    <VTextField
                      v-model="formModel.fullNameFurigana"
                      :rules="formRules.fullNameFurigana"
                      variant="outlined"
                      color="primary"
                      name="fullNameFurigana"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('birthday')}}</VLabel>
                    <VTextField
                      v-model="formModel.birthday"
                      :rules="formRules.birthday"
                      variant="outlined"
                      color="primary"
                      name="birthday"
                      input
                      type="date"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('gender')}}</VLabel>
                    <VRadioGroup v-model="formModel.gender" :rules="formRules.gender" row name="gender" class="gender-radio-group" >
                      <VRadio :label="t('male')" :value="'male'" />
                      <VRadio :label="t('female')" :value="'female'" />
                    </VRadioGroup>
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('department')}}</VLabel>
                    <VTextField
                      v-model="formModel.department"
                      :rules="formRules.department"
                      variant="outlined"
                      color="primary"
                      name="department"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('work_place')}}</VLabel>
                    <VTextField
                      v-model="formModel.workPlace"
                      :rules="formRules.workPlace"
                      variant="outlined"
                      color="primary"
                      name="workPlace"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('role')}}</VLabel>
                    <VAutocomplete
                      v-model="formModel.roles"
                      :items="roles"
                      item-title="title"
                      item-value="value"
                      variant="outlined"
                      color="primary"
                      name="roles"
                      multiple
                      chips
                      clearable
                    >
                    <template v-slot:selection="{ item }">
                      <span>{{ t(item.title) }}</span>
                    </template>
                    <template v-slot:default="{ item }">
                      <span>{{ t(item.title) }}</span>
                    </template>
                    </VAutocomplete>
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('join_date')}}</VLabel>
                    <VTextField
                      v-model="formModel.joinDate"
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
    <!-- </VCardText> -->
    <VCardActions>
      <VBtn type="submit" variant="outlined" color="primary" @click="handleSubmit">{{ t('register') }}</VBtn>
      <VBtn @click="handleCancel">{{t('cancel')}}</VBtn>
    </VCardActions>
  </VCard>
</template>
