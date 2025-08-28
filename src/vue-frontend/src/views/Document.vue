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
import { VCalendar } from "vuetify/labs/VCalendar";

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
const events = ref<IDocument[]>([]);

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
const headersOnDay = reactive([
  { title: t("number"), key: "number", sortable: false },
  { title: t("title"), key: "title" },
  { title: t("submitter"), key: "uploadBy" },
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
    fetchCalendar(documents.value);
  } catch (error: any) {
    isError.value = true;
    toast.error(t(error.message));
  } finally {
    isLoading.value = false;
  }
};
const getRandomColor = () => {
  let color = `#${Math.floor(Math.random() * 16777215).toString(16)}`;
  return color.length === 7 ? color : "#000000";
};
const fetchCalendar = (documents: IDocument[]) => {
  try {
    let tempEvents: IDocument[] = [];

    documents.forEach((item) => {
      let currentDate = new Date(item.uploadAt);
      tempEvents.push({
        id: item.id,
        title: item.title,
        start: new Date(item.uploadAt),
        end: new Date(item.uploadAt),
        color: getRandomColor(),
        uploadBy: item.uploadBy,
        filePath: item.filePath,
        uploadAt: item.uploadAt,
        employeeId: item.employeeId,
        allDay: true,
      });
      currentDate.setDate(currentDate.getDate() + 1);
    });

    events.value = tempEvents;
  } catch (error: any) {
    toast.error(t(error.message));
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
const iconSwitchView = ref("mdi-calendar-multiselect-outline");
const viewType = ref("table");
const switchView = () => {
  if (viewType.value == "table") {
    viewType.value = "calendar";
    iconSwitchView.value = "mdi-format-list-bulleted-square";
  } else if (viewType.value == "calendar") {
    viewType.value = "table";
    iconSwitchView.value = "mdi-calendar-multiselect-outline";
  }
};
interface ICalendarDay {
  date: string;
}
const getEventStyle = (color: string) => ({
  backgroundColor: color,
  border: `1px solid ${color}`,
  borderRadius: "4px",
  padding: "2px 4px",
  color: "#fff",
  fontSize: "12px",
  whiteSpace: "nowrap",
  overflow: "hidden",
  textOverflow: "ellipsis",
});

const getDayClass = (date) => {
  const day = date.getDay();
  if (day === 0 || day === 6) {
    return "weekend";
  }
  return "";
};

const getEventsByDate = (date: Date) => {
  return events.value.filter(
    (e) => new Date(e.start).toDateString() === date.toDateString()
  );
};
const dialog = ref(false);
const selectedDateEvents = ref<IDocument[]>([]);
const handleMoreClick = (date: Date) => {
  selectedDateEvents.value = getEventsByDate(date);
  dialog.value = true;
  let dateStr = date.toDateString();
  renderedDays.delete(dateStr);
};
const renderedDays = new Set<string>();
const shouldDisplayEvent = (day: any, event: any) => {
  let dayEvents = getEventsByDate(new Date(day.date));
  return dayEvents.length && dayEvents[0] === event;
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
            <div class="d-flex justify-end gap-2" style="min-height: 50px">
              <VBtn
                v-if="employeeRoles.includes(ERole.MANAGER) || employeeRoles.includes(ERole.ADMIN)"
                color="primary"
                variant="text"
                class="h-100"
                @click="switchView"
              >
                <div class="d-flex flex-column align-center">
                  <VIcon size="30" :icon="iconSwitchView" />
                  <span class="text-caption">{{ t("switch_view") }}</span>
                </div>
              </VBtn>
            </div>

            <!-- view as table -->
            <VDataTable
              :items="documents"
              :items-per-page-text="t('items_per_page')"
              :headers="headers"
              :no-data-text="t('no_records_found')"
              class="data-table"
              v-if="!isLoading && !isError && viewType == 'table'"
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

            <!-- view as calender -->
            <VCalendar
              v-if="viewType == 'calendar'"
              class="p-2"
              :events="events"
              event-text-color="#fff"
              event-overlap-mode="column"
              color="primary"
              hide-week-number
              :day-class="getDayClass"
            >
              <template #event="{ day, event }">
                <template v-if="shouldDisplayEvent(day, event)">
                  <template
                    v-for="(ev) in getEventsByDate(new Date((day as ICalendarDay).date)).slice(0, 3)"
                    :key="ev.title"
                  >
                    <VTooltip location="top">
                      <template #activator="{ props }">
                        <div
                          v-bind="props"
                          class="custom-event"
                          :style="getEventStyle(ev.color)"
                          @click="handlePreview({ id: ev.id, title: ev.title })"
                        >
                          {{ ev.title }}
                        </div>
                      </template>
                      {{ ev.title }}
                    </VTooltip>
                  </template>
                  <div
                    v-if="getEventsByDate(new Date((day as ICalendarDay).date)).length > 3"
                    class="more-events"
                    @click="
                      handleMoreClick(new Date((day as ICalendarDay).date))
                    "
                  >
                    <VIcon size="18" icon="mdi-dots-horizontal" />
                  </div>
                </template>
              </template>
            </VCalendar>
          </VCardItem>
        </VCard>
      </VContainer>
    </VCol>
  </VRow>
  <VDialog v-model="dialog" width="auto" persistent>
    <VCard>
      <VCardTitle>{{ t("list_documents") }}</VCardTitle>
      <VCardItem>
        <VDataTable
          :items="selectedDateEvents"
          :items-per-page-text="t('items_per_page')"
          :headers="headersOnDay"
          :no-data-text="t('no_records_found')"
          class="data-table-day"
        >
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
      <VCardActions>
        <VBtn @click="dialog = false">{{ t("close") }}</VBtn>
      </VCardActions>
    </VCard>
  </VDialog>
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

::v-deep(.data-table table) {
  min-width: 663px !important;
}
::v-deep(.data-table-day .v-table) {
  overflow-x: auto;
  display: block;
}
::v-deep(.data-table-day table) {
  min-width: 450px !important;
}
::v-deep(.v-calendar-header) {
  background-color: rgba(0, 86, 247, 0.2) !important;
  border-top-left-radius: 12px !important;
  border-top-right-radius: 12px !important;
  min-height: 56px;
  padding-left: 10px;
}
</style>
