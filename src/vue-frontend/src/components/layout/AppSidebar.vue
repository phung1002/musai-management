<script setup lang="ts">
import logoImg from "@/assets/images/logo.png";
import { computed, defineProps } from "vue";
import { useI18n } from "vue-i18n";
import { useEmployeeStore } from "@/store/employeeStore";
import { getMenuItems } from "@/constants/menuItems";
import { ref } from "vue";
import { watch } from "vue";

const props = defineProps<{ drawer: boolean }>();
const emit = defineEmits(["update:drawer"]);
const localDrawer = ref(props.drawer);

watch(() => props.drawer, (val) => (localDrawer.value = val));
watch(localDrawer, (val) => emit("update:drawer", val));

const employeeStore = useEmployeeStore();
const filteredItems = computed(() => {
  return items.filter((item) => {
    if (!item.roles) return true;
    return item.roles.some((role) => employeeRoles.value.includes(role));
  });
});
const employeeRoles = computed(() => employeeStore.roles || []);
const { t } = useI18n();
const items = getMenuItems(t);
</script>

<template>
  <VNavigationDrawer
  v-model="localDrawer"
    rail-width="256"
    :rail="false"
    :border="true"
    :elevation="1"
  >
    <div>
      <VToolbar color="transparent" class="mr-2">
        <VImg class="logo-img" :src="logoImg" alt="logo" contain></VImg>
      </VToolbar>
    </div>
    <div class="app-drawer__inner">
      <VList
        :items="filteredItems"
        color="primary"
        class="menu-list pt-0"
        nav
        :slim="true"
      />
    </div>
  </VNavigationDrawer>
</template>

<style lang="scss" scoped>
.logo-img {
  margin: 4px 2px !important;
  // width: 60px !important;
}
.btn-collapse {
  position: absolute;
  inset-block-start: 50%;
  transform: translateY(-50%);
}
.menu-list {
  .v-list-subheader__text {
    text-transform: uppercase;
  }

  .v-list-item__prepend > .v-icon {
    margin-inline-end: 16px;
  }

  .v-list-item-title {
    text-transform: capitalize;
  }
}
.v-navigation-drawer--border {
  border-width: inherit;
  box-shadow: none;
}
</style>
