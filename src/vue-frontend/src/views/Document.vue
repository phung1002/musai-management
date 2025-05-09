<!-- 書類提出 画面　-->
<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import UploadForm from "@/components/form/UploadForm.vue"; // フォームコンポーネント
import ConfimDialogView from "@/components/common/ConfimDialog.vue"; // 確認ダイアログ
import PdfPreview from "@/components/ui/PdfPreview.vue"; // PDFプレビュー
import { useEmployeeStore } from "@/store/employeeStore";
import { ERole } from "@/constants/role";
import {
  getDocuments,
  deleteDocument,
  getDocumentPreview,
  getDocumentsOfMember,
} from "@/api/document"; // API関数
import { IDocument } from "@/types/type"; // 型定義
import { useI18n } from "vue-i18n";
import { toast } from "vue3-toastify"; // トースト通知
import { shortenFileName } from "@/utils/stringUtils";
const { t } = useI18n();
const employeeStore = useEmployeeStore(); // ユーザーストア
const applyFrom = ref(false); // フォーム表示
const isDialogVisible = ref(false); // 確認ダイアログ表示
const documents = ref<IDocument[]>([]); // 動的データ
const selectedDocumentId = ref<number | null>(null); // 削除用
const pdfTitle = ref<string>(""); // pdfTitle を ref として宣言
const isPreviewDialogVisible = ref(false); // プレビューダイアログ表示制御
const pdfUrl = ref<string>(""); // PDFファイルURL
const isLoading = ref(false); // ローディング
const isError = ref(false); // エラープラグ
const employeeRoles = computed(() => employeeStore.roles || []);
const isEmployee = computed(() => employeeStore.id); // ユーザーかどうかの判定
// // テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number", sortable: false },
  { title: t("title"), key: "title" },
  { title: t("submitter"), key: "uploadBy" },
  { title: t("submit_date"), key: "uploadAt" },
  { title: t("action"), key: "action", sortable: false },
]);
// データ取得
const fetchDocuments = async () => {
  isLoading.value = true;
  isError.value = false;

  try {
    let response;
    // ユーザーのロールに基づいて API 呼び出しを変更
    if (
      employeeRoles.value.includes(ERole.MANAGER) ||
      employeeRoles.value.includes(ERole.ADMIN)
    ) {
      // 管理者 または 担当者の場合は全ての書類を取得
      response = await getDocuments();
    } else {
      // userの場合は自分の書類のみ取得
      response = await getDocumentsOfMember();
    }
    // 取得したデータをdocumentsにセット
    documents.value = response;

    // 日付を "yyyy-MM-dd" 形式に変換
    documents.value = documents.value.map((doc: any) => {
      if (doc.uploadAt) {
        const date = new Date(doc.uploadAt);
        doc.uploadAt = date.toISOString().split("T")[0]; // "yyyy-MM-dd"形式に変換
      }
      return doc;
    });
  } catch (error:any) {
    isError.value = true;
    toast.error(t(error.message));
  } finally {
    isLoading.value = false;
  }
};

const handleCreateItem = () => {
  applyFrom.value = true;
};
const handleDeleteItem = (id: number) => {
  selectedDocumentId.value = id;
  isDialogVisible.value = true;
};
// プレビュー関数
const handlePreview = async (item: { id: number; title: string }) => {
  try {
    const response = await getDocumentPreview(item.id);
    // 取得したFileオブジェクトを使ってBlob URLを生成
    if (response.file) {
      pdfUrl.value = URL.createObjectURL(response.file); // Blob URLに変換して設定
    }
    // 必要に応じて name を表示や処理で使用する
    pdfTitle.value = item.title; // item.name をタイトルに設定
    isPreviewDialogVisible.value = true;
  } catch (error: any) {
    toast.error(t(error.message));
  }
};
const onCancelHandler = () => {
  isPreviewDialogVisible.value = false; // ダイアログを閉じる
};

// 削除処理
const onDeleted = async () => {
  if (!selectedDocumentId.value) return;
  try {
    await deleteDocument(selectedDocumentId.value); // API呼び出し
    fetchDocuments(); // データ再取得
    toast.success(t("message.delete_success"));
  } catch (error: any) {
    toast.error(t(error.message));
  } finally {
    isDialogVisible.value = false;
    selectedDocumentId.value = null;
  }
};

// 初期化時にデータ取得
onMounted(() => {
  fetchDocuments();
});
</script>
<template>
  <VRow>
    <VCol cols="12">
      <VContainer class="app-container">
        <VCard flat elevation="0">
          <VToolbar tag="div">
            <VToolbarTitle
              ><VIcon icon="mdi-folder-upload" />
              {{ t("submit_document") }}
            </VToolbarTitle>
            <!-- アップロード　ボタン-->
            <VCardActions>
              <VSpacer />
              <VBtn
                v-if="employeeRoles.includes(ERole.MEMBER)"
                color="primary"
                @click="handleCreateItem"
                variant="elevated"
                ><v-icon icon="mdi-plus" start></v-icon>{{ t("upload") }}
                <VDialog v-model="applyFrom" width="auto" persistent>
                  <UploadForm
                    @form:cancel="applyFrom = false"
                    @fetch="fetchDocuments"
                  />
                </VDialog>
              </VBtn>
            </VCardActions>
          </VToolbar>
          <VDivider />
          <!-- 申請情報　表示 -->
          <VCardItem>
            <VDataTable
              :items="documents"
              :items-per-page-text="t('items_per_page')"
              :headers="headers"
              :no-data-text="t('no_records_found')"
              class="data-table"
              v-if="!isLoading && !isError"
            >
              <!-- 表示　番号設定  -->
              <template v-slot:item.number="{ index }">
                {{ index + 1 }}
              </template>
              <template v-slot:item.title="{ item }">
                <span>{{ shortenFileName(item.title) }}</span>
              </template>
              <template v-slot:item.uploadBy="{ item }">
                <td>{{ shortenFileName(item.uploadBy) }}</td>
              </template>
              <!-- アクション　設定  -->
              <template v-slot:item.action="{ item }">
                <div class="action-buttons">
                  <VBtn
                    icon
                    v-if="employeeRoles.includes(ERole.MEMBER)"
                    variant="plain"
                    class="action-btn"
                    :disabled="item.employeeId !== isEmployee"
                    @click="handleDeleteItem(item.id)"
                  >
                    <VIcon color="red">mdi-delete</VIcon>
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="handlePreview(item)"
                  >
                    <VIcon color="blue">mdi-eye</VIcon>
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
    <ConfimDialogView
      :title="t('confirm')"
      :message="t('delete_confirm_message')"
      :isVisible="isDialogVisible"
      @update:isVisible="isDialogVisible = $event"
      @confirmed="onDeleted"
    />
  </VDialog>
  <!-- PDFプレビュー表示ダイアログ -->
  <VDialog v-model="isPreviewDialogVisible" width="auto" persistent>
    <PdfPreview
      :pdfUrl="pdfUrl"
      :pdfTitle="pdfTitle"
      :isVisible="isPreviewDialogVisible"
      @form:cancel="onCancelHandler"
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
  min-width: 663px !important;
}
</style>
