<!-- 申請確認 画面　-->
<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { ILeaveResponse } from "@/types/type";
import { toast } from "vue3-toastify";
import { allLeaveRequests, updateLeaveRespond } from "@/api/response";
import { format } from "date-fns"; // 日付フォーマットライブラリ
import { ELeaveStatus } from "@/constants/leaveStatus";
import LeaveResponseDetails from "@/components/ui/LeaveResponseDetails.vue";
import { shortenFileName } from "@/utils/stringUtils";
const { t } = useI18n();
const keyWord = ref("");
const LeaveRequests = ref<ILeaveResponse[]>([]); // 休暇リスト
const selectedResponse = ref<ILeaveResponse>({} as ILeaveResponse);
const detailsCard = ref(false);
const isLoading = ref(false); // ローディングフラグ
const isError = ref(false); // エラーフラグ
// // テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number", sortable: false },
  { title: t("employee_name"), key: "employeeFullName" },
  { title: t("leave_type"), key: "leaveTypeName" },
  { title: t("submit_date"), key: "createdAt" },
  { title: t("status"), key: "status" },
  { title: t("action"), key: "action", sortable: false },
]);
// 申請リストをロード
const loadLeave = (lst: any) => {
  LeaveRequests.value = lst.map((leaveResponse: ILeaveResponse) => ({
    ...leaveResponse,
    createdAt: leaveResponse.createdAt
      ? format(new Date(leaveResponse.createdAt), "yyyy-MM-dd")
      : "",
  }));
};
// 申請リスト取得 API呼び出し
const fetchLeaveType = async (searchQuery: string = "") => {
  isLoading.value = true;
  isError.value = false;
  try {
    // 検索キーワードが空でも呼び出せる
    const response = await allLeaveRequests(searchQuery); // API呼び出
    loadLeave(response); // リスト更新
  } catch (error:any) {
    isError.value = true;
    toast.error(t(error.message));
  } finally {
    isLoading.value = false;
  }
};
const handleSearch = () => {
  if (!keyWord.value.trim()) {
    // キーワードがある場合は検索実行
    fetchLeaveType(); // 入力がある場合はAPIでリストを取得
  } else {
    // キーワードが空の場合、リストを再表示
    fetchLeaveType(keyWord.value);
  }
};
const handleClear = () => {
  keyWord.value = ""; // 検索ボックスをクリア
  fetchLeaveType(); // 全ユーザーを再表示
};
// 許可処理
const approve = async (id: number) => {
  try {
    await updateLeaveRespond(id, "APPROVED");
    toast.success(t("message.approved_success"));
    await fetchLeaveType(); // 最新データを取得
  } catch (error: any) {
    toast.error(t(error.message));
    console.error("Error approving request:", error);
  }
};
// 拒否処理
const reject = async (id: number) => {
  try {
    await updateLeaveRespond(id, "REJECTED");
    toast.success(t("message.rejected_success"));
    await fetchLeaveType(); // 最新データを取得
  } catch (error: any) {
    toast.error(t(error.message));
    console.error("Error rejecting request:", error);
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

const details = (LeaveResponse: ILeaveResponse) => {
  detailsCard.value = true;
  selectedResponse.value = LeaveResponse;
};
// コンポーネントがマウントされたときAPI呼び出し修理実行
onMounted(() => {
  fetchLeaveType();
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
                @click:clear="handleClear"
                @keydown.enter="handleSearch"
              />
              <VBtn icon density="comfortable" @click="handleSearch">
                <VIcon>mdi-magnify</VIcon>
              </VBtn>
            </VToolbar>
          </VCardItem>
          <!-- 申請情報　表示 -->
          <VCardItem class="pt-0">
            <VDataTable
              :items-per-page-text="t('items_per_page')"
              :headers="headers"
              :items="LeaveRequests"
              class="data-table"
              v-if="!isLoading && !isError"
            >
              <!-- 表示　番号設定  -->
              <template v-slot:item.number="{ index }">
                {{ index + 1 }}
              </template>
              <template v-slot:item.employeeFullName="{ item }">
                    <td>{{ shortenFileName(item.employeeFullName) }}</td>
                  </template>
                  <template v-slot:item.leaveTypeName="{ item }">
                    <td>{{ shortenFileName(item.leaveTypeName) }}</td>
                  </template>
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
                    <VIcon color="blue"> mdi-check-bold </VIcon>
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    :disabled="item.status != 'PENDING'"
                    @click="reject(item.id)"
                  >
                    <VIcon color="red"> mdi-close-thick </VIcon>
                  </VBtn>
                </div>
              </template>
            </VDataTable>
          </VCardItem>
        </VCard>
      </VContainer>
    </VCol>
  </VRow>
  <VDialog v-model="detailsCard" width="auto" persistent>
    <LeaveResponseDetails
      @form:cancel="detailsCard = false"
      @fetch="fetchLeaveType"
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

::v-deep(thead) {
  background-color: rgba(0, 86, 247, 0.2) !important;
}
::v-deep(.data-table table) {
  min-width: 853px !important;
}
</style>
