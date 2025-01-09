<template>
  <div>
    <h1>Home Page</h1>
    <p>Welcome, {{ username }}</p>
  </div>
</template>

<script>
import { useUserStore } from '@/store/userStore';

export default {
  name: 'HomeView',
  setup() {
    const userStore = useUserStore();
    return { username: userStore.username };
  },
};
</script>
