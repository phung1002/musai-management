<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import LeaveApplyForm from '@/components/forms/LeaveApplyForm.vue';
import ConfimDialogView from '@/components/massege/ConfimDialogView.vue'
import { getAllUsers } from '@/api/user';
import { getApplyLists} from '@/api/requst';
const { t } = useI18n();
const showFilter = ref(true);
const isDialogVisible = ref(false);
const applyFrom  = ref(false);
const editForm  = ref(false);
const loading = ref(true);
const users = ref<User[]>([]);
// const leaveReqlist = ref<LeaveRequestList[]>([]);
const isLoading = ref(false);
const isError = ref(false);
const loadData = async () => {
  loading.value = true;
  const params = { filter: filters, ...pagination };
  loading.value = false;
};
const handleApplyFilter = () => {
  loadData();
};
const handleCreateItem = () => {
  applyFrom.value = true;
};
const handleDeleteItem = (id: number) => {
  isDialogVisible.value = true;
  console.log('delete', id);
};
const handleEditItem = (id: number) => {
  editForm.value = true;
  console.log('edit', id);
};
const handleClear = () => {
  filters.role = '';
  filters.status = '';
};
const filters = reactive({
  role: '',
  status: ''
});
const pagination = reactive({
  page: 1,
  pageSize: 10
});
// ⇩⇩⇩⇩　ここからデバッグ用　⇩⇩⇩⇩⇩
const headers = reactive([
  { title: t('number'), key: 'no' },
  { title: t('username'), key: 'username' },
  { title: t('email'), key: 'email' },
  { title: t('full_name'), key: 'fullName' },
  { title: t('gender'), key: 'gender' },
  { title: t('department'), key: 'department' },
  { title: t('work_place'), key: 'workPlace' },
  { title: t('action'), key: 'action' },
]);
interface User {
  id: number;
  username: string;
  email: string;
  full_name: string;
  gender : string;
  department: string;
  workPlace: string;
}
// Get list user from api API
const fetchUsers = async () => {
  isLoading.value = true;
  isError.value = false;

  try {
    const response = await getAllUsers(); // Call API
    users.value = response.data.map((user: User) => ({
      ...user,
    }));
  } catch (error) {
    isError.value = true;
    console.error('Error fetching users:', error);
  } finally {
    isLoading.value = false;
  }
};

// ⇑⇑⇑　ここまで　⇑⇑⇑

// // 申請　リスト　インターフェース
// interface LeaveRequestList {
//   leave_type: string;
//   leave_duration_from: string;
//   leave_duration_to: string;
//   leave_reason : string;
//   status: string;
// }
// // テーブル　ヘッダー
// const headers = reactive([
//   { title: t('leave_type'), key: 'leave_type' },
//   { title: t('leave_duration_from'), key: 'leave_duration_from' },
//   { title: t('leave_duration_to'), key: 'leave_duration_to' },
//   { title: t('leave_reason'), key: 'leave_reason' },
//   { title: t('status'), key: 'status' },
//   { title: t('action'), key: 'action' }
// ]);
// // GET申請リスト　API　
// const fetchRequests = async () => {
//   isLoading.value = true;
//   isError.value = false;

//   try {
//     const response = await getApplyList(); //  API呼び出し
//     leaveReqlist.value = response.data.map((LeaveRequestList: leaveReqlist) => ({
//       ...LeaveRequestList,
//     }));
//   } catch (error) {
//     isError.value = true;
//   } finally {
//     isLoading.value = false;
//   }
// };
// Call API when component is mounted
onMounted(() => {
  fetchUsers();
});
const onDeleted = () => {
  console.log("削除されました");
  // ここに処理を追加
};
</script>

<template>
  <section>
    <VRow>
      <VCol cols="12">
      <VContainer class="app-container">
        <VCard flat elevation="0" >
          <VToolbar tag="div">
            <VToolbarTitle><VIcon icon= "mdi-list-box-outline"/>{{ t('leave_apply_list') }}</VToolbarTitle>
            <!-- 申請入力フォーム　ボタン-->
            <VCardActions>
              <VSpacer />
              <VBtn color="primary" variant="elevated" @click="handleCreateItem"><v-icon icon="mdi-plus" start></v-icon>{{ t('leave_applying') }}
                <VDialog v-model="applyFrom" width="auto" eager>
                  <LeaveApplyForm @form:cancel="applyFrom = false" />
                </VDialog>
              </VBtn>
            </VCardActions>
          </VToolbar>
          <!-- 検索バー -->
          <VCardItem class="py-0">
            <VToolbar tag="div" color="transparent" flat>
              <VTextField
                :prepend-icon="'mdi-filter-variant'"
                :placeholder="t('type_something')"
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
            </VToolbar>
          </VCardItem>
          <VDivider />
          <!-- 申請情報　表示 -->
          <VCardItem>
            <VDataTable
              :headers="headers"
              :items="users"
              :items-per-page-text="t('items_per_page')"
              v-if="!isLoading && !isError"
            >
              <!-- 表示　番号設定  -->
              <template v-slot:item.no="{ index }">
                {{ index + 1 }}
              </template>
               <!-- アクション　設定  -->
              <template v-slot:item.action="{ item }">
                <div class="action-buttons">
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="handleEditItem"
                  >
                    <VIcon color="blue">mdi-pencil</VIcon>
                    <VDialog v-model="editForm" width="auto" eager>
                      <LeaveApplyEditFormView :user="item" @form:cancel="editForm = false" />
                    </VDialog>
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="handleDeleteItem"
                  >
                    <VIcon color="red">mdi-delete</VIcon>
                    <VDialog v-model="isDialogVisible" width="auto" eager>
                      <ConfimDialogView 
                      :title="t('confrim')"
                      :message="t('delete_con_msg')"
                      :isVisible="isDialogVisible"
                      @update:isVisible="isDialogVisible = $event"
                      @confirmed="onDeleted"
                    />
                    </VDialog>
                  </VBtn>
                </div>
              </template>
            </VDataTable>
          </VCardItem>
        </VCard>
      </VContainer>
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
