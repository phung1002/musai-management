<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import ConfimDialogView from '../massege/ConfimDialogView.vue';
import { VTab } from 'vuetify/lib/components/index.mjs';
import { useLeaveTypesStore } from '@/store/leaveTypesStore';
const isDialogVisible = ref(false);
const errorMessage = ref('');
const { t } = useI18n();
const leaveUserStore = useLeaveTypesStore();
const emit = defineEmits(['form:cancel']);
// // エラーメッセージデータの型定義
interface Errors {
  leave_type: string;
  leave_duration_from: string;
  leave_duration_to: string;
  leave_reason: string;
}
// エラーメッセージ
const errors = ref<Errors>({
  leave_type: '',
  leave_duration_from: '',
  leave_duration_to: '',
  leave_reason: ''
});

// フォームデータ
const filters = reactive({
  userid: '',
  username: '',
  paid_leave: '',
  public_leave: '',
  special_day_leave: '',
  special_occasions_leave: '',
  department: '',
  leave_duration_from: '',
  leave_duration_to: '',
  leave_reason: '',
});
// 入力初期化
const handleResetFilter = () => {
  filters.paid_leave = '';
  filters.public_leave = '';
  filters.special_day_leave = '';
  filters.special_occasions_leave = '';
  filters.department = '';
  filters.leave_duration_from = '';
  filters.leave_duration_to = '';
  filters.leave_reason = '';
  filters.userid = '';
  filters.username = ''
};
// エラーメッセージ初期化
const resetErrors = () => {
  errors.value = {
    leave_type: '',
    leave_duration_from: '',
    leave_duration_to: '',
    leave_reason: ''
  };
};
// 入力チェック　バリデーションを行う
const handleSubmit = () => {
  
  resetErrors();  // フォーム送信前にエラーメッセージをリセット
  let valid = true
  
  // leave_type のバリデーション
  if (
      filters.paid_leave.valueOf() === '' && 
      filters.public_leave.valueOf() === '' 
    ) {
      errors.value.leave_type = t('leave_type') + t('required');
      valid = false;
  } else if (
    ( filters.public_leave.valueOf() !== '' && filters.special_occasions_leave.valueOf() === '') && 
    ( filters.public_leave.valueOf() !== '' && filters.special_day_leave.valueOf() === '')
  ) {
    errors.value.leave_type = t('leave_type') + t('must_select_one_category');
    valid = false;
  } 

  // leave_duration_from のバリデーション
  if (filters.leave_duration_from.valueOf() === '') {
    errors.value.leave_duration_from = t('leave_duration_from') + t('required');
    valid = false;
  } else if (filters.leave_duration_from.valueOf() > filters.leave_duration_to.valueOf()) {
    errors.value.leave_duration_to = t('leave_duration_from') + t('invalid_range');
    valid = false;
  }

  // leave_duration_to のバリデーション 
  if (filters.leave_duration_to.valueOf() === '') {
    errors.value.leave_duration_to = t('leave_duration_to') + t('required');
    valid = false;
  } else if (filters.leave_duration_from.valueOf() > filters.leave_duration_to.valueOf()) {
    errors.value.leave_duration_to = t('leave_duration_from') + t('invalid_range');
    valid = false;
  }

  // 休暇　理由
  if (filters.leave_reason.valueOf() === '' ) {
    errors.value.leave_reason = t('leave_reason')+ t('required');
    valid = false;
  } 
  // バリデーション通過後、フォームデータを送信
  if (valid) {
    // 確認ポップアップを表示
      isDialogVisible.value = true;
      console.log('確認ポップアップを表示');
    }
};
const onConfirmed = () => {
  console.log("許可されました");
  // ここに処理を追加
};

// フォームの送信処理
// const handleSubmit = () => {
//   // エラーをリセット
//   errors.value = { 
//     paid_leave: null,
//     public_leave: null,
//     special_day_leave: null,
//     special_occasions_leave: null,
//     leave_duration_from: null,
//     leave_duration_to: null,
//     leave_reason: null,
//   }

//   let valid = true

//   // パスワードチェック: 必須
//   if (!form.value.paid_leave) {
//     errors.value.paid_leave = t('pw_required_error')
//     valid = false
//   }

