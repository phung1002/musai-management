<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue';
import { IUser } from '@/types/type';
import { useI18n } from 'vue-i18n';
import { userValidator } from '@/utils/validation';
import { roles, defaultUser, formRules, genders } from '../../configs/userFormConfig';
import { handleSubmit  } from '../../composables/useUserForm';

const { t } = useI18n();
const submiting = ref(false);
const validator = userValidator(t);
const formRef = ref(null);
const props = defineProps<{ user?: IUser, isEdit: boolean }>();
const activeTab =  ref('account');
const emit = defineEmits(['form:cancel']);

const formModel = reactive<IUser>(
  props.isEdit ? { ...defaultUser, ...props.user } :
  { ...defaultUser }
);
// const formModel: Ref<IUser> = ref({ ...defaultUser });

// Form validation rules
const formRulesConfig = formRules(validator, formModel);
const confirmPassword = ref('');
const formValid = ref(false);

// trans title of genders
const translatedGenders = computed(() =>
  genders.map(gender => ({
    ...gender,
    title: t(`genders.${gender.value}`),
  }))
);
const resetForm = () => {
  Object.assign(formModel, defaultUser);
  // Object.assign(formModel, props.isEdit ? { ...defaultUser, ...props.user } : { ...defaultUser });
  if (formRef.value && formRef.value.resetValidation) {
    formRef.value.resetValidation();
  }

  formValid.value = false;
  activeTab.value = 'account';  // Reset to first tab
};
const handleCancel = () => {
  resetForm();
  emit('form:cancel');
};

// watch(() => props.user, (newUser) => {
//   if (props.isEdit) {
//     Object.assign(formModel, newUser);
//   }
// }, { deep: true });

watch(props, () => {
  if (props.isEdit) {
    Object.assign(formModel, props.user);
  }
});
</script>

<template>
  <VCard width="940px">
    <VToolbar tag="div">
      <VToolbarTitle>{{ t('create_user') }}</VToolbarTitle>
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <!-- <VCardText> -->
      <VForm ref="formRef" v-model="formValid">
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
                      :rules="formRulesConfig.username"
                      variant="outlined"
                      color="primary"
                      name="username"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('email')}}</VLabel>
                    <VTextField
                      v-model="formModel.email"
                      :rules="formRulesConfig.email"
                      variant="outlined"
                      color="primary"
                      name="email"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('password')}}</VLabel>
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
                    <VLabel>{{t('password_confrim')}}</VLabel>
                    <VTextField
                      v-model="confirmPassword"
                      :rules="formRulesConfig.confirmPassword"
                      variant="outlined"
                      color="primary"
                      name="confirmPassword"
                      type="password"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('role')}}</VLabel>
                    <VAutocomplete
                      v-model="formModel.roles"
                      :rules="[validator.required]"
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
                    </VAutocomplete>
                  </VCol>
                </VRow>
              </VWindowItem>
              <VWindowItem value="security">
                <VRow class="d-flex mb-3">
                  <VCol cols="6">
                    <VLabel>{{t('full_name')}}</VLabel>
                    <VTextField
                      v-model="formModel.fullName"
                      :rules="formRulesConfig.fullName"
                      variant="outlined"
                      color="primary"
                      name="fullName"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('full_name_furigana')}}</VLabel>
                    <VTextField
                      v-model="formModel.fullNameFurigana"
                      :rules="formRulesConfig.fullNameFurigana"
                      variant="outlined"
                      color="primary"
                      name="fullNameFurigana"
                    />
                  </VCol>
                  <VCol cols="3">
                    <VLabel>{{t('birthday')}}</VLabel>
                    <VTextField
                      v-model="formModel.birthday"
                      :rules="[validator.required]"
                      variant="outlined"
                      color="primary"
                      name="birthday"
                      input
                      type="date"
                    />
                  </VCol>
                  <VCol cols="3">
                    <VLabel>{{t('gender')}}</VLabel>
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
                    <VLabel>{{t('department')}}</VLabel>
                    <VTextField
                      v-model="formModel.department"
                      :rules="[validator.required]"
                      variant="outlined"
                      color="primary"
                      name="department"
                    />
                  </VCol>
                  <VCol cols="6">
                    <VLabel>{{t('work_place')}}</VLabel>
                    <VTextField
                      v-model="formModel.workPlace"
                      :rules="[validator.required]"
                      variant="outlined"
                      color="primary"
                      name="workPlace"
                    />
                  </VCol>
                  <VCol cols="3">
                    <VLabel>{{t('join_date')}}</VLabel>
                    <VTextField
                      v-model="formModel.joinDate"
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
    <!-- </VCardText> -->
    <VCardActions>
      <VBtn type="submit" variant="elevated" color="primary" @click="handleSubmit(formValid,formModel, submiting)">{{ t('register') }}</VBtn>
      <VBtn type="reset" variant="tonal" @click="handleCancel">{{t('cancel')}}</VBtn>
    </VCardActions>
  </VCard>
</template>
./useUserForm../../configs/userFormConfig
