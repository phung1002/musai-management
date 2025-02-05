import { defineStore } from 'pinia';
export const useLeaveTypesStore = defineStore('leave_types', {
  state: () => ({
    token: '',
    userid: '',
    paid_leave: [
      { title: 'full_day_leave', value: 'FULL_DAY_LEAVE' },
      { title: 'half_day_leave', value: 'HALF_DAY_LEAVE' }
    ],
    public_leave: [
      { title: 'special_day_leave', value: 'SPECIAL_DAY_LEAVE' },
      { title: 'special_occasions_leave', value: 'SPECIAL_OCCASIONS_LEAVE' }
    ],
    special_day_leave: [
      { title: 'bbq', value: 'BBQ' },
      { title: 'medical_test', value: 'MEDICAL_TEST' },
      { title: 'summer_leave', value: 'SUMMER_LEAVE' },
      { title: 'trip_leave', value: 'TRIP_LEAVE' }
      
    ],
    special_occasions_leave: [
      { title: 'own_wedding', value: 'OWN_WEDDING' },
      { title: 'child_wedding', value: 'CHILD_WEDDING' },
      { title: 'wife_delivery', value: 'WIFE_DELIVERY' },
      { title: 'family_funereal', value: 'FAMILY_FUNEREAL' },
      { title: 'other', value: 'OTHER' }
    ]
  }),
  // Getters
  getters: {
    getPaidLeave(state) {
      return state.paid_leave;
    },
    getPublicLeave(state) {
      return state.public_leave;
    },
    getSpecialDayLeave(state) {
      return state.special_day_leave;
    },
    getSpecialOccasionsLeave(state) {
      return state.special_occasions_leave;
    },
    getUserId(state) {
      return state.userid;
    },
    getAccessToken(state) {
      return state.token;
    },
    hasPaidLeave: (state) => (paid_leave) => {
      return state.paid_leave.includes(paid_leave);
    },
    hasPublicLeave: (state) => (public_leave) => {
      return state.public_leave.includes(public_leave);
    },
    hasSpecialDayLeave: (state) => (special_day_leave) => {
      return state.special_day_leave.includes(special_day_leave);
    },
    hasSpecialOccasionsLeave: (state) => (special_occasions_leave) => {
      return state.special_occasions_leave.includes(special_occasions_leave);
    },
  },
  // Actions
  actions: {
    setToken(token) {
      this.token = token;
    },
    setUserId(userid) {
      this.username = userid;
    },
    setPaidLeave(paid_leave) {
      this.paid_leave = paid_leave;
    },
    setPublicLeave(public_leave) {
      this.public_leave = public_leave;
    },
    setSpecialDayLeave(special_day_leave) {
      this.special_day_leave = special_day_leave;
    },
    setSpecialOccasionsLeave(special_occasions_leave) {
      this.special_occasions_leave = special_occasions_leave;
    },
  },
  persist: {
    key: 'leave-types-store',
    storage: window.localStorage,
  },
});
