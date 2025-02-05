<script setup lang="ts">
import { reactive, ref, onMounted, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { getAllUsers } from '@/api/user';
import AppSidebar from '@/components/layout/AppSidebar.vue';
import AppToolbar from '@/components/layout/AppToolbar.vue';
import { IUser } from '@/types/type';
import UserForm from '@/components/user/UserForm.vue';
import { useUserStore } from '@/store/userStore';
import SnackBar from '@/components/common/SnackBar.vue';
import { useSnackbar } from '@/composables/useSnackbar';

const { snackbar } = useSnackbar();
const userStore = useUserStore();

const formatRole = (role: string) => role.toLowerCase();

// i18n: to translate
const { t } = useI18n();

// Headers of table
const headers = reactive([
  { title: t('number'), key: 'number' },
  { title: t('username'), key: 'username' },
  { title: t('email'), key: 'email' },
  { title: t('full_name'), key: 'fullName' },
  { title: t('role'), key: 'roles' },
  { title: t('department'), key: 'department' },
  { title: t('work_place'), key: 'workPlace' },
  { title: t('action'), key: 'action' },
]);

// Status
const users = ref<IUser[]>([]);
const isLoading = ref(false);
const isError = ref(false);

// Get list user from api API
const fetchUsers = async () => {
  isLoading.value = true;
  isError.value = false;

  try {
    const response = await getAllUsers(); // Call API

    users.value = response.map((user: IUser) => ({
      ...user,
      roles: user.roles.map(formatRole),
    }));
  } catch (error) {
    isError.value = true;
    console.error('Error fetching users:', error);
  } finally {
    isLoading.value = false;
  }
};

//set color for each role
const getRoleColor = (role: string) => {
  switch (role) {
    case 'admin':
      return 'red';
    case 'manager':
      return 'yellow';
    case 'member':
      return 'green';
    default:
      return 'grey';
  }
};
const isEdit = ref(false);
const showDialog = ref(false);
const handleCreateItem = () => {
  isEdit.value = false;
  showDialog.value = true;
};
// Hàm tìm kiếm role title theo value
const getRoleTitle = (roleValue: string) => {
  const role = userStore.roles.find(r => r.value === roleValue);
  return role ? role.title : roleValue;
};
// Call API when component is mounted
onMounted(() => {
  fetchUsers();
});
</script>

<template>
  <VApp class="app">
    <!------Sidebar-------->
    <AppSidebar />
    <!------Header-------->
    <AppToolbar />
    <!------Page-------->
    <VMain class="app-main">
      <SnackBar :snackbar="snackbar"></SnackBar>
      <VContainer class="app-container">
        <div class="page-wrapper">
          <!-- Add user button -->
          <VCardActions>
           <VSpacer />
            <VBtn color="primary" variant="elevated" @click="handleCreateItem">{{ t('register') }}</VBtn>
          </VCardActions>
          <VCard>
            <!-- Search -->
            <VCardItem class="py-0">
              <VToolbar tag="div" color="transparent" flat>
                <VTextField
                  :prepend-icon="'mdi-filter-variant'"
                  :placeholder="t('type_something')"
                  hide-details
                  clearable
                  variant="plain"
                  class="search"
                />
                <VBtn icon density="comfortable">
                  <VIcon>mdi-magnify</VIcon>
                </VBtn>
              </VToolbar>
            </VCardItem>

            <VDivider/>
            <!--Table list user -->
            <VCardItem>
              <VDataTable
                :headers="headers"
                :items="users"
                :items-per-page-text="t('items_per_page')"
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
                    :key="index"
                    :color="getRoleColor(role)"
                    variant="elevated"
                    text-color="white"
                  >
                    {{ t(getRoleTitle(role)) }}
                  </VChip>
                </VChipGroup>
              </template>

              <!-- Slot for 'action'  -->
              <template v-slot:item.action="{ item }">
                <div class="action-buttons">
                  <VBtn icon variant="plain" class="action-btn" @click="onEdit(item)">
                    <VIcon color="blue">mdi-pencil</VIcon>
                  </VBtn>
                  <VBtn icon variant="plain" class="action-btn" @click="onDelete(item)">
                    <VIcon color="red">mdi-delete</VIcon>
                  </VBtn>
                </div>
              </template>

            </VDataTable>
          </VCardItem>
        </VCard>
      </div>
    </VContainer>
  </VMain>
</VApp>
<!-- Dialog Create/Update user -->
<VDialog v-model="showDialog" width="auto" persistent>
  <UserForm :isEdit="isEdit" @form:cancel="showDialog = false" />
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
../../components/user/UserForm.vue
