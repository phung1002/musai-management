<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { getAllUsers } from "@/api/user";
import { IUser } from "@/types/type";
const { t } = useI18n();

const props = defineProps({
  title: {
    type: String,
    default: "確認",
  },
  isVisible: {
    type: Boolean,
    default: false,
  },
});
const emit = defineEmits(["update:isVisible", "selectUser"]);
const visible = ref(props.isVisible);

// テーブルのヘッダー
const headers = reactive([
  { title: t("number"), key: "number" },
  // { title: t("employee_id"), key: "id" },
  { title: t("full_name"), key: "fullName" },
]);

// 検索
const keyWord = ref("");
const users = ref<IUser[]>([]);
const isLoading = ref(false);
const isError = ref(false);

// ユーザーリスト取得API呼び出
const fetchUsers = async (searchQuery: string = "") => {
  isLoading.value = true;
  isError.value = false;
  try {
    // 検索キーワードが空でも呼び出せる
    const response = await getAllUsers(searchQuery);
    loadUser(response);
  } catch (error) {
    isError.value = true;
  } finally {
    isLoading.value = false;
  }
};
const handleSearch = () => {
  if (!keyWord.value.trim()) {
    // 入力が空の場合、リストを再表示（全データを取得）
    fetchUsers();
  } else {
    // 入力がある場合は検索を実行
    fetchUsers(keyWord.value);
  }
};
const handleClear = () => {
  keyWord.value = ""; // キーワードを空に設定
  fetchUsers(); // 空の検索でリストを再表示
};
const loadUser = (lst: any) => {
  users.value = lst.map((user: IUser) => ({
    ...user,
  }));
};

// 行がクリックされたときの処理
const onRowClick = (item: IUser) => {
  emit("selectUser", { id: item.id, name: item.fullName });
  emit("update:isVisible", false);
};

// 閉じる
const onCancel = () => {
  visible.value = false;
  emit("update:isVisible", false);
};

// コンポーネントがマウントされたときAPI呼び出し修理実行
onMounted(() => {
  fetchUsers();
});
</script>
<template>
  <VCard flat width="640px">
    <VToolbar tag="div">
      <VToolbarTitle>
        <VIcon icon="mdi-account-search" />{{ title }}
      </VToolbarTitle>
      <VBtn icon="mdi-close" @click="onCancel"></VBtn>
    </VToolbar>
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
          @click:clear="handleClear"
          @keydown.enter="handleSearch"
        />
        <VBtn icon density="comfortable" @click="handleSearch">
          <VIcon>mdi-magnify</VIcon>
        </VBtn>
      </VToolbar>
    </VCardItem>
    <VDivider />
    <!--ユーザーリスト表示 -->
    <VCardItem>
      <VDataTable
        :headers="headers"
        :items="users"
        :items-per-page-text="t('items_per_page')"
        :no-data-text="t('no_records_found')"
        v-if="!isLoading && !isError"
      >
        <!-- クリック時に item を渡す -->
        <template v-slot:item="{ item, index }">
          <tr @click="onRowClick(item)" class="selectrow-btn">
            <td>{{ index + 1 }}</td>
            <!-- <td>{{ item.id }}</td> -->
            <td>{{ item.fullName }}</td>
          </tr>
        </template>
      </VDataTable>
    </VCardItem>
  </VCard>
</template>

<style scoped>
.selectrow-btn {
  background-color: white;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 8px;
}

.selectrow-btn:hover {
  background-color: #f5f5f5;
}
</style>
