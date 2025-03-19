<!-- 申請確認 画面　-->
<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { ILeaveResponse } from "@/types/type";
import { toast } from "vue3-toastify";
import {
  getLeaveRequests,
  updateLeaveRespond,
} from "@/api/response";
import { format } from "date-fns"; // 日付フォーマットライブラリ
import LeaveResponseDetails from "@/components/ui/LeaveResponseDetails.vue";
const { t } = useI18n();
const keyWord = ref("");
const LeaveRequests = ref<ILeaveResponse[]>([]); // 休暇リスト
const selectedResponse = ref<ILeaveResponse>({} as ILeaveResponse);
const detailsCard = ref(false);
const isDetails = ref(false);
const isLoading = ref(false); // ローディングフラグ
const isError = ref(false); // エラーフラグ
// // テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number" },
  { title: t("employee_name"), key: "userFullName" },
  { title: t("leave_type"), key: "leaveTypeName" },
  { title: t("submit_date"), key: "createdAt" },
  { title: t("status"), key: "status" },
  { title: t("action"), key: "action" },
]);
// 申請リストをロード
const loadLeave = (lst: any) => {
  LeaveRequests.value = lst.map((LeaveRequest: ILeaveResponse) => ({
    ...LeaveRequest,
    createdAt: LeaveRequest.createdAt
      ? format(new Date(LeaveRequest.createdAt), "yyyy-MM-dd")
      : "",
  }));
};
// 申請リスト取得 API呼び出し
const fetchLeaveType = async () => {
  isLoading.value = true;
  isError.value = false;
  try {
    const response = await getLeaveRequests(keyWord.value); // API呼び出
    loadLeave(response); // リスト更新
  } catch (error) {
    isError.value = true;
    console.error("Error fetching leaves:", error);
  } finally {
    isLoading.value = false;
  }
};
// 許可処理
const approve = async (id: number) => {
  try {
    await updateLeaveRespond(id, "APPROVED");
    toast.success(t("message.approved_success"));
    await fetchLeaveType(); // 最新データを取得
  } catch (error) {
    console.error("Error approving request:", error);
  }
};
// 拒否処理
const reject = async (id: number) => {
  try {
    await updateLeaveRespond(id, "REJECTED");
    toast.success(t("message.rejected_success"));
    await fetchLeaveType(); // 最新データを取得
  } catch (error) {
    console.error("Error rejecting request:", error);
  }
};
const getStatusColor = (status: string) => {
  switch (status) {
    case "APPROVED":
      return "green";
    case "REJECTED":
      return "orange";
    case "PENDING":
      return "blue";
    case "REVOKED":
      return "red";
    default:
      return "grey";
  }
};
const details = (LeaveResponse: ILeaveResponse) => {
  detailsCard.value = true;
  isDetails.value = true;
  selectedResponse.value = LeaveResponse;
  console.log(LeaveResponse);
};
// コンポーネントがマウントされたときAPI呼び出し修理実行
onMounted(() => {
  fetchLeaveType();
  console.log("ok");
});
</script>
<template>
  <VRow>
    <VCol cols="12">
      <VContainer class="app-container">
        <VCard flat elevation="0">
          <VToolbar tag="div">
            <VToolbarTitle
              ><VIcon icon="mdi-message-check-outline" />
              {{ t("request_confirm") }}
            </VToolbarTitle>
          </VToolbar>
          <VDivider />
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
                @click:clear="fetchLeaveType"
                @keydown.enter="fetchLeaveType"
              />
              <VBtn icon density="comfortable" @click="fetchLeaveType">
                <VIcon>mdi-magnify</VIcon>
              </VBtn>
            </VToolbar>
          </VCardItem>
          <VDivider />
          <!-- 申請情報　表示 -->
          <VCardItem>
            <VDataTable
              :items-per-page-text="t('items_per_page')"
              :headers="headers"
              :items="LeaveRequests"
              v-if="!isLoading && !isError"
            >
              <!-- 表示　番号設定  -->
              <template v-slot:item.number="{ index }">
                {{ index + 1 }}
              </template>
              <template v-slot:item.status="{ item }">
                <span
                  :style="{ color: getStatusColor(item.status) }"
                  class="status-text"
                >
                  {{ t(`application_status.${item.status}`) }}
                </span>
              </template>
              <!-- アクション　設定  -->
              <template v-slot:item.action="{ item }">
                <div class="action-buttons">
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="details(item)"
                  >
                    <VIcon color="black">mdi-information-outline</VIcon>
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    :disabled="item.status != 'PENDING'"
                    @click="approve(item.id)"
                  >
                    <VIcon :color="getStatusColor(item.status)"
                      >mdi-check-bold</VIcon
                    >
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    :disabled="item.status != 'PENDING'"
                    @click="reject(item.id)"
                  >
                    <VIcon :color="getStatusColor(item.status)"
                      >mdi-close-thick</VIcon
                    >
                  </VBtn>
                </div>
              </template>
            </VDataTable>
          </VCardItem>
        </VCard>
      </VContainer>
    </VCol>
  </VRow>
  <VDialog v-model="detailsCard" width="auto">
    <LeaveResponseDetails
      @form:cancel="detailsCard = false"
      @fetch="fetchLeaveType"
      :isDetails="isDetails"
      :LeaveResponse="selectedResponse"
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
  background-color: #ebf5f8;
}
</style>
