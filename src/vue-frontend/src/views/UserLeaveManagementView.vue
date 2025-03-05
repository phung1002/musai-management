<!-- 書類提出 画面　-->
<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import UploadForm from "@/components/form/UploadForm.vue";
import { useI18n } from "vue-i18n";
import { VTab } from "vuetify/lib/components/index.mjs";
import { IUserLeaves } from "@/types/type";
import { getUserLeaves } from "@/api/leave";
const { t } = useI18n();
const addFrom = ref(false); // 追加プラグ
const isDialogVisible = ref(false); //確認ダイアログ
const editForm = ref(false); //編集プラグ
const activeTab = ref("paid"); // タブの初期値
const userLeaves = ref<IUserLeaves[]>([]); // 休暇リスト
const selectedId = ref<IUserLeaves>({} as IUserLeaves);
const isLoading = ref(false); // ローディングフラグ
const isError = ref(false); // エラーフラグ

// メイン休暇タブで分類
const tabs = ref([
  { title: "paid_leave", icon: "mdi-gift-open", tab: "paid" },
  { title: "public_leave", icon: "mdi-pine-tree-box", tab: "public" },
]);
// // テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number" },
  { title: t("employee_name"), key: "employee_name" },
  { title: t("valid_leaves"), key: "valid_leaves" },
  { title: t("available_leaves"), key: "available_leaves" },
  { title: t("used_leaves"), key: "used_leaves" },
  { title: t("expired"), key: "expired" },
  { title: t("action"), key: "action" },
]);
// 休暇リストをロード
const loadLeave = (lst: any) => {
  userLeaves.value = lst.map((userLeaves: IUserLeaves) => ({
    ...userLeaves,
  }));
};
// ユーザー休暇リスト取得　API呼び出し
const fetchLeaveType = async () => {
  isLoading.value = true;
  isError.value = false;
  try {
    const response = await getUserLeaves(); // API呼び出
    console.log("response", response);
    loadLeave(response); // リスト更新
  } catch (error) {
    isError.value = true;
    console.error("Error fetching leaves:", error);
  } finally {
    isLoading.value = false;
  }
};
// 追加用ダイアログ表示
const handleCreateItem = () => {
  addFrom.value = true;
};
// 削除確認ダイアログ表示
const handleDeleteItem = (id: number) => {
  isDialogVisible.value = true;
  console.log("delete", id);
};
// 編集用ダイアログ表示
const handleEditItem = (id: number) => {
  editForm.value = true;
  console.log("edit", id);
};
// 削除確認ダイアログでOKがクリックされたとき
const onDeleted = () => {
  console.log("削除されました");
  // ここに処理を追加
};
// コンポーネントがマウントされたときAPI呼び出し修理実行
onMounted(() => {
  fetchLeaveType();
  console.log("ok");
});
const pagination = reactive({
  page: 1,
  pageSize: 10,
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
                <VDialog v-model="addFrom" width="auto" eager>
                  <UploadForm @form:cancel="addFrom = false" />
                </VDialog>
              </VBtn>
            </VCardActions>
          </VToolbar>
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
              </VWindow>
            </VCardItem>
          </VTable>
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
  background-color: #f5f5f5;
}
</style>
