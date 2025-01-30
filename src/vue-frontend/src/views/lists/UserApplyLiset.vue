<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
// import { useUserStore } from '@/store';
// import { IUser } from '@/api/type';
// import { fetchUser } from '@/api/user';
import LeaveApplyForm from '@/components/forms/LeaveApplyForm.vue';

const { t } = useI18n();
const itemsPerPage = ref(10);
const showFilter = ref(true);
const showDialog = ref(false);
// const users = ref<IUser[]>([]);
// const userStore = useUserStore();
const filters = reactive({
  role: '',
  status: ''
});
const pagination = reactive({
  page: 1,
  pageSize: 10
});

const leaveList = reactive({
  userid: 0,
  username:'',
  leave_type: '',
  leave_duration_from: '',
  leave_duration_to: '',
  leave_reason: '',
  status: 'active'
});
// 👉 search filters
// const roles = userStore.getRoles;
// const status = userStore.getStatusOptions;
const headers = reactive([
  { title: t('leave_type'), key: 'leave_type' },
  { title: t('leave_duration_from'), key: 'leave_duration_from' },
  { title: t('leave_duration_to'), key: 'leave_duration_to' },
  { title: t('leave_reason'), key: 'leave_reason' },
  { title: t('status'), key: 'status' },
  { title: t('action'), key: 'action' }
]);

const loading = ref(true);
// const computeAvatarText = (value) => {
//   if (!value) return '';
//   const nameArray = value.split(' ');
//   return nameArray.map((word) => word.charAt(0).toUpperCase()).join('');
// };
const computeStatusColor = (status) => {
  const statusMap = {
    accepted: 'success',
    pendeing: 'warn'
  };
  return statusMap[status];
};

const loadData = async () => {
  loading.value = true;
  const params = { filter: filters, ...pagination };
//   const { data } = await fetchUser(params);
//   users.value = data;
  loading.value = false;
};
const handleApplyFilter = () => {
  loadData();
};
const handleRefreshItem = () => {
  loadData();
};
const handleCreateItem = () => {
  showDialog.value = true;
};
const handleViewItem = () => {
  console.log('view');
};
const handleEditItem = (row) => {
  Object.assign(leaveList, row);
  showDialog.value = true;
};

const handleDeleteItem = () => {
  showDialog.value = true;
  console.log('delete');
};
const handleClear = () => {
  filters.role = '';
  filters.status = '';
};
// const handleResetFilter = () => {
//   filters.role = '';
//   filters.status = '';
//   loadData();
// };
</script>

<template>
  <section>
    <VRow>
      <VCol cols="12">
        <VCard flat elevation="0" >
          <VToolbar tag="div">
            <VToolbarTitle><VIcon icon= "mdi-list-box-outline"/>{{ t('leave_apply_list') }}</VToolbarTitle>
          </VToolbar>
          <VCardItem class="py-0">
            <VToolbar tag="div" color="transparent" flat>
              <VTextField
                :prepend-icon="!showFilter ? 'mdi-filter-variant-plus' : 'mdi-filter-variant'"
                placeholder="Type something"
                hide-details
                clearable
                variant="plain"
                class="search"
                @keyup.enter="handleApplyFilter"
                @click:prepend="showFilter = !showFilter"
                @click:clear="handleClear"
              />
              <VBtn icon @click="handleApplyFilter" density="comfortable">
                <VIcon>mdi-magnify</VIcon>
              </VBtn>
              <VBtn icon @click="handleRefreshItem" density="comfortable">
                <VIcon>mdi-refresh</VIcon>
              </VBtn>
              <VBtn icon @click="handleCreateItem" density="comfortable">
                <VIcon>mdi-plus</VIcon>
                <VDialog v-model="showDialog" width="auto" eager>
                    <LeaveApplyForm :list="leaveList" @form:cancel="showDialog = false" />
                </VDialog>
              </VBtn>
            </VToolbar>
          </VCardItem>
          <VCardText class="pa-0 pb-5">
            <VDivider />
            <VDataTable
              :headers="headers"
              v-model:items-per-page="itemsPerPage"
              :loading="loading"
              @update:options="loadData"
              hover
              show-select
            >
              <template #item.status="{ item }">
                <VChip :color="computeStatusColor(item.status)">{{ item.status }}</VChip>
              </template>
              <template #item.action="{ item }">
                <VBtn variant="plain" density="compact" icon="mdi-pencil-outline" @click="handleEditItem(item)"> </VBtn>
                <VBtn variant="plain" density="compact" icon="mdi-trash-can-outline"> </VBtn>
                <VMenu :close-on-content-click="false">
                  <template v-slot:activator="{ props }">
                    <VBtn variant="plain" density="compact" icon="mdi-dots-vertical" v-bind="props"> </VBtn>
                  </template>
                  <VSheet rounded="md" width="200" elevation="10" class="mt-2">
                    <VList lines="one" density="compact" class="pa-0" color="primary">
                      <VListItem @click="handleViewItem">{{ t('view') }}</VListItem>
                      <VListItem @click="handleEditItem">{{ t('edit') }}</VListItem>
                      <VListItem @click="handleDeleteItem">{{ t('delete') }}</VListItem>
                    </VList>
                  </VSheet>
                </VMenu>
              </template>
            </VDataTable>
          </VCardText>
        </VCard>
      </VCol>
    </VRow>
  </section>
</template>

<style lang="scss">
.search {
  input {
    padding-top: 10px;
  }
}
</style>
