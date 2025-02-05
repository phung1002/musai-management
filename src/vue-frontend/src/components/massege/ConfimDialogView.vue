<!--確認　メッセージ -->
<script lang="ts" setup>
import { ref, defineProps, defineEmits } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const props = defineProps({
  title: {
    type: String,
    default: '確認',
  },
  message: {
    type: String,
    default: '本当に実行しますか？',
  },
  isVisible: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['update:isVisible', 'confirmed']);

const visible = ref(props.isVisible);

const onConfirm = () => {
  emit('confirmed');
  visible.value = false;
  emit('update:isVisible', false);
};
const onCancel = () => {
  visible.value = false;
  emit('update:isVisible', false);
};
</script>

<template>
  <VCard width="640px">
    <VToolbar tag="div">
      <VToolbarTitle style="color: red;">
        <VIcon icon= "mdi-alert"/>{{ title }}
      </VToolbarTitle>
      <!-- <VBtn icon="mdi-close" @click="handleCancel" ></VBtn> -->
    </VToolbar>
    <!-- 確認内容を表示 -->
    <VCardText style="color: black;">{{ message }}</VCardText>
    <VDivider />
    <VCardActions>
      <VBtn type="submit" variant="outlined" color="primary" @click="onConfirm">{{ t('yes') }}</VBtn>
      <VBtn @click="onCancel" >{{ t('no') }}</VBtn>
    </VCardActions>
  </VCard>
</template>
