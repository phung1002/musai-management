<!-- 社員休暇管理 画面　-->
<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import EmployeeLeaveForm from "@/components/form/EmployeeLeaveForm.vue";
import { useI18n } from "vue-i18n";
import { VTab } from "vuetify/lib/components/index.mjs";
import { IEmployeeLeaves } from "@/types/type";
import { getEmployeeLeaves } from "@/api/employeeLeave";
import { shortenFileName } from "@/utils/stringUtils";
import { toast } from "vue3-toastify";
const { t } = useI18n();
const keyWord = ref("");
const addFrom = ref(false); // 追加プラグ
const editForm = ref(false); //編集プラグ
const activeTab = ref("有休"); // タブの初期値
const EmployeeLeaves = ref<IEmployeeLeaves[]>([]); // 休暇リスト
const isLoading = ref(false); // ローディングフラグ
const isError = ref(false); // エラーフラグ
const selectedLeave = ref<IEmployeeLeaves | undefined>(undefined); // 編集する休暇情報
const isEdit = ref(false); // 編集モードかどうか
const selectedTab = ref("paid_leave");
// メイン休暇タブで分類
const tabs = ref([
  { title: "paid_leave", icon: "mdi-gift-open", tab: "有休" },
  { title: "public_leave", icon: "mdi-pine-tree-box", tab: "夏休み" },
]);
// // テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number" },
  { title: t("employee_name"), key: "employeeFullName" },
  { title: t("valid_leaves"), key: "totalDays" },
  { title: t("used_leaves"), key: "usedDays" },
  { title: t("available_leaves"), key: "remainedDays" },
  { title: t("leave_start"), key: "validFrom" },
  { title: t("leave_expired"), key: "validTo" },
  { title: t("action"), key: "action" },
]);
// データを分類する計算プロパティ
const filteredLeaves = computed(() => {
  return EmployeeLeaves.value.filter(
    (leave) =>
      (selectedTab.value === "paid_leave" &&
        leave.leaveTypeValue == "PAID_LEAVE") ||
      (selectedTab.value === "public_leave" &&
        leave.leaveTypeValue == "SUMMER_DAY")
  );
});
// 休暇リストをロード
const loadLeave = (lst: any) => {
  EmployeeLeaves.value = lst.map((EmployeeLeave: IEmployeeLeaves) => ({
    ...EmployeeLeave,
  }));
};
// ユーザー休暇リスト取得 API呼び出し
const fetchLeaveType = async (searchQuery: string = "") => {
  isLoading.value = true;
  isError.value = false;
  try {
    const response = await getEmployeeLeaves(searchQuery); // API呼び出
    loadLeave(response); // リスト更新
  } catch (error: any) {
    isError.value = true;
    toast.error(t(error.message));
  } finally {
    isLoading.value = false;
  }
};
const handleSearch = () => {
  if (!keyWord.value.trim()) {
    // 入力が空の場合、リストを再表示（全データを取得）
    fetchLeaveType();
  } else {
    // 入力がある場合は検索を実行
    fetchLeaveType(keyWord.value);
  }
};
const handleClear = () => {
  keyWord.value = ""; // 検索ボックスをクリア
  fetchLeaveType(); // 全ユーザーを再表示
};
// 追加用ダイアログ表示
const handleCreateItem = (leaveType: IEmployeeLeaves) => {
  selectedLeave.value = leaveType; // 新規作成なのでリセット
  addFrom.value = true;
  isEdit.value = false;
};
// 編集用ダイアログ表示
const handleEditItem = (EmployeeLeave: IEmployeeLeaves) => {
  selectedLeave.value = { ...EmployeeLeave }; // 選択データをセット
  addFrom.value = true;
  isEdit.value = true;
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
              ><VIcon icon="mdi-badge-account-horizontal-outline" />
              {{ t("employee_leave_management") }}
            </VToolbarTitle>
            <!-- 追加　ボタン-->
            <VCardActions>
              <VSpacer />
              <VBtn color="primary" @click="handleCreateItem" variant="elevated"
                ><v-icon icon="mdi-plus" start></v-icon>{{ t("register") }}
              </VBtn>
            </VCardActions>
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
                @click:clear="handleClear"
                @keyup.enter="handleSearch"
              />
              <VBtn icon density="comfortable" @click="handleSearch">
                <VIcon>mdi-magnify</VIcon>
              </VBtn>
            </VToolbar>
          </VCardItem>
          <VDivider />
          <!-- 休暇タイプタブ設定 -->
          <VTable>
            <VCardItem>
              <VTabs v-model="selectedTab" color="primary">
                <VTab v-for="tab in tabs" :key="tab.title" :value="tab.title">
                  <!-- <VIcon size="20" start :icon="item.icon" /> -->
                  <v-icon>{{ tab.icon }}</v-icon>
                  {{ tab.tab }}
                </VTab>
              </VTabs>
              <VWindow v-model="activeTab" >
                <VWindowItem value="paid"></VWindowItem>
                <VWindowItem value="public"></VWindowItem>
                <VDataTable
                  :items-per-page-text="t('items_per_page')"
                  :headers="headers"
                  :items="filteredLeaves"
                  v-if="!isLoading && !isError"
                >
                  <!-- 表示　番号設定  -->
                  <template v-slot:item.number="{ index }">
                    {{ index + 1 }}
                  </template>
                  <template v-slot:item.employeeFullName="{ item }">
                    <td>{{ shortenFileName(item.employeeFullName) }}</td>
                  </template>
                  <!-- アクション　設定  -->
                  <template v-slot:item.action="{ item }">
                    <div class="action-buttons">
                      <VBtn
                        icon
                        variant="plain"
                        class="action-btn"
                        @click="handleEditItem(item)"
                      >
                        <VIcon color="blue">mdi-pencil</VIcon>
                        <VDialog
                          v-model="editForm"
                          width="auto"
                          eager
                          persistent
                        >
                        </VDialog>
                      </VBtn>
                    </div>
                  </template>
                </VDataTable>
              </VWindow>
            </VCardItem>
          </VTable>
        </VCard>
      </VContainer>
    </VCol>
  </VRow>
  <!-- 追加・修正確認 -->
  <VDialog v-model="addFrom" width="auto" persistent>
    <EmployeeLeaveForm
      :isEdit="isEdit"
      :employeeLeave="selectedLeave"
      @form-cancel="addFrom = false"
      @refetch-data="fetchLeaveType"
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
</style>
