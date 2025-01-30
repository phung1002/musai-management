import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    username: '',
    fullName: '',
    roles: [
      { title: 'Admin', value: 'ROLE_ADMIN' },
      { title: 'Management', value: 'ROLE_MANAGER' },
      { title: 'Member', value: 'ROLE_MEMBER' }
    ],
    authenticated: false
  }),

  // Getters
  getters: {
    isAdmin() {
      return this.roles.some(role => role.value === 'ROLE_ADMIN');
    },
    isManager() {
      return this.roles.some(role => role.value === 'ROLE_MAMAGER');
    },
    isMember() {
      return this.roles.some(role => role.value === 'ROLE_MEMBER');
    }
  },
  // Actions
  actions: {
    setToken(token) {
      this.token = token;
    },
    setUsername(username) {
      this.username = username;
    },
    setRoles(roles) {
      this.roles = roles;
    },
    setAuthenticated(auth) {
      this.authenticated = auth;
    },
    setFullName(fullName) {
      this.fullName = fullName;
    }
  },
  persist: {
    key: 'user-store',
    storage: window.localStorage,
  },
});
