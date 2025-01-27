import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    username: '',
    permissions: [],
    roles: [
      { title: 'Admin', value: 'ROLE_ADMIN' },
      { title: 'Management', value: 'ROLE_MANAGER' },
      { title: 'Member', value: 'ROLE_MEMBER' }
    ],
    authenticated: false
  }),

  // Getters
  getters: {
    getRoles(state) {
      return state.roles;
    },
    getUsername(state) {
      return state.username;
    },
    getAccessToken(state) {
      return state.token;
    },
    hasRole: (state) => (role) => {
      return state.roles.includes(role);
    },
    getAuthenticated(state) {
      return state.authenticated;
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
    }
  },
  persist: {
    key: 'user-store',
    storage: window.localStorage,
  },
});
