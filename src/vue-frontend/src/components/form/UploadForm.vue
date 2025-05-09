<!-- PDFファイル　アップロード　画面 -->
<script lang="ts" setup>
import { ref, onUnmounted } from "vue";
import { useI18n } from "vue-i18n";
import ConfimDialogView from "@/components/common/ConfimDialog.vue"; // 確認ダイアログ
import { useValidator } from "@/utils/validation"; // バリデーション
import { uploadDocument } from "@/api/document"; // API関数
import { toast } from "vue3-toastify"; // トースト通知
import { shortenFileName } from "@/utils/stringUtils";

const { t } = useI18n();
const isDialogVisible = ref(false); // 確認ダイアログ表示
const validator = useValidator(t); // バリデーション
const selectedFile = ref<File | null>(null); // 選択されたファイル
const pdfPreviewUrl = ref<string | null>(null); // PDF プレビュー URL
const emit = defineEmits(["form:cancel", "fetch"]);

// ファイルリセット
const resetFile = () => {
  selectedFile.value = null;
  pdfPreviewUrl.value = null;
};
// ファイルアップロード
const handleFileUpload = (event: Event) => {
  resetFile();
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];

    if (file.type !== "application/pdf") {
      alert("PDF ファイルを選択してください");
      resetFile(); // PDF以外のファイルが選ばれたらリセット
      return;
    }
    selectedFile.value = file;
    pdfPreviewUrl.value = URL.createObjectURL(file);
  } else {
    resetFile(); // ファイルが選択されていない場合リセット
  }
};
// アップロード確認
const uploadFile = async () => {
  if (!selectedFile.value) {
    toast.error(t("message.no_file_selected"));
    return;
  }
  isDialogVisible.value = true;
};

// アップロード処理
const onConfirmed = async () => {
  if (!selectedFile.value) {
    toast.error(t("error.validation_error"));
    return;
  }
  try {
    await uploadDocument(selectedFile.value);
    toast.success(t("message.upload_success"));
    emit("fetch");
  } catch (error) {
    console.error("PDF アップロードに失敗しました:", error);
    toast.error(t("error.upload_error"));
    return;
  }
  handleCancel(); // フォーム閉じる
};
// キャンセル処理
const handleCancel = () => {
  resetFile();
  emit("form:cancel");
};

// メモリリーク防止 (コンポーネント終了時)
onUnmounted(() => {
  if (pdfPreviewUrl.value) {
    URL.revokeObjectURL(pdfPreviewUrl.value);
  }
});
</script>

<template>
  <VCard class="v-card-form">
    <VToolbar tag="div">
      <VToolbarTitle
        ><VIcon icon="mdi-file-upload-outline" />{{
          t("pdf_file_upload")
        }}</VToolbarTitle
      >
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <VForm @submit.prevent="() => {}">
      <VContainer>
        <VFileInput
          v-model="selectedFile"
          label="PDF をアップロード"
          accept="application/pdf"
          @change="handleFileUpload"
          :rules="[validator.required]"
          :clearable="!selectedFile"
        >
          <template #selection="{ fileNames }">
            <span>{{ shortenFileName(fileNames[0]) }}</span>
          </template>
        </VFileInput>
      </VContainer>
    </VForm>
    <!-- PDF プレビュー -->
    <VCard flat elevation="0" v-if="pdfPreviewUrl" class="mt-4">
      <v-card-title>{{ t("pdf_view") }}</v-card-title>
      <v-card-text>
        <iframe :src="pdfPreviewUrl" width="100%" height="400px"></iframe>
      </v-card-text>
    </VCard>
    <VCardActions>
      <VBtn
        @click="uploadFile"
        type="submit"
        variant="elevated"
        color="primary"
      >
        {{ t("submit") }}
      </VBtn>
      <VBtn @click="resetFile" type="reset" variant="tonal">
        {{ t("reset") }}
      </VBtn>
    </VCardActions>

    <!-- 確認ダイアログ表示 -->
    <VDialog v-model="isDialogVisible" width="auto" eager persistent>
      <ConfimDialogView
        :title="t('confirm')"
        :message="t('apply_confirm_message')"
        :isVisible="isDialogVisible"
        @update:isVisible="isDialogVisible = $event"
        @confirmed="onConfirmed"
      />
    </VDialog>
  </VCard>
</template>