//   // 再パスワードチェック: 必須
//   if (!form.value.public_leave) {
//     errors.value.public_leave = t('confirm_pw_required_error')
//     valid = false
//   }
//   // バリデーション通過後、フォームデータを送信
//   if (valid) {
//     // 確認ポップアップを表示
//       isDialogVisible.value = true;
//     }
// }
// const onConfirmed = () => {
//   console.log("許可されました");
//   // ここに処理を追加
// };
// ////

// const formRules = reactive({
//   public_leave: [
//     (value: string) => {
//       if (value) return true;
//       return t('public_leave')+ t('requied');
//     }
//   ],
//   paid_leave: [
//     (value: string) => {
//       if (value) return true;
//       return t('paid_leave')+ t('requied');
//     }
//   ],
//   special_day_leave: [
//     (value: string) => {
//       if (value) return true;
//       return t('special_day_leave')+ t('requied');
//     }
//   ],
//   special_occasions_leave: [
//     (value: string) => {
//       if (value) return true;
//       return t('special_occasions_leave')+ t('requied');
//     }
//   ],
//   leave_duration_from: [
//     (value: string) => {
//       if (value) return true;
//       return t('leave_duration_from')+ t('requied');
//     }
//   ],
//   leave_duration_to: [
//     (value: string) => {
//       if (value) return true;
//       return t('leave_duration_to')+ t('requied');
//     }
//   ],
//   leave_reason: [
//     (value: string) => {
//       if (value) return true;
//       return t('leave_reason')+ t('requied');
//     }
//   ],
// });
// const submiting = ref(false);
// const formValid = ref(true);
// const handleCancel = () => {
//   emit('form:cancel');
// };
// const handleSubmit = async () => {
//   if (formValid.value === true) {
//     submiting.value = true;
//       try {
//         errorMessage.value = '';
//         // handleCancel();
//         showDialog.value = true;
//       } catch (error) {
 
//       } finally {
//         submiting.value = false;
//       }
//     }
//     // showDialog.value = true;
// };
// const handleSubmit = () => {
//   showDialog.value = true;
// };

// メイン休暇タブで分類
const activeTab = ref('personal-info');
const tabs = [
  { title: t('paid_leave'), icon: 'mdi-gift-open', tab: 'paid' },
  { title: t('public_leave'), icon: 'mdi-pine-tree-box', tab: 'public' },
];
// 休暇区分情報取得
const paid_leave = leaveUserStore.getPaidLeave;
const public_leave = leaveUserStore.getPublicLeave;
const special_day_leave = leaveUserStore.getSpecialDayLeave;
const special_occasions_leave = leaveUserStore.getSpecialOccasionsLeave
</script>

