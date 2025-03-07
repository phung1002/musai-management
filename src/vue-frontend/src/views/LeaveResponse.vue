<!-- 申請確認 画面　-->
<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { ILeaveApplications } from "@/types/type";
import {
  getLeaveApplicationsLists,
  searchLeaveApplications,
} from "@/api/response";
import { format } from "date-fns"; // 日付フォーマットライブラリ
const { t } = useI18n();
const LeaveApplications = ref<ILeaveApplications[]>([]); // 休暇リスト
const isDialogVisible = ref(false);
const detailsCard = ref(false);
const isLoading = ref(false); // ローディングフラグ
const isError = ref(false); // エラーフラグ
// 申請の状態を管理 (null: 未決定, 'approved': 許可, 'rejected': 拒否)
const decision = ref<null | "approved" | "rejected">(null);
// // テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number" },
  { title: t("employee_name"), key: "userFullName" },
  { title: t("leave_type"), key: "leaveTypeName" },
  { title: t("submit_date"), key: "createdAt" },
  { title: t("action"), key: "action" },
]);
// 申請リストをロード
const loadLeave = (lst: any) => {
  LeaveApplications.value = lst.map((LeaveApplication: ILeaveApplications) => ({
    ...LeaveApplication,
    createdAt: LeaveApplication.createdAt
      ? format(new Date(LeaveApplication.createdAt), "yyyy-MM-dd")
      : "",
  }));
};
// 申請リスト取得 API呼び出し
const fetchLeaveType = async () => {
  isLoading.value = true;
  isError.value = false;
  try {
    const response = await getLeaveApplicationsLists(); // API呼び出
    console.log("response ss", response);
    loadLeave(response); // リスト更新
  } catch (error) {
    isError.value = true;
    console.error("Error fetching leaves:", error);
  } finally {
    isLoading.value = false;
  }
};
// 検索
const keyWord = ref("");
const handleSearch = async () => {
  if (keyWord.value == null) {
    fetchLeaveType();
    return;
  }
  try {
    const response = await searchLeaveApplications(keyWord.value);
    loadLeave(response);
  } catch (error) {
    isError.value = true;
  } finally {
    isLoading.value = false;
  }
};
// 許可処理
const approve = (id: number) => {
  decision.value = "approved";
  isDialogVisible.value = true;
  console.log("accept", id);
};
// 拒否処理
const reject = (id: number) => {
  decision.value = "rejected";
  console.log("reject", id);
};
const details = (id: number) => {
  detailsCard.value = true;
  console.log("reject", id);
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
                @click:clear="handleSearch"
                @keydown.enter="handleSearch"
              />
              <VBtn icon density="comfortable" @click="handleSearch">
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
              :items="LeaveApplications"
              v-if="!isLoading && !isError"
            >
              <!-- 表示　番号設定  -->
              <template v-slot:item.number="{ index }">
                {{ index + 1 }}
              </template>
              <!-- アクション　設定  -->
              <template v-slot:item.action="{ item }">
                <div class="action-buttons">
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="details"
                  >
                    <VIcon color="black">mdi-information-outline</VIcon>
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    :disabled="decision !== null"
                    @click="approve"
                  >
                    <VIcon
                      :color="
                        decision === 'approved'
                          ? 'green'
                          : decision === 'rejected'
                          ? 'red'
                          : 'primary'
                      "
                      >mdi-check-bold</VIcon
                    >
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    :disabled="decision !== null"
                    @click="reject"
                  >
                    <VIcon :color="decision === 'rejected' ? 'red' : 'error'"
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
