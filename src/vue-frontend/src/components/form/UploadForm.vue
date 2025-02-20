<script lang="ts" setup>
import { ref, reactive } from "vue";
import { useI18n } from "vue-i18n";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import SnackBar from "@/components/common/SnackBar.vue";
import { showSnackbar } from "@/composables/useSnackbar";
import { useValidator } from "@/utils/validation";
// import { uploadPdf } from '@/api/request';
const { t } = useI18n();
const isDialogVisible = ref(false);
const validator = useValidator(t);
const selectedFile = ref<File | null>(null);
const pdfPreviewUrl = ref<string | null>(null);

const emit = defineEmits(["form:cancel"]);
const resetFile = () => {
  selectedFile.value = null;
  pdfPreviewUrl.value = null;
};
const handleFileUpload = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];

    if (file.type !== "application/pdf") {
      alert("PDF ファイルを選択してください");
      return;
    }

    selectedFile.value = file;
    pdfPreviewUrl.value = URL.createObjectURL(file);
  }
};
// 入力チェック　バリデーションを行う
const uploadFile = async () => {
  console.log("Uploaded PDF URL:", selectedFile.value);
  if (!selectedFile.value) {
    showSnackbar("validation_error", "error");
    return;
  }
  isDialogVisible.value = true;
};
const handleCancel = () => {
  resetFile();
  emit("form:cancel");
};
// API 通信
const onConfirmed = async () => {
  if (!selectedFile.value) {
    showSnackbar("ファイルが選択されていません", "error");
    return;
  }
  // アップロード処理
  console.log("Uploading PDF:", selectedFile.value);
  try {
    // const uploadedUrl = await uploadPdf(selectedFile.value);
    // console.log("Uploaded PDF URL:", uploadedUrl);
    showSnackbar("upload_success", "success");
  } catch (error) {
    console.error("PDF アップロードに失敗しました:", error);
    showSnackbar("upload_error", "error");
  }
  handleCancel(); //フォーム閉じる
};
</script>

<template>
  <VCard width="940px">
    <VToolbar tag="div">
      <SnackBar :snackbar="showSnackbar"></SnackBar>
      <VToolbarTitle
        ><VIcon icon="mdi-file-upload-outline" />{{
          t("pdf_file_upload")
        }}</VToolbarTitle
      >
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <VForm @submit.prevent="() => {}">
      <v-container>
        <VRow>
          <VCol cols="6">
            <v-file-input
              v-model="selectedFile"
              label="PDF をアップロード"
              accept="application/pdf"
              @change="handleFileUpload"
              :rules="[validator.required]"
            ></v-file-input>
          </VCol>
        </VRow>
      </v-container>
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
        >{{ t("submit") }}</VBtn
      >
      <VBtn @click="resetFile" type="reset" variant="tonal">{{
        t("reset")
      }}</VBtn>
    </VCardActions>

    <!-- 確認ダイアログ表示 -->
    <VDialog v-model="isDialogVisible" width="auto" eager>
      <ConfimDialogView
        :title="t('confirm')"
        :message="t('leave_apply_confirm_message')"
        :isVisible="isDialogVisible"
        @update:isVisible="isDialogVisible = $event"
        @confirmed="onConfirmed"
      />
    </VDialog>
  </VCard>
</template>
