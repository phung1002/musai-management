<script lang="ts" setup>
import { ref, reactive} from 'vue';
import { useI18n } from 'vue-i18n';
import ConfrimDialgView from '@/components/massege/LeaveSubConrimView.vue';
import { VTab } from 'vuetify/lib/components/index.mjs';
import { useLeaveTypesStore } from '@/store/leaveTypesStore';
const showDialog = ref(false);
const { t } = useI18n();
const leaveUserStore = useLeaveTypesStore();
const emit = defineEmits(['form:cancel']);
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
const handleSubmit = () => {
  showDialog.value = true;
};
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
        <!--row1--->
        <v-table>
          <thead>
            <tr >
              <th class="table-label"><label for="leave_type">{{ t('leave_type')}}</label></th>
                <th>
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
                            <VCol :cols="4" class="dropdown-box">
                              <VAutocomplete v-model="filters.paid_leave" :items="paid_leave" :label="t('paid_leave')" />
                            </VCol>
                          </VRow>
                        </VCardText>
                      </VWindowItem>
                      <VWindowItem value="public">
                        <VCardText>
                          <VRow>
                            <VCol :cols="4" class="dropdown-box">
                              <VAutocomplete v-model="filters.public_leave" :items="public_leave" :label="t('public_leave')" />
                            </VCol>
                            
                            <VCol :cols="4" v-if="filters.public_leave.valueOf() === 'SPECIAL_DAY_LEAVE'" class="dropdown-box">
                              <VAutocomplete v-model="filters.special_day_leave" :items="special_day_leave" :label="t('special_day_leave')" />
                            </VCol>
                            <VCol :cols="4" v-if="filters.public_leave.valueOf() === 'SPECIAL_OCCASIONS_LEAVE'" class="dropdown-box">
                              <VAutocomplete v-model="filters.special_occasions_leave" :items="special_occasions_leave" :label="t('special_occasions_leave')" />
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
              <th><VTextField v-model="filters.userid" type="text" readonly/></th>
            </tr>
          </thead>
          <VDivider />
          <!---row3--->
          <thead>
            <tr>
              <th><label for="username">{{t('username')}}</label></th>
              <th><VTextField v-model="filters.username" type="text" readonly/></th>
            </tr>
          </thead>
         <VDivider />
          <!---row4--->
          <thead>
            <tr>
              <th><label for="department">{{t('department')}}</label></th>
              <th><VTextField v-model="filters.department" type="text" readonly/></th>
            </tr>
          </thead>
          <VDivider />
          <!---row5--->
          <thead>
            <tr>
              <th><label for="leave_duration_from">{{t('leave_duration_from')}}</label></th>
              <th><VTextField v-model="filters.leave_duration_from" input type="date" /></th>
            </tr>
          </thead>
          <VDivider />
            <!---row6--->
          <thead>
            <tr>
                <th><label for="leave_duration_to">{{t('leave_duration_to')}}</label></th>
                <th><VTextField v-model="filters.leave_duration_to" input type="date" /></th>
            </tr>
          </thead>
          <VDivider />
          <!---row7--->
          <thead>
            <tr >
              <th><label for="leave_reason">{{t('leave_reason')}}</label></th>
              <th><VTextField v-model="filters.leave_reason" input type="text"/></th>
            </tr>
          </thead>
        </v-table>
        <VDivider />      
        <VCardText class="d-flex gap-4">
          <VBtn @click="handleSubmit" color="primary" class="mr-4">{{ t('submit') }}</VBtn>
          <VBtn @click="handleResetFilter" class="mr-4">{{ t('reset') }}</VBtn>
        </VCardText>
        <VDialog v-model="showDialog" width="auto" eager>
          <ConfrimDialgView  @form:cancel="showDialog = false" />
        </VDialog>
      </v-container>
    </VForm>
  </VCard>
</template>
