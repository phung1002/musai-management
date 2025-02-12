<!-- 書類提出 画面　-->
<script setup lang="ts">
import { ref, reactive } from 'vue';
import AppSidebar from '@/components/layout/AppSidebar.vue';
import AppToolbar from '@/components/layout/AppToolbar.vue';
import UploadForm from '@/components/upload/UploadForm.vue';
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
const applyFrom  = ref(false);
const isDialogVisible = ref(false);
const editForm  = ref(false);

// // テーブル　ヘッダー
const headers = reactive([
  { title: t('number'), key: 'number' },
  { title: t('title'), key: 'title' },
  { title: t('submit_date'), key: 'title' },
  { title: t('action'), key: 'action' }
]);

const handleCreateItem = () => {
  applyFrom.value = true;
};
const handleDeleteItem = (id: number) => {
  isDialogVisible.value = true;
  console.log('delete', id);
};
const handleEditItem = (id: number) => {
  editForm.value = true;
  console.log('edit', id);
};
const onDeleted = () => {
  console.log("削除されました");
  // ここに処理を追加
};

const pagination = reactive({
  page: 1,
  pageSize: 10
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
        <div class="page-wrapper">
            <VRow>
              <VCol cols="12">
                <VContainer class="app-container">
                  <VCard flat elevation="0" >
                    <VToolbar tag="div">
                      <VToolbarTitle><VIcon icon= "mdi-folder-upload"/> {{ t('submit_document') }} </VToolbarTitle>
                      <!-- アップロード　ボタン-->
                      <VCardActions>
                        <VSpacer />
                        <VBtn color="primary" @click="handleCreateItem" variant="elevated"><v-icon icon="mdi-plus" start></v-icon>{{ t('upload') }}
                          <VDialog v-model="applyFrom" width="auto" eager>
                            <UploadForm @form:cancel="applyFrom = false" />
                          </VDialog>
                        </VBtn>
                      </VCardActions>
                    </VToolbar>
                    <VDivider />
                    <!-- 申請情報　表示 -->
                    <VCardItem>
                      <VDataTable
                        :items-per-page-text="t('items_per_page')"
                        :headers="headers"
                      >
                        <!-- 表示　番号設定  -->
                        <template v-slot:item.no="{ index }">
                          {{ index + 1 }}
                        </template>
                        <!-- アクション　設定  -->
                        <template v-slot:item.action="{ item }">
                          <div class="action-buttons">
                            <VBtn
                              icon
                              variant="plain"
                              class="action-btn"
                              @click="handleEditItem"
                            >
                              <VIcon color="blue">mdi-pencil</VIcon>
                              <VDialog v-model="editForm" width="auto" eager>
                              </VDialog>
                            </VBtn>
                            <VBtn
                              icon
                              variant="plain"
                              class="action-btn"
                              @click="handleDeleteItem"
                            >
                              <VIcon color="red">mdi-delete</VIcon>
                              <VDialog v-model="isDialogVisible" width="auto" eager>
                                <ConfimDialogView
                                :title="t('confirm')"
                                :message="t('delete_confirm_message')"
                                :isVisible="isDialogVisible"
                                @update:isVisible="isDialogVisible = $event"
                                @confirmed="onDeleted"
                              />
                              </VDialog>
                            </VBtn>
                          </div>
                        </template>
                      </VDataTable>
                    </VCardItem>                 
                  </VCard>
                </VContainer>
              </VCol>
            </VRow>
          <RouterView />
        </div>
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
