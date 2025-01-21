<template>
  <div>
    <h1>Unauthorized!!!</h1>
  </div>
</template>

<script>
import { useUserStore } from '@/store/userStore';

export default {
  name: 'UnauthorizedView',
  setup() {
    const userStore = useUserStore();
    return { username: userStore.username };
  },
};
</script>
