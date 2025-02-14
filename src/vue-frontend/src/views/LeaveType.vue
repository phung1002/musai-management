<!-- 休暇管理 画面 -->
<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import LeaveForm from "@/components/form/LeaveForm.vue";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { useI18n } from "vue-i18n";
import { ILeaveTypes } from "@/types/type";
import { deleteLeave, getLeaves } from "@/api/leave";
const { t } = useI18n();
const addLeaves = ref(false);
const isDialogVisible = ref(false);
const editLeave = ref(false);
const isDeleting = ref(false); // 削除中の状態管理
const errorMessage = ref(""); // エラーメッセージ管理
const selectedId = ref<number | null>(null); // 削除する休暇ID
// テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number" },
  // { title: t("id"), key: "id" },
  // { title: t("parent_id"), key: "parentId" },
  { title: t("name"), key: "name" },
  { title: t("action"), key: "action" },
]);
// Status
const leaves = ref<ILeaveTypes[]>([]);
const isLoading = ref(false);
const isError = ref(false);

// 休暇リスト取得　API呼び出し
const fetchLeaveType = async () => {
  isLoading.value = true;
  isError.value = false;

  try {
    const response = await getLeaves(); // API呼び出
    console.log("response", response);
    leaves.value = response.map((leave: ILeaveTypes) => ({
      ...leave,
    }));
  } catch (error) {
    isError.value = true;
    console.error("Error fetching leaves:", error);
  } finally {
    isLoading.value = false;
  }
};

const handleCreateItem = () => {
  addLeaves.value = true;
};
const handleDeleteItem = (id: number) => {
  selectedId.value = id;
  isDialogVisible.value = true;
  console.log("delete", id);
};
const handleEditItem = (id: number) => {
  editLeave.value = true;
  console.log("edit", id);
};
const onDeleted = async () => {
  // ここに処理を追加
  if (selectedId.value === null) return;

  isDeleting.value = true;
  errorMessage.value = "";
  console.log(selectedId.value);
  try {
    await deleteLeave(selectedId.value);
    console.log(`休暇ID ${selectedId.value} を削除しました`);
    alert("削除が完了しました！");
    isDialogVisible.value = false;
    selectedId.value = null;
    // ここで削除後のリスト更新処理を入れる
  } catch (error) {
    errorMessage.value = "削除に失敗しました";
  } finally {
    isDeleting.value = false;
  }

  // ここに処理を追加
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
                <VDialog v-model="addLeaves" width="auto" eager>
                  <LeaveForm @form:cancel="addLeaves = false" />
                </VDialog>
              </VBtn>
            </VCardActions>
          </VToolbar>
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
                <div class="action-buttons">
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="handleEditItem"
                  >
                    <VIcon color="blue">mdi-pencil</VIcon>
                    <VDialog v-model="editLeave" width="auto" eager> </VDialog>
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="handleDeleteItem(item.id)"
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
