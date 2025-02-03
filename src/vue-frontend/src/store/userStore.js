import { Roles } from '@/constants/role';
import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    username: '',
    fullName: '',
    roles: [],
    authenticated: false
  }),

  // Getters
  getters: {
    isAdmin() {
      return this.roles.some(role => role.value === Roles.ADMIN);
    },
    isManager() {
      return this.roles.some(role => role.value === Roles.MANAGER);
    },
    isMember() {
      return this.roles.some(role => role.value === Roles.MEMBER);
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
