<!-- 休暇申請 画面-->
<script setup lang="ts">
import AppSidebar from "@/components/layout/AppSidebar.vue";
import AppToolbar from "@/components/layout/AppToolbar.vue";
// import LeaveRequstList from './lists/LeaveRequst.vue';
import { ref, reactive, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import LeaveRequestFormVue from "@/components/form/LeaveRequestForm.vue";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { getRequstLists } from "@/api/requst";
import { ILeaveRequest } from "@/types/type";

// 日本語にローカル変更用
const { t } = useI18n();
const showFilter = ref(true);
const isDialogVisible = ref(false);
const applyFrom = ref(false);
const editForm = ref(false);
const loading = ref(true);

const leaveRequest = ref<ILeaveRequest[]>([]);
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
  console.log("delete", id);
};
const handleEditItem = (id: number) => {
  editForm.value = true;
  console.log("edit", id);
};
const handleClear = () => {
  filters.role = "";
  filters.status = "";
};
const filters = reactive({
  role: "",
  status: "",
});
const pagination = reactive({
  page: 1,
  pageSize: 10,
});
// // テーブル　ヘッダー
const headers = reactive([
  { title: t("leave_type"), key: "leave_type" },
  { title: t("leave_duration_from"), key: "leave_duration_from" },
  { title: t("leave_duration_to"), key: "leave_duration_to" },
  { title: t("leave_reason"), key: "leave_reason" },
  { title: t("status"), key: "status" },
  { title: t("action"), key: "action" },
]);
// GET申請リスト　API
const fetchRequests = async () => {
  isLoading.value = true;
  isError.value = false;
  console.log(headers);
  try {
    const response = await getRequstLists(); //  API呼び出し
    leaveRequest.value = response.map((LeaveRequestList: ILeaveRequest) => ({
      ...LeaveRequestList,
    }));
  } catch (error) {
    isError.value = true;
  } finally {
    isLoading.value = false;
  }
};
// Call API when component is mounted
onMounted(() => {
  fetchRequests();
});
const onDeleted = () => {
  console.log("削除されました");
  // ここに処理を追加
};
</script>

<template>
  <VRow>
    <VCol cols="12">
      <VContainer class="app-container">
        <VCard flat elevation="0">
          <VToolbar tag="div">
            <VToolbarTitle>
              <VIcon>mdi-email-arrow-right-outline</VIcon>
              <span class="text-lg font-medium ml-2">{{
                t("leave_request")
              }}</span>
            </VToolbarTitle>
            <!-- 申請入力フォーム　ボタン-->
            <VCardActions>
              <VSpacer />
              <VBtn
                color="primary"
                variant="elevated"
                @click="handleCreateItem"
              >
                <VIcon>mdi-plus</VIcon>
                <span class="text-lg font-medium ml-2">{{
                  t("leave_applying")
                }}</span>
                <VDialog v-model="applyFrom" width="auto" eager>
                  <LeaveRequestFormVue @form:cancel="applyFrom = false" />
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
              :items="leaveRequest"
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
                    <VDialog v-model="editForm" width="auto" eager> </VDialog>
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
                        :title="t('confirm')"
                        :message="t('delete_confirm_message')"
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
  <!-- <LeaveRequstList /> -->
  <RouterView />
</template>

<style scoped>
.page-wrapper {
  padding: 20px;
}

.search {
  flex-grow: 1;
}

.text-error {
  color: red;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  background-color: white;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 8px;
}

.action-btn:hover {
  background-color: #f5f5f5;
}
</style>
