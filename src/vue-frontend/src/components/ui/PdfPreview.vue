<!-- PDF プレビュー -->
<script lang="ts" setup>
import { ref, onUnmounted } from "vue";
import { useI18n } from "vue-i18n";
import { toast } from "vue3-toastify"; // トースト通知

const { t } = useI18n();
const selectedFile = ref<File | null>(null); // 選択されたファイル
const pdfPreviewUrl = ref<string | null>(null); // PDF プレビュー URL
const emit = defineEmits(["form:cancel"]); // form:cancel イベント

// ファイルリセット
const resetFile = () => {
  selectedFile.value = null;
  pdfPreviewUrl.value = null;
};

// PDF プレビュー
const props = defineProps<{
  pdfUrl: string | null;
}>();

// ファイルダウンロード
const handleDownload = () => {
  if (!props.pdfUrl) {
    toast.error("PDFファイルのURLが無効です。");
    return;
  }

  try {
    // ダウンロードリンクを作成
    const link = document.createElement("a");
    link.href = props.pdfUrl;
    link.download = props.pdfUrl.split("/").pop() || "downloaded_file.pdf";
    link.click();

    // ダウンロード完了後に成功メッセージを表示
    toast.success("ダウンロードが完了しました！");
  } catch (error) {
    // エラーが発生した場合にエラーメッセージを表示
    console.error("ダウンロード中にエラーが発生しました:", error);
    toast.error("ダウンロード中にエラーが発生しました。");
  }
};

// プレビューダイアログの状態を更新
if (props.pdfUrl) {
  pdfPreviewUrl.value = props.pdfUrl; // pdfUrl プロパティから URL を設定
}
const handleCancel = () => {
  emit("form:cancel"); // ここでイベントが発火する
  resetFile();
};
// メモリリーク防止 (コンポーネント終了時)
onUnmounted(() => {
  if (pdfPreviewUrl.value) {
    URL.revokeObjectURL(pdfPreviewUrl.value);
  }
});
</script>

<template>
  <VCard width="940px">
    <VToolbar tag="div">
      <VToolbarTitle>
        <VIcon icon="mdi-file-upload-outline" /> {{ t("pdf_view") }}
      </VToolbarTitle>
      <VBtn @click="handleCancel" icon :disabled="false">
        <VIcon icon="mdi-close" />
      </VBtn>
    </VToolbar>
    <!-- PDF プレビュー -->
    <VCard flat elevation="0" v-if="pdfPreviewUrl" class="mt-4">
      <v-card-text>
        <iframe
          v-if="pdfPreviewUrl"
          :src="pdfPreviewUrl"
          width="100%"
          height="400px"
          frameborder="0"
        ></iframe>
      </v-card-text>
    </VCard>
    <VCardActions>
      <VBtn
        @click="handleDownload"
        type="dawnload"
        variant="elevated"
        color="primary"
        ><v-icon icon="mdi-content-save" start></v-icon
        >{{ t("dawnload") }}</VBtn
      >
    </VCardActions>
  </VCard>
</template>
