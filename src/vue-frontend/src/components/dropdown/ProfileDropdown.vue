<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { reactive, computed } from 'vue';
import { useUserStore } from '@/store/userStore';
import LogOutConrimView from '@/components/massege/LogOutConrimView.vue';
import ProfileDropdown from '@/components/profile/VerticalProfileCard.vue';
import { ref } from 'vue';

const showDialog = ref(false);
const showProfileView = ref(false);
const userStore = useUserStore();
const userRoles = computed(() => userStore.roles || []);
const { t } = useI18n();
const router = useRouter();
const handleSubmit = () => {
  showDialog.value = true;
};
const showProfile = () => {
  showProfileView.value = true;
};

</script>

<template>
  <VMenu :close-on-content-click="false">
    <template v-slot:activator="{ props }">
      <VCardTitle>user</VCardTitle>
      <VBtn class="custom-hover-primary mr-3" variant="text" v-bind="props" icon>
        <VBadge dot color="success" offset-x="0" offset-y="3">
          
          <VAvatar size="35">
          
            <img src="@\assets\images\users\avatar-4.png" height="35" alt="user" title="{{ username }}"/>
          </VAvatar>
        </VBadge>
      </VBtn>
    </template>
    <VSheet rounded="md" width="200" elevation="10" class="mt-2">
      <div class="pt-4 pb-4 px-5 text-center">
        <VBtn color="primary" variant="outlined" block @click="showProfile" ><v-icon icon="mdi-account" start></v-icon>{{ t('profile') }}</VBtn>
        <VDialog v-model="showProfileView" width="auto" eager>
          <ProfileDropdown  @form:cancel="showProfileView = false" />
        </VDialog>
      </div>
      <VDivider />
      <div class="pt-4 pb-4 px-5 text-center">
      <VBtn color="primary" block @click="handleSubmit" class="mr-4"><v-icon icon="mdi-logout" start></v-icon>{{ t('logout') }}</VBtn>
      <VDialog v-model="showDialog" width="auto" eager>
        <LogOutConrimView  @form:cancel="showDialog = false" />
      </VDialog>
    </div>
    </VSheet>
  </VMenu>
</template>