<template>
  <VCard class="leave_form">
    <VToolbar tag="div">
      <VToolbarTitle><VIcon icon= "mdi-lead-pencil"/>{{ t('leave_applying') }}</VToolbarTitle>
      <VBtn icon="mdi-close" @click="$emit('form:cancel')"></VBtn>
    </VToolbar>
    <VForm @submit.prevent="() => {}">
      <v-container >
        <VLabel>{{errorMessage}}</VLabel>
        <!--row1--->
        <!-- 休暇　区分 -->
        <v-table>
          <thead>
            <tr >
              <th class="table-label"><label for="leave_type">{{ t('leave_type')}}</label></th>
                <th>
                  <VTabs v-model="activeTab" color="primary">
                    <VTab v-for="item in tabs" :key="item.icon" :value="item.tab" >
                      <VIcon size="20" start :icon="item.icon" />
                      {{ item.title }}
                    </VTab>
                  </VTabs>
                  <VCardText >
                    <VWindow v-model="activeTab">
                      <span v-if="errors.leave_type" class="error" style="color: red;">{{ errors.leave_type }}</span>
                      <VWindowItem value="paid">
                        <VCardText>
                          <VRow>
                            <VCol :cols="4" class="dropdown-box">
                              <VAutocomplete 
                              v-model="filters.paid_leave" 
                              :items="paid_leave" 
                              :label="t('paid_leave')" 
                              />
                            </VCol>
                          </VRow>
                        </VCardText>
                      </VWindowItem>
                      <VWindowItem value="public">
                        <VCardText>
                          <VRow>
                            <VCol :cols="4" class="dropdown-box">
                              <VAutocomplete 
                              v-model="filters.public_leave" 
                              :items="public_leave" 
                              :label="t('public_leave')" 
                              />
                            </VCol>
                            <!-- 特別休暇選択場合　特別休暇リストのみ表示設定 -->
                            <VCol :cols="4" v-if="filters.public_leave.valueOf() === 'SPECIAL_DAY_LEAVE'" class="dropdown-box">
                              <VAutocomplete 
                              v-model="filters.special_day_leave" 
                              :items="special_day_leave" 
                              :label="t('special_day_leave')" 
                              />
                            </VCol>
                            <!-- 慶弔休暇選択場合　慶弔休暇リストのみ表示設定 -->
                            <VCol :cols="4" v-if="filters.public_leave.valueOf() === 'SPECIAL_OCCASIONS_LEAVE'" class="dropdown-box">
                              <VAutocomplete 
                              v-model="filters.special_occasions_leave" 
                              :items="special_occasions_leave" 
                              :label="t('special_occasions_leave')" 
                              />
                            </VCol>
                          </VRow>
                        </VCardText>
                      </VWindowItem>
                    </VWindow>
                  </VCardText>
                </th>
            </tr>
          </thead>
        </v-table>
        <!--row2--->
        <VDivider />
        <v-table>
          <thead>
            <tr >
              <th><label for="id">{{ t('userid') }}</label></th>
              <th><VTextField 
                v-model="filters.userid" 
                type="text" 
                readonly 
                />
              </th>
            </tr>
          </thead>
          <VDivider />
          <!---row3--->
          <thead>
            <tr>
              <th><label for="username">{{t('username')}}</label></th>
              <th><VTextField 
                v-model="filters.username" 
                type="text" 
                readonly
                />
              </th>
            </tr>
          </thead>
         <VDivider />
          <!---row4--->
          <thead>
            <tr>
              <th><label for="department">{{t('department')}}</label></th>
              <th><VTextField 
                v-model="filters.department" 
                type="text" 
                readonly
                />
              </th>
            </tr>
          </thead>
          <VDivider />
          <!---row5--->
          <thead>
            <tr>
              <th><label for="leave_duration_from">{{t('leave_duration_from')}}</label></th>
              <th><VTextField 
                v-model="filters.leave_duration_from" 
                input type="date" 
                />
                <span v-if="errors.leave_duration_from" class="error" style="color: red;">{{ errors.leave_duration_from}}</span>
              </th>
            </tr>
          </thead>
          <VDivider />
            <!---row6--->
          <thead>
            <tr>
                <th><label for="leave_duration_to">{{t('leave_duration_to')}}</label></th>
                <th><VTextField 
                  v-model="filters.leave_duration_to" 
                  input type="date" 
                  />
                  <span v-if="errors.leave_duration_to" class="error" style="color: red;">{{ errors.leave_duration_to }}</span>
                </th>
            </tr>
          </thead>
          <VDivider />
          <!---row7--->
          <thead>
            <tr >
              <th><label for="leave_reason">{{t('leave_reason')}}</label></th>
              <th><VTextField 
                v-model="filters.leave_reason" 
                input type="text"
                />
                <span v-if="errors.leave_reason" class="error" style="color: red;">{{ errors.leave_reason}}</span>
              </th>
            </tr>
          </thead>
        </v-table>
        <VDivider />      
        <VCardText class="d-flex gap-4">
          <VBtn @click="handleSubmit" color="primary" class="mr-4">{{ t('submit') }}</VBtn>
          <VBtn @click="handleResetFilter" class="mr-4">{{ t('reset') }}</VBtn>
        </VCardText>
        <!-- 確認ダイアログ表示 -->
        <VDialog v-model="isDialogVisible" width="auto" eager>
          <ConfimDialogView 
          :title="t('confrim')"
          :message="t('leave_apply_con_msg')"
          :isVisible="isDialogVisible"
          @update:isVisible="isDialogVisible = $event"
          @confirmed="onConfirmed"
        />
        </VDialog>
      </v-container>
    </VForm>
  </VCard>
</template>
