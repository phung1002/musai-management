<script setup lang="ts">
import { reactive, ref, onMounted, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { getAllUsers } from '@/api/user';
import { useUserStore } from '@/store/userStore';
import { computed } from 'vue';
import AppSidebar from '@/components/layout/AppSidebar.vue';
import AppToolbar from '@/components/layout/AppToolbar.vue';

interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  roles: string[];
  department: string;
  workPlace: string;
}
const formatRole = (role: string) => {
  // Delete "ROLE_" and upper the first case
  return role.replace('ROLE_', '').toLowerCase().replace(/_/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase());
};

// i18n: to translate
const { t } = useI18n();

// Headers of table
const headers = reactive([
  { title: t('number'), key: 'no' },
  { title: t('username'), key: 'username' },
  { title: t('email'), key: 'email' },
  { title: t('full_name'), key: 'fullname' },
  { title: t('role'), key: 'roles' },
  { title: t('department'), key: 'department' },
  { title: t('work_place'), key: 'workPlace' },
  { title: t('action'), key: 'action' },
]);

// Status
const users = ref<User[]>([]);
const isLoading = ref(false);
const isError = ref(false);

// Get list user from api API
const fetchUsers = async () => {
  isLoading.value = true;
  isError.value = false;

  try {
    const response = await getAllUsers(); // Call API
    users.value = response.data.map((user: User) => ({
      ...user,
      roles: user.roles.map(formatRole), // Format role
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
    case 'Admin':
      return 'red';
    case 'Manager':
      return 'yellow';
    case 'Member':
      return 'green';
    default:
      return 'grey';
  }
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
      <VContainer class="app-container">
        <VCard flat elevation="0">
          <VToolbar tag="div">
            <VToolbarTitle><VIcon icon= "mdi-list-box"/>{{ t('user_list') }}</VToolbarTitle>
            <!-- Add user button -->
            <VCardActions>
              <VSpacer />
              <VBtn color="primary" variant="elevated"><v-icon icon="mdi-plus" start></v-icon>{{ t('user_register') }}</VBtn>
            </VCardActions>
          </VToolbar>
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
              <template v-slot:item.no="{ index }">
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
                    {{ role }}
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
                  @click="onEdit(item)"
                >
                  <VIcon color="blue">mdi-pencil</VIcon>
                </VBtn>
                <VBtn
                  icon
                  variant="plain"
                  class="action-btn"
                  @click="onDelete(item)"
                >
                  <VIcon color="red">mdi-delete</VIcon>
                </VBtn>
              </div>
            </template>
            </VDataTable>
          </VCardItem>
        </VCard>
    </VContainer>
  </VMain>
</VApp>
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
