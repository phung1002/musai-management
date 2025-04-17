import { ERole } from "@/constants/role";
import { defineStore } from "pinia";

export const useEmployeeStore = defineStore("user", {
  state: () => ({
    id: "",
    employeeId: "",
    fullName: "",
    roles: [],
    gender: "",
    authenticated: false,
  }),

  // Getters
  getters: {
    isAdmin() {
      return this.roles.some((role) => role.value === ERole.ADMIN);
    },
    isManager() {
      return this.roles.some((role) => role.value === ERole.MANAGER);
    },
    isMember() {
      return this.roles.some((role) => role.value === ERole.MEMBER);
    },
  },
  // Actions
  actions: {
    setId(id) {
      this.id = id;
    },
    setUsername(username) {
      this.username = username;
    },
    setEmployeeId(employeeId) {
      this.employeeId = employeeId;
    },
    setRoles(roles) {
      this.roles = roles;
    },
    setGender(gender) {
      this.gender = gender;
    },
    setAuthenticated(auth) {
      this.authenticated = auth;
    },
    setFullName(fullName) {
      this.fullName = fullName;
    },
  },
  persist: {
    key: "user-store",
    storage: window.localStorage,
  },
});
