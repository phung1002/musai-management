<!-- 休暇申請 画面-->
<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import LeaveRequestForm from "@/components/form/LeaveRequestForm.vue";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { listLeaveRequestForMember, cancelRequest } from "@/api/request";
import { getEmployeeLeavesForMember } from "@/api/employeeLeave";
import { ILeaveRequest } from "@/types/type";
import { toast } from "vue3-toastify";
import { ELeaveStatus } from "@/constants/leaveStatus";
import { shortenFileName } from "@/utils/stringUtils";

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
const employeeLeaves = ref([
  { leaveTypeName: "", remainedDays: 0, validTo: "" },
]);
const keyWord = ref("");

const convertDate = (date: Date | null): string | null => {
  if (date == null) return null;
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const openCreateDialog = () => {
  isEdit.value = false;
  isDialogVisible.value = true;
};
const openUpdateDialog = (application: ILeaveRequest) => {
  isEdit.value = true;
  isDialogVisible.value = true;
  selectedRequest.value = application;
};
const headersUserLeave = ref([
  { title: t("leave_type"), key: "leaveTypeName", sortable: false },
  { title: t("available_leaves"), key: "remainedDays", sortable: false },
  { title: t("leave_expired"), key: "validTo", sortable: false },
]);
// テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number", sortable: false },
  { title: t("leave_type"), key: "leaveTypeName" },
  { title: t("leave_duration_from"), key: "startDate" },
  { title: t("leave_duration_to"), key: "endDate" },
  { title: t("leave_reason"), key: "reason" },
  { title: t("status"), key: "status" },
  { title: t("action"), key: "action", sortable: false },
]);
const loadEmployeeLeave = async () => {
  try {
    let response = await getEmployeeLeavesForMember();
    employeeLeaves.value = response.map(
      ({ leaveTypeName, remainedDays, validTo }) => ({
        leaveTypeName,
        remainedDays,
        validTo,
      })
    );
  } catch (error) {}
};
// GET申請リスト　API
const fetchLeaveRequests = async (searchQuery: string = "") => {
  isLoading.value = true;
  isError.value = false;
  // 検索キーワードが空でも呼び出せる
  try {
    const response = await listLeaveRequestForMember(searchQuery); //  API呼び出し
    leaveRequests.value = response.map((leaveRequestList: ILeaveRequest) => ({
      ...leaveRequestList,
    }));
  } catch (error:any) {
    isError.value = true;
    toast.error(t(error.message));
  } finally {
    isLoading.value = false;
  }
};
const handleSearch = () => {
  if (!keyWord.value.trim()) {
    // 入力が空の場合、リストを再表示（全データを取得）
    fetchLeaveRequests();
  } else {
    // 入力がある場合は検索を実行
    fetchLeaveRequests(keyWord.value);
  }
};
const handleClear = () => {
  keyWord.value = ""; // 検索ボックスをクリア
  fetchLeaveRequests(); // 全ユーザーを再表示
};
// Call API when component is mounted
onMounted(() => {
  fetchLeaveRequests();
  loadEmployeeLeave();
});

const openConfirmCancelDialog = (leaveRequest: ILeaveRequest) => {
  selectedRequest.value = leaveRequest;
  isConfirmDialogVisible.value = true;
};
const handleCancel = async () => {
  if (!selectedRequest.value.id) return;
  try {
    await cancelRequest(selectedRequest.value.id);
    toast.success(t("message.delete_success"));
    fetchLeaveRequests();
    loadEmployeeLeave();
  } catch (error: any) {
    toast.error(t("message.cancel_only_pending"));
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
        <VCardText class="d-flex justify-end">
          <VDataTable
            :headers="headersUserLeave"
            :items="employeeLeaves"
            item-value="id"
            density="compact"
            class="small-text table-leave-of-employee"
            hide-default-footer
            :no-data-text="t('no_leave_day')"
          />
        </VCardText>

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
              <VBtn color="primary" variant="elevated" @click="openCreateDialog"
                ><v-icon icon="mdi-plus" start></v-icon>{{ t("leave_request") }}
              </VBtn>
            </VCardActions>
          </VToolbar>
          <VDivider/>
          <!-- 検索バー -->
          <VCardItem class="py-0">
            <VToolbar tag="div" color="transparent" flat>
              <VTextField
                v-model="keyWord"
                :prepend-icon="'mdi-filter-variant'"
                :placeholder="t('type_something')"
                hide-details
                clearable
                variant="plain"
                class="search"
                @keyup.enter="handleSearch"
                @click:prepend="showFilter = !showFilter"
                @click:clear="handleClear"
              />
              <VBtn icon @click="handleSearch" density="comfortable">
                <VIcon>mdi-magnify</VIcon>
              </VBtn>
            </VToolbar>
          </VCardItem>
          <!-- 申請情報　表示 -->
          <VCardItem class="pt-0">
            <VDataTable
              :headers="headers"
              :items="leaveRequests"
              :items-per-page-text="t('items_per_page')"
              :no-data-text="t('no_records_found')"
              class="data-table"
              v-if="!isLoading && !isError"
            >
              <!-- 表示　番号設定  -->
              <template v-slot:item.number="{ index }">
                {{ index + 1 }}
              </template>
              <!--   -->
              <template v-slot:item.status="{ item }">
                <VChipGroup column active-class="bg-primary text-white">
                  <VChip
                    :style="{ color: getStatusColor(item.status) }"
                    text-color="white"
                  >
                    {{ t(`application_status.${item.status}`) }}
                  </VChip>
                </VChipGroup>
              </template>
              <template v-slot:item.leaveTypeName="{ item }">
                <td>{{ shortenFileName(item.leaveTypeName) }}</td>
              </template>

              <!-- アクション　設定  -->
              <template v-slot:item.action="{ item }">
                <div class="action-buttons">
                  <VBtn
                    :disabled="item.status != 'PENDING'"
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="openUpdateDialog(item)"
                  >
                    <VIcon color="blue">mdi-pencil</VIcon>
                  </VBtn>
                  <VBtn
                    :disabled="item.status != 'PENDING'"
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="openConfirmCancelDialog(item)"
                  >
                    <VIcon color="red">mdi-close-thick</VIcon>
                  </VBtn>
                </div>
              </template>
            </VDataTable>
          </VCardItem>
        </VCard>
      </VContainer>
    </VCol>
  </VRow>
  <VDialog v-model="isDialogVisible" width="auto" persistent>
    <LeaveRequestForm
      :isEdit="isEdit"
      :application="selectedRequest"
      @form:cancel="isDialogVisible = false"
      @refetch-data="fetchLeaveRequests"
      @refetch-employeeleave="loadEmployeeLeave"
    />
  </VDialog>
  <VDialog v-model="isConfirmDialogVisible" width="auto" persistent>
    <ConfimDialogView
      :title="t('confirm')"
      :message="t('cancel_confirm_message')"
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

::v-deep(thead) {
  background-color: rgba(0, 86, 247, 0.2) !important;
}
::v-deep(.data-table table) {
  min-width: 808px !important;
}
::v-deep(.table-leave-of-employee table *){
  padding-right: 0px !important;
}
::v-deep(.table-leave-of-employee table) {
  min-width: 251px !important;
}
</style>
