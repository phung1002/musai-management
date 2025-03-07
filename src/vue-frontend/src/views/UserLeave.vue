<!-- 社員休暇管理 画面　-->
<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import UserLeaveForm from "@/components/user/UserLeaveForm.vue";
import { useI18n } from "vue-i18n";
import { VTab } from "vuetify/lib/components/index.mjs";
import { IUserLeaves } from "@/types/type";
import { getUserLeaves, searchUserLeave } from "@/api/leave";
const { t } = useI18n();
const addFrom = ref(false); // 追加プラグ
const editForm = ref(false); //編集プラグ
const activeTab = ref(" 有休"); // タブの初期値
const userLeaves = ref<IUserLeaves[]>([]); // 休暇リスト
const isLoading = ref(false); // ローディングフラグ
const isError = ref(false); // エラーフラグ
const selectedLeave = ref<IUserLeaves | undefined>(undefined); // 編集する休暇情報
const isEdit = ref(false); // 編集モードかどうか
// メイン休暇タブで分類
const tabs = ref([
  { title: "paid_leave", icon: "mdi-gift-open", tab: "有休" },
  { title: "public_leave", icon: "mdi-pine-tree-box", tab: "公休" },
]);
// // テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number" },
  { title: t("employee_name"), key: "userName" },
  { title: t("valid_leaves"), key: "totalDays" },
  { title: t("used_leaves"), key: "usedDays" },
  { title: t("available_leaves"), key: "remained_day" },
  { title: t("leave_start"), key: "validFrom" },
  { title: t("leave_expired"), key: "validTo" },
  { title: t("action"), key: "action" },
]);
// 休暇リストをロード
const loadLeave = (lst: any) => {
  userLeaves.value = lst.map((userLeave: IUserLeaves) => ({
    ...userLeave,
  }));
};
// ユーザー休暇リスト取得 API呼び出し
const fetchLeaveType = async () => {
  isLoading.value = true;
  isError.value = false;
  try {
    const response = await getUserLeaves(); // API呼び出
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
    const response = await searchUserLeave(keyWord.value);
    loadLeave(response);
  } catch (error) {
    isError.value = true;
  } finally {
    isLoading.value = false;
  }
};
// 追加用ダイアログ表示
const handleCreateItem = (leaveType: IUserLeaves) => {
  selectedLeave.value = leaveType; // 新規作成なのでリセット
  console.log("新規作成");
  console.log(isEdit.value);
  addFrom.value = true;
  isEdit.value = false;
};
// 編集用ダイアログ表示
const handleEditItem = (userLeave: IUserLeaves) => {
  selectedLeave.value = { ...userLeave }; // 選択データをセット
  console.log("編集対象", selectedLeave.value);
  console.log(isEdit.value);
  addFrom.value = true;
  isEdit.value = true;
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
              ><VIcon icon="mdi-badge-account-horizontal-outline" />
              {{ t("user_leave_management") }}
            </VToolbarTitle>
            <!-- 追加　ボタン-->
            <VCardActions>
              <VSpacer />
              <VBtn color="primary" @click="handleCreateItem" variant="elevated"
                ><v-icon icon="mdi-plus" start></v-icon>{{ t("add") }}
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
                @click:clear="handleSearch"
                @keydown.enter="handleSearch"
              />
              <VBtn icon density="comfortable" @click="handleSearch">
                <VIcon>mdi-magnify</VIcon>
              </VBtn>
            </VToolbar>
          </VCardItem>
          <VDivider />
          <!-- 休暇タイプタブ設定 -->
          <VTable>
            <VTabs v-model="activeTab" color="primary">
              <VTab v-for="item in tabs" :key="item.icon" :value="item.tab">
                <VIcon size="20" start :icon="item.icon" />
                {{ t(item.title) }}
              </VTab>
            </VTabs>
            <VCardItem>
              <VWindow v-model="activeTab">
                <VWindowItem value="paid"></VWindowItem>
                <VWindowItem value="public"></VWindowItem>
                <VDataTable
                  :items-per-page-text="t('items_per_page')"
                  :headers="headers"
                  :items="userLeaves"
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
                        @click="handleEditItem(item)"
                      >
                        <VIcon color="blue">mdi-pencil</VIcon>
                        <VDialog v-model="editForm" width="auto" eager>
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
    <UserLeaveForm
      :isEdit="isEdit"
      :userLeave="selectedLeave"
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
</style>
