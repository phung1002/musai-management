<!-- 申請詳細　画面 -->
<script setup lang="ts">
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { ILeaveResponse } from "@/types/type";
import { updateLeaveRespond } from "@/api/response";
import { toast } from "vue3-toastify";
import { ELeaveStatus } from "@/constants/leaveStatus";
const { t } = useI18n();
const props = defineProps<{ LeaveResponse: ILeaveResponse }>();
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
const emit = defineEmits(["form:cancel", "fetch"]);
const handleCancel = () => {
  emit("form:cancel");
};
const isDialogVisible = ref(false); // 確認ダイアログ表示
const targetId = ref<number | null>(null); // 取り消す ID を保持

// 取り消しボタンを押した時
const handleRevoked = (id: number) => {
  targetId.value = id;
  isDialogVisible.value = true;
};

// API に削除リクエストを送信
const onRevoked = async () => {
  if (targetId.value === null) return;
  try {
    await updateLeaveRespond(targetId.value, ELeaveStatus.REVOKED);
    toast.success(t("message.cancel_success"));
    // await fetchLeaveType(); // 最新データを取得
    isDialogVisible.value = false; // ダイアログを閉じる
    emit("fetch");
    handleCancel();
  } catch (error: any) {
    toast.error(t(error.message));
    console.error("Error rejecting request:", error);
  }
};
</script>

<template>
  <VCard min-width="600">
    <VToolbar tag="div">
      <VCardTitle>{{ t("detail_information") }}</VCardTitle>
      <VSpacer />
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <VCardItem>
      <VList>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-account" class="icon-spacing" />
            {{ t("employee_name") }} :
          </template>
          <VListItemTitle> {{ LeaveResponse.userFullName }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-calendar" class="icon-spacing" />{{
              t("leave_type")
            }}
            :
          </template>
          <VListItemTitle> {{ LeaveResponse.leaveTypeName }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-comment" class="icon-spacing" />{{
              t("leave_reason")
            }}
            :
          </template>
          <VListItemTitle> {{ LeaveResponse.reason }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-calendar-start" class="icon-spacing" />{{
              t("leave_duration_from")
            }}
            :
          </template>
          <VListItemTitle> {{ LeaveResponse.startDate }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-calendar-end" class="icon-spacing" />{{
              t("leave_duration_to")
            }}
            :
          </template>
          <VListItemTitle> {{ LeaveResponse.endDate }}</VListItemTitle>
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-alert-box-outline" class="icon-spacing" />{{
              t("status")
            }}
            :
          </template>
          <VListItemTitle>
            {{
              t(`application_status.${LeaveResponse.status}`)
            }}</VListItemTitle
          >
        </VListItem>
        <VListItem>
          <template v-slot:prepend>
            <VIcon icon="mdi-account-box-edit-outline" class="icon-spacing" />{{
              t("responded_by")
            }}
            :
          </template>
          <VListItemTitle>
            {{ LeaveResponse.respondedByFullName }}</VListItemTitle
          >
        </VListItem>
      </VList>
    </VCardItem>
    <VCardActions>
      <VBtn
        @click="handleRevoked(LeaveResponse.id)"
        type="submit"
        variant="elevated"
        color="red"
        :disabled="LeaveResponse.status != ELeaveStatus.APPROVED"
      >
        {{ t("application_status.REVOKED") }}
      </VBtn>
    </VCardActions>
  </VCard>
  <!-- キャンセル確認 -->
  <VDialog v-model="isDialogVisible" width="auto" eager persistent>
    <ConfimDialogView
      :title="t('confirm')"
      :message="t('revoked_confirm_message')"
      :isVisible="isDialogVisible"
      @update:isVisible="isDialogVisible = $event"
      @confirmed="onRevoked"
    />
  </VDialog>
</template>

<style>
.icon-spacing {
  margin-right: 8px; /* 8px のスペースを追加 */
}
</style>
