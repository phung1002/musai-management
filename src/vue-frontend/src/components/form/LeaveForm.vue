<script lang="ts" setup>
import { ref, Ref, defineProps, onMounted, reactive, watch } from "vue";
import { useI18n } from "vue-i18n";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { VTab } from "vuetify/lib/components/index.mjs";
import { useValidator } from "@/utils/validation";
import { ILeaveTypes } from "@/types/type";
import { getLeavesTree } from "@/api/leave";
import { showSnackbar } from "@/composables/useSnackbar";
const { t } = useI18n(); //日本語にローカル変更用
const emit = defineEmits(["form-cancel", "refetch-data"]);
const errors = ref<{ leave_type?: string; leave_name?: string }>({});
const props = defineProps<{ leave?: ILeaveTypes; isEdit: boolean }>();
const leaves = ref<ILeaveTypes[]>([]); // 休暇リスト
const validator = useValidator(t); // バリデーション
const isDialogVisible = ref(false); // 確認ダイアログ表示
const isLoading = ref(false); // ローディングフラグ
const isError = ref(false); // エラーフラグ
const activeTab = ref("personal-info"); // タブの初期値
// フォームのデータ
// const formData = ref({
//   // // paid_leave: "",
//   // public_leave: "",
//   // leave_name: "",
// });
const defaultLeave = {
  id: 0,
  name: "",
};
const formModel = reactive<ILeaveTypes>(
  // isEdit が true の場合は props.leave を、false の場合は空のデフォルト値を使う
  props.isEdit ? { ...defaultLeave, ...props.leave } : { ...defaultLeave }
);

// 入力初期化
const handleResetFilter = () => {
  formModel.name = "";
  // formModel.public_leave
};
// エラーメッセージ初期化
const resetErrors = () => {
  errors.value = {
    leave_type: "",
    leave_name: "",
  };
};
const handleCancel = () => {
  handleResetFilter();
  resetErrors();
  emit("form-cancel");
};
// 各カテゴリーの items
const paid_leave: Ref<{ title: string; value: string }[]> = ref([]);
const public_leave: Ref<{ title: string; value: string }[]> = ref([]);
// メイン休暇タブで分類
const tabs = [
  { title: t("paid_leave"), icon: "mdi-gift-open", tab: "paid" },
  { title: t("public_leave"), icon: "mdi-pine-tree-box", tab: "public" },
];
// 休暇リスト取得　API呼び出し
const fetchLeaveType = async () => {
  isLoading.value = true;
  isError.value = false;
  try {
    const response = await getLeavesTree();
    leaves.value = response;

    // カテゴリーごとに分類
    categorizeLeaves(response);
  } catch (error) {
    isError.value = true;
    console.error("Error fetching leaves:", error);
  } finally {
    isLoading.value = false;
  }
};
// 取得データをカテゴリーごとに分ける関数
const categorizeLeaves = (data: ILeaveTypes[]) => {
  data.forEach((leave) => {
    if (leave.id === 3) {
      paid_leave.value =
        leave.children?.map((child) => ({
          title: child.name,
          value: child.name.toUpperCase(),
        })) || [];
    }
    if (leave.id === 4) {
      public_leave.value =
        leave.children?.map((child) => ({
          title: child.name,
          value: child.name.toUpperCase(),
        })) || [];
    }
  });
};
// コンポーネントがマウントされたときAPI呼び出し修理実行
onMounted(() => {
  fetchLeaveType();
});
// フォーム送信処理
const handleSubmit = () => {
  if (!props.isEdit) {
    isDialogVisible.value = true;
    console.log("新しいデータを登録します...");
    // 登録処理を実行
    // emit("form-cancel"); // フォームを閉じる
  } else {
    isDialogVisible.value = true;
    console.log("データを更新します...");
    // 更新処理を実行
    // emit("form-cancel"); // フォームを閉じる
  }
};
// 確認ダイアログで許可されたらイベント発火
const onConfirmed = () => {
  if (!props.isEdit) {
    console.log("新しいデータを登録します...");
    // 登録処理を実行
    emit("form-cancel"); // フォームを閉じる
  } else {
    console.log("データを更新します...");
    // 更新処理を実行
    emit("form-cancel"); // フォームを閉じる
  }
};

// const handleSubmit = async () => {
//   // エラーチェック

