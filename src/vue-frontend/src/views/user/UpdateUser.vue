<script setup lang="ts">
import { reactive, watch } from 'vue';
import { useUserStore } from '@/store/userStore';
import { IUser } from '@/api/type';
import { useI18n } from 'vue-i18n';
import { ref } from 'vue';
import { createUser } from '@/api/user';

interface IModel {
  user: IUser;
}

const { t } = useI18n();
const props = defineProps<IModel>();
const emit = defineEmits(['form:cancel']);
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
const formModel = reactive<IUser>({
  ...props.user
});

const userStore = useUserStore();
const submiting = ref(false);
const formValid = ref(true);
const handleSubmit = async () => {
  console.log(formModel);

  if (formValid.value === true) {
    submiting.value = true;
      try {
        await createUser(formModel);
        // errorMessage.value = '';
      } catch (error) {

      } finally {
        submiting.value = false;
      }
    }
};
const handleCancel = () => {
  emit('form:cancel');
};

watch(props, () => {
  Object.assign(formModel, props.user);
});
</script>

<template>
  <VCard width="940px">
    <VToolbar tag="div">
      <VToolbarTitle>{{ t('create_user') }}</VToolbarTitle>
      <VBtn icon="mdi-close" @click="$emit('form:cancel')"></VBtn>
    </VToolbar>
    <VCardText>
      <VForm>
        <VRow class="d-flex mb-3">
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
            />
          </VCol>
          <VCol cols="6">
            <VLabel>{{t('password_confrim')}}</VLabel>
            <VTextField
              v-model="formModel.confirmPassword"
              :rules="formRules.confirmPassword"
              variant="outlined"
              color="primary"
              name="confirmPassword"
            />
          </VCol>
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
            <VLabel>{{t('role')}}</VLabel>
            <VAutocomplete
              v-model="formModel.roles"
              :items="userStore.roles"
              variant="outlined"
              color="primary"
              name="roles"
              multiple
              chips
              clearable
            />
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
            <VLabel>{{t('join_date')}}</VLabel>
            <VTextField
              v-model="formModel.joinDate"
              :rules="formRules.joinDate"
              variant="outlined"
              color="primary"
              name="joinDate"
            />
          </VCol>
          <VCol cols="6">
            <VLabel>{{t('gender')}}</VLabel>
            <VTextField
              v-model="formModel.gender"
              :rules="formRules.gender"
              variant="outlined"
              color="primary"
              name="gender"
            />
          </VCol>
        </VRow>
      </VForm>
    </VCardText>
    <VCardActions>
      <VBtn type="submit" variant="outlined" color="primary" @click="handleSubmit">{{ t('register') }}</VBtn>
      <VBtn @click="handleCancel">{{t('cancel')}}</VBtn>
    </VCardActions>
  </VCard>
</template>
