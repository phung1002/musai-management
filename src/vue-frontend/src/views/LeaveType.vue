<!-- 休暇管理 画面 -->
<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import LeaveTypeForm from "@/components/form/LeaveTypeForm.vue";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { useI18n } from "vue-i18n";
import { ILeaveTypes } from "@/types/type";
import { deleteLeave, getLeaves, searchLeave } from "@/api/leave";
import { showSnackbar } from "@/composables/useSnackbar";

const { t } = useI18n(); // 日本語にローカル変更用
const addLeaves = ref(false); // 休暇追加・編集フォーム表示
const isDialogVisible = ref(false); // 削除確認ダイアログ表示
const errorMessage = ref(""); // エラーメッセージ管理
const selectedId = ref<ILeaveTypes>({} as ILeaveTypes);
const selectedLeave = ref<ILeaveTypes | undefined>(undefined); // 編集する休暇情報
const isEdit = ref(false); // 編集モードかどうか
const leaves = ref<ILeaveTypes[]>([]); // 休暇リスト
const isLoading = ref(false); // ローディングフラグ
const isError = ref(false); // エラーフラグ
// テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number" }, // 表示番号
  { title: t("leave_name"), key: "name" }, // 休暇名
  { title: t("action"), key: "action" }, // アクション
]);
// 休暇リストをロード
const loadLeave = (lst: any) => {
  leaves.value = lst.map((leave: ILeaveTypes) => ({
    ...leave,
  }));
};
// 休暇リスト取得　API呼び出し
const fetchLeaveType = async () => {
  isLoading.value = true;
  isError.value = false;
  try {
    const response = await getLeaves(); // API呼び出
    console.log("response", response);
    loadLeave(response); // リスト更新
  } catch (error) {
    isError.value = true;
    console.error("Error fetching leaves:", error);
  } finally {
    isLoading.value = false;
  }
};
// 新規作成
const handleCreateItem = (leaveType: ILeaveTypes) => {
  selectedLeave.value = leaveType; // 新規作成なのでリセット
  console.log("新規作成");
  console.log(isEdit.value);
  addLeaves.value = true;
  isEdit.value = false;
};
// 検索
const keyWord = ref("");
const handleSearch = async () => {
  if (keyWord.value == null) {
    fetchLeaveType();
    return;
  }
  try {
    const response = await searchLeave(keyWord.value);
    loadLeave(response);
  } catch (error) {
    isError.value = true;
  } finally {
    isLoading.value = false;
  }
};
// 編集
const handleEditItem = (leave: ILeaveTypes) => {
  selectedLeave.value = { ...leave }; // 選択データをセット
  console.log("編集対象", selectedLeave.value);
  addLeaves.value = true;
  isEdit.value = true;
};
// 削除
const handleDeleteItem = (leave: ILeaveTypes) => {
  selectedId.value = leave;
  isDialogVisible.value = true;
  console.log("delete", leave);
};
// 削除確認ダイアログのOKボタン押した際イベント
const onDeleted = async () => {
  // ここに処理を追加
  if (!selectedId.value.id) return;
  errorMessage.value = "";
  try {
    await deleteLeave(selectedId.value.id);
    console.log(`休暇ID ${selectedId.value.id} を削除しました`);
    showSnackbar("delete_success", "success");
    fetchLeaveType(); // リスト更新
  } catch (error: any) {
    if (error.status == 403) {
      showSnackbar("delete_your_self", "error");
    } else {
      showSnackbar("delete_failure", "error");
    }
  } finally {
    isDialogVisible.value = false;
  }
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
              ><VIcon icon="mdi-calendar-star-outline" />
              {{ t("leave_management") }}
            </VToolbarTitle>
            <!-- アップロード　ボタン-->
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
          <!-- 申請情報　表示 -->
          <VCardItem>
            <VDataTable
              :items-per-page-text="t('items_per_page')"
              :headers="headers"
              :items="leaves"
              v-if="!isLoading && !isError"
            >
              <!-- 表示　番号設定  -->
              <template v-slot:item.number="{ index }">
                {{ index + 1 }}
              </template>
              <!-- アクション　設定  -->
              <template v-slot:item.action="{ item }">
                <div class="action-buttons" v-if="item.parentId != null">
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="handleEditItem(item)"
                  >
                    <VIcon color="blue">mdi-pencil</VIcon>
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="handleDeleteItem(item)"
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
  <!-- 追加・修正確認 -->
  <VDialog v-model="addLeaves" width="auto" persistent>
    <LeaveTypeForm
      :isEdit="isEdit"
      :leave="selectedLeave"
      @form-cancel="addLeaves = false"
      @refetch-data="fetchLeaveType"
    />
  </VDialog>
  <!-- 削除確認 -->
  <VDialog v-model="isDialogVisible" width="auto" eager>
    <ConfimDialogView
      :title="t('confirm')"
      :message="t('delete_confirm_message')"
      :isVisible="isDialogVisible"
      @update:isVisible="isDialogVisible = $event"
      @confirmed="onDeleted"
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