//   // if (Object.keys(errors.value).length > 0) {
//   //   showSnackbar('validation_error', 'error');
//   //   // 登録処理を中断する
//   //   return;
//   // } else if (Object.keys(errors.value).length < 0)
//   // return;
//   isDialogVisible.value = true;
//   // message.value = "";
// };
</script>

<template>
  <VCard class="leave_form">
    <VToolbar tag="div">
      <!-- 新規登録際タイトルの表示 -->
      <VToolbarTitle v-if="!isEdit">
        <VIcon icon="mdi-lead-pencil" />{{ t("leave_request") }}
      </VToolbarTitle>
      <!-- 編集再タイトルの表示 -->
      <VToolbarTitle v-else>
        <VIcon icon="mdi-lead-pencil" />{{ t("update_leave") }}
      </VToolbarTitle>
      <VBtn icon="mdi-close" @click="handleCancel"></VBtn>
    </VToolbar>
    <!-- 新規登録際表示 -->
    <VForm VForm ref="formRef" @submit.prevent="() => {}" v-if="!isEdit">
      <v-container>
        <VTable>
          <VTabs v-model="activeTab" color="primary">
            <VTab v-for="item in tabs" :key="item.icon" :value="item.tab">
              <VIcon size="20" start :icon="item.icon" />
              {{ item.title }}
            </VTab>
          </VTabs>
          <VCardText>
            <VWindow v-model="activeTab">
              <span v-if="errors.leave_type" class="error" style="color: red">{{
                errors.leave_type
              }}</span>
              <VWindowItem value="paid">
                <VCardText>
                  <VRow>
                    <VCol :cols="4" class="dropdown-box">
                      <VTextField
                        v-model="formModel.name"
                        input
                        type="text"
                        :label="t('leave_name')"
                        :rules="[validator.required]"
                      />
                    </VCol>
                  </VRow>
                </VCardText>
              </VWindowItem>
              <VWindowItem value="public">
                <VCardText>
                  <VRow>
                    <VCol :cols="4" class="dropdown-box">
                      <VAutocomplete
                        v-model="formModel.name"
                        :items="public_leave"
                        :label="t('public_leave')"
                        item-title="title"
                        item-value="value"
                      />
                    </VCol>
                  </VRow>
                </VCardText>
                <VCardText>
                  <VRow>
                    <VCol :cols="4" class="dropdown-box">
                      <VTextField
                        v-model="formModel.name"
                        input
                        type="text"
                        :label="t('leave_name')"
                        :rules="[validator.required]"
                      />
                    </VCol>
                  </VRow>
                </VCardText>
              </VWindowItem>
            </VWindow>
          </VCardText>
        </VTable>
        <VDivider />
      </v-container>
    </VForm>
    <!-- 編集際表示 -->
    <VForm VForm ref="formRef" V-if="isEdit" @submit.prevent="() => {}" v-else>
      <v-container>
        <VRow v-if="formModel.parentId !== null">
          <VCol :cols="4" class="dropdown-box">
            <VTextField
              v-model="formModel.parentId"
              input
              type="text"
              :label="t('parent_id')"
              :rules="[validator.required]"
            />
          </VCol>
        </VRow>
        <VRow>
          <VCol :cols="4" class="dropdown-box">
            <VTextField
              v-model="formModel.name"
              input
              type="text"
              :label="t('leave_name')"
              :rules="[validator.required]"
            />
          </VCol>
        </VRow>
      </v-container>
    </VForm>
    <VCardActions>
      <VBtn
        @click="handleSubmit"
        type="submit"
        variant="elevated"
        color="primary"
        >{{ isEdit ? t("update") : t("submit") }}
      </VBtn>
      <VBtn @click="handleResetFilter" type="reset" variant="tonal">{{
        t("reset")
      }}</VBtn>
    </VCardActions>
    <!-- 確認ダイアログ表示 -->
    <VDialog v-model="isDialogVisible" width="auto" eager>
      <ConfimDialogView
        :title="t('confirm')"
        :message="
          isEdit
            ? t('leave_update_confirm_message')
            : t('leave_apply_confirm_message')
        "
        :isVisible="isDialogVisible"
        @update:isVisible="isDialogVisible = $event"
        @confirmed="onConfirmed"
      />
    </VDialog>
  </VCard>
</template>
