<!-- 休暇申請 画面-->
<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import LeaveRequestForm from "@/components/form/LeaveRequestForm.vue";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { listLeaveRequestForMember, cancelRequest } from "@/api/request";
import { ILeaveRequest } from "@/types/type";
import { toast } from "vue3-toastify";
import { ELeaveStatus } from "@/constants/leaveStatus";

// 日本語にローカル変更用
const { t } = useI18n();
const showFilter = ref(true);
const isEdit = ref(false);
const isDialogVisible = ref(false);
const selectedRequest = ref<ILeaveRequest>({} as ILeaveRequest);
const leaveRequests = ref<ILeaveRequest[]>([]);
const isLoading = ref(false);
const isError = ref(false);
const isConfirmDialogVisible = ref(false);

const convertDate = (date: Date | null): string | null => {
  if (date == null) return null;
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const handleSearch = async () => {};
const openCreateDialog = () => {
  isEdit.value = false;
  isDialogVisible.value = true;
};
const openUpdateDialog = (application: ILeaveRequest) => {
  isEdit.value = true;
  isDialogVisible.value = true;
  selectedRequest.value = application;
};

// テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number" },
  { title: t("leave_type"), key: "leaveTypeName" },
  { title: t("leave_duration_from"), key: "startDate" },
  { title: t("leave_duration_to"), key: "endDate" },
  { title: t("leave_reason"), key: "reason" },
  { title: t("status"), key: "status" },
  { title: t("action"), key: "action" },
]);
// GET申請リスト　API
const fetchLeaveRequests = async () => {
  isLoading.value = true;
  isError.value = false;
  try {
    const response = await listLeaveRequestForMember(); //  API呼び出し
    leaveRequests.value = response.map((leaveRequestList: ILeaveRequest) => ({
      ...leaveRequestList,
    }));
  } catch (error) {
    isError.value = true;
  } finally {
    isLoading.value = false;
  }
};
// Call API when component is mounted
onMounted(() => {
  fetchLeaveRequests();
});

const openConfirmCancelDialog = (leaveRequest: ILeaveRequest) => {
  selectedRequest.value = leaveRequest;
  isConfirmDialogVisible.value = true;
};
const handleCancel = async () => {
  if (!selectedRequest.value.id) return;
  try {
    await cancelRequest(selectedRequest.value.id);

    toast.success(t("delete_success"));
    fetchLeaveRequests();
  } catch (error: any) {
    toast.error(t("cancel_only_pending"));
  } finally {
    isConfirmDialogVisible.value = false;
  }
};
const getStatusColor = (status: string) => {
  switch (status) {
    case ELeaveStatus.APPROVED:
      return "green";
    case ELeaveStatus.REJECTED:
      return "orange";
    case ELeaveStatus.PENDING:
      return "blue";
    case ELeaveStatus.REVOKED:
      return "red";
    default:
      return "grey";
  }
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
                @click="openCreateDialog"
              >
                <VIcon>mdi-plus</VIcon>
                <span class="text-lg font-medium ml-2">{{
                  t("leave_request")
                }}</span>
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
                @keyup.enter="handleSearch"
                @click:prepend="showFilter = !showFilter"
                @click:clear="handleSearch"
              />
              <VBtn icon @click="handleSearch" density="comfortable">
                <VIcon>mdi-magnify</VIcon>
              </VBtn>
            </VToolbar>
          </VCardItem>
          <VDivider />
          <!-- 申請情報　表示 -->
          <VCardItem>
            <VDataTable
              :headers="headers"
              :items="leaveRequests"
              :items-per-page-text="t('items_per_page')"
              v-if="!isLoading && !isError"
            >
              <!-- 表示　番号設定  -->
              <template v-slot:item.number="{ index }">
                {{ index + 1 }}
              </template>
              <!--   -->
              <template v-slot:item.status="{ item }">
                <span
                  :style="{ color: getStatusColor(item.status) }"
                  class="status-text"
                >
                  {{ t(`application_status.${item.status}`) }}
                </span>
              </template>

              <!--   -->
              <template v-slot:item.startDate="{ item }">
                {{ convertDate(item.startDate) }}
              </template>

              <!--   -->
              <template v-slot:item.endDate="{ item }">
                {{ convertDate(item.endDate) }}
              </template>

              <!-- アクション　設定  -->
              <template v-slot:item.action="{ item }">
                <div class="action-buttons">
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="openUpdateDialog(item)"
                  >
                    <VIcon color="blue">mdi-pencil</VIcon>
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="openConfirmCancelDialog(item)"
                  >
                    <VIcon color="red">mdi-delete</VIcon>
                  </VBtn>
                </div>
              </template>
            </VDataTable>
          </VCardItem>
        </VCard>
      </VContainer>
    </VCol>
  </VRow>
  <VDialog v-model="isDialogVisible" width="auto">
    <LeaveRequestForm
      :isEdit="isEdit"
      :application="selectedRequest"
      @form:cancel="isDialogVisible = false"
      @refetch-data="fetchLeaveRequests"
    />
  </VDialog>
  <VDialog v-model="isConfirmDialogVisible" width="auto">
    <ConfimDialogView
      :title="t('confirm')"
      :message="t('delete_confirm_message')"
      :isVisible="isConfirmDialogVisible"
      @update:isVisible="isConfirmDialogVisible = $event"
      @confirmed="handleCancel"
    />
  </VDialog>
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
