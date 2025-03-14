<!-- 書類提出 画面　-->
<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import UploadForm from "@/components/form/UploadForm.vue"; // フォームコンポーネント
import ConfimDialogView from "@/components/common/ConfimDialog.vue"; // 確認ダイアログ
import PdfPreview from "@/components/ui/PdfPreview.vue"; // PDFプレビュー
import {
  getDocuments,
  deleteDocument,
  getDocumentPreview,
} from "@/api/document"; // API関数
import { IDocument } from "@/types/type"; // 型定義
import { useI18n } from "vue-i18n";
import { toast } from "vue3-toastify"; // トースト通知
const { t } = useI18n();

const applyFrom = ref(false); // フォーム表示
const isDialogVisible = ref(false); // 確認ダイアログ表示
const documents = ref<IDocument[]>([]); // 🔹 動的データ
const selectedDocumentId = ref<number | null>(null); // 🔹 削除用
const pdfPreviewUrl = ref<string | null>(null); // PDFプレビューURL
const isPreviewDialogVisible = ref(false); // プレビューダイアログ表示制御
const pdfUrl = ref<string | null>(null); // PDFファイルURL
const isLoading = ref(false); // ローディング
const isError = ref(false); // エラープラグ
// // テーブル　ヘッダー
const headers = reactive([
  { title: t("number"), key: "number" },
  { title: t("title"), key: "title" },
  { title: t("submitter"), key: "submitter" },
  { title: t("submit_date"), key: "submit_date" },
  { title: t("action"), key: "action" },
]);
// データ取得
const fetchDocuments = async () => {
  isLoading.value = true;
  isError.value = false;
  try {
    // documents.value = await getDocuments();
    console.log(documents.value);
  } catch (error) {
    isError.value = true;
    console.error("データ取得失敗:", error);
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
const handlePreview = async (id: number) => {
  try {
    const response = await getDocumentPreview(id);
    // 取得したFileオブジェクトを使ってBlob URLを生成
    if (response.filePath) {
      pdfUrl.value = URL.createObjectURL(response.filePath); // Blob URLに変換して設定
    }
    isPreviewDialogVisible.value = true;
    console.log("PDFプレビュー表示:", response.filePath);
  } catch (error) {
    console.error("PDFのプレビューに失敗しました:", error);
  }
};
// プレビューダイアログを閉じる
const closePreviewDialog = () => {
  isPreviewDialogVisible.value = false;
  pdfPreviewUrl.value = null;
};

// 削除処理
const onDeleted = async () => {
  if (!selectedDocumentId.value) return;

  try {
    await deleteDocument(selectedDocumentId.value); // API呼び出し
    toast.success(t("message.delete_success"));
  } catch (error) {
    console.error("ドキュメントの削除に失敗しました:", error);
    toast.error(t("message.delete_failure"));
  } finally {
    isDialogVisible.value = false;
    selectedDocumentId.value = null;
  }
};

// 初期化時にデータ取得
onMounted(() => {
  fetchDocuments();
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
              ><VIcon icon="mdi-folder-upload" />
              {{ t("submit_document") }}
            </VToolbarTitle>
            <!-- アップロード　ボタン-->
            <VCardActions>
              <VSpacer />
              <VBtn color="primary" @click="handleCreateItem" variant="elevated"
                ><v-icon icon="mdi-plus" start></v-icon>{{ t("upload") }}
                <VDialog v-model="applyFrom" width="auto" eager>
                  <UploadForm @form:cancel="applyFrom = false" />
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
                    @click="handleDeleteItem(item.id)"
                  >
                    <VIcon color="red">mdi-delete</VIcon>
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="handlePreview(item.id)"
                  >
                    <VIcon color="blue"
                      >mdi-microsoft-xbox-controller-view</VIcon
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
  <VDialog v-model="isDialogVisible" width="auto" eager>
    <ConfimDialogView
      :title="t('confirm')"
      :message="t('delete_confirm_message')"
      :isVisible="isDialogVisible"
      @update:isVisible="isDialogVisible = $event"
      @confirmed="onDeleted"
    />
    <!-- PDFプレビュー表示 -->
    <PdfPreview :pdfUrl="pdfUrl" :isVisible="isPreviewDialogVisible" />
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
