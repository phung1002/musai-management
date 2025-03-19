<!-- ユーザー管理画面 -->
<script setup lang="ts">
import { reactive, ref, onMounted, watch } from "vue";
import { useI18n } from "vue-i18n";
import { deleteUser, getAllUsers } from "@/api/user";
import { IUser } from "@/types/type";
import UserForm from "@/components/form/UserForm.vue";
import ConfimDialogView from "@/components/common/ConfimDialog.vue";
import { toast } from "vue3-toastify";
import { ERole } from "@/constants/role";

const isConfirmDialogVisible = ref(false);
const formatRole = (role: string) => role;
const { t } = useI18n();

// Headers of table
const headers = reactive([
  { title: t("number"), key: "number" },
  { title: t("employee_name"), key: "username" },
  { title: t("email"), key: "email" },
  { title: t("full_name"), key: "fullName" },
  { title: t("role"), key: "roles" },
  { title: t("department"), key: "department" },
  { title: t("work_place"), key: "workPlace" },
  { title: t("action"), key: "action" },
]);

const keyWord = ref("");
const users = ref<IUser[]>([]);
const isLoading = ref(false);
const isError = ref(false);

const isEdit = ref(false);
const showDialog = ref(false);
const openCreateDialog = () => {
  isEdit.value = false;
  showDialog.value = true;
};
const openUpdateDialog = (user: IUser) => {
  isEdit.value = true;
  showDialog.value = true;
  selectedUser.value = user;
};
const selectedUser = ref<IUser>({} as IUser);
const openConfirmDialog = (user: IUser) => {
  selectedUser.value = user;
  isConfirmDialogVisible.value = true;
};
// Get list user from api API
const fetchUsers = async () => {
  isLoading.value = true;
  isError.value = false;
  try {
    const response = await getAllUsers(keyWord.value);
    loadUser(response);
    console.log("response", response);
  } catch (error) {
    isError.value = true;
  } finally {
    isLoading.value = false;
  }
};

const loadUser = (lst: any) => {
  users.value = lst.map((user: IUser) => ({
    ...user,
    roles: user.roles.map(formatRole),
  }));
};

const handleDeleteUser = async () => {
  if (!selectedUser.value.id) return;

  try {
    await deleteUser(selectedUser.value.id);
    toast.success(t("message.delete_success"));
    fetchUsers();
  } catch (error: any) {
    toast.error(t(error.message));
  } finally {
    isConfirmDialogVisible.value = false;
  }
};
const getRoleColor = (roles: string) => {
  switch (roles) {
    case ERole.ADMIN:
      return "red";
    case ERole.MANAGER:
      return "orange";
    default:
      return "green";
  }
};
// Call API when component is mounted
onMounted(() => {
  fetchUsers();
});
</script>

<template>
  <VRow>
    <VCol cols="12">
      <VContainer class="app-container">
        <!-- Add user button -->
        <VCard flat elevation="0">
          <VToolbar tag="div">
            <VToolbarTitle>
              <VIcon>mdi-account-box-multiple-outline</VIcon>
              <span class="text-lg font-medium ml-2">{{
                t("employee_management")
              }}</span>
            </VToolbarTitle>
            <!-- 申請入力フォーム　ボタン-->
            <VCardActions>
              <VSpacer />
              <VBtn
                color="primary"
                variant="elevated"
                @click="openCreateDialog"
              >
                <VIcon>mdi-plus</VIcon>
                <span class="text-lg font-medium ml-2">{{
                  t("register")
                }}</span>
              </VBtn>
            </VCardActions>
          </VToolbar>
          <!-- Search -->
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
                @click:clear="fetchUsers"
                @keydown.enter="fetchUsers"
              />
              <VBtn icon density="comfortable" @click="fetchUsers">
                <VIcon>mdi-magnify</VIcon>
              </VBtn>
            </VToolbar>
          </VCardItem>
          <VDivider />
          <!--Table list user -->
          <VCardItem>
            <VDataTable
              :headers="headers"
              :items="users"
              :items-per-page-text="t('items_per_page')"
              :no-data-text="t('no_records_found')"
              v-if="!isLoading && !isError"
            >
              <!-- Slot for 'no'  -->
              <template v-slot:item.number="{ index }">
                {{ index + 1 }}
              </template>

              <!-- Slot for 'roles' -->
              <template v-slot:item.roles="{ item }">
                <VChipGroup column active-class="bg-primary text-white">
                  <VChip
                    v-for="(role, index) in item.roles"
                    :style="{ color: getRoleColor(role) }"
                    :key="index"
                    text-color="white"
                  >
                    {{ t(`roles.${role}`) }}
                  </VChip>
                </VChipGroup>
              </template>

              <!-- Slot for 'action'  -->
              <template v-slot:item.action="{ item }">
                <div class="action-buttons">
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="openUpdateDialog(item)"
                  >
                    <VIcon color="blue">mdi-pencil</VIcon>
                  </VBtn>
                  <VBtn
                    icon
                    variant="plain"
                    class="action-btn"
                    @click="openConfirmDialog(item)"
                  >
                    <VIcon color="red">mdi-delete</VIcon>
                  </VBtn>
                </div>
              </template>
            </VDataTable>
          </VCardItem>
        </VCard>
      </VContainer>
    </VCol>
  </VRow>
  <!-- Dialog Create/Update user -->
  <VDialog v-model="showDialog" width="auto" persistent>
    <UserForm
      :isEdit="isEdit"
      :user="selectedUser"
      @form-cancel="showDialog = false"
      @refetch-data="fetchUsers"
    />
  </VDialog>
  <VDialog v-model="isConfirmDialogVisible" width="auto">
    <ConfimDialogView
      :title="t('confirm')"
      :message="t('delete_confirm_message')"
      :isVisible="isConfirmDialogVisible"
      @update:isVisible="isConfirmDialogVisible = $event"
      @confirmed="handleDeleteUser"
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
</style>
