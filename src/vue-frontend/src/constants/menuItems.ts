import { ERole } from "@/constants/role";
import { useI18n } from "vue-i18n";

export function getMenuItems(t: ReturnType<typeof useI18n>["t"]) {
  return  [
    { type: "divider", roles: [ERole.ADMIN] },
    { type: "subheader", title: t("roles.ADMIN"), roles: [ERole.ADMIN] },
    {
      title: t("employee_management"),
      props: {
        prependIcon: "mdi-account-box-multiple-outline",
        link: true,
        to: "/admin/employees",
        exact: false,
      },
      value: "/admin/employees",
      roles: [ERole.ADMIN],
    },
    {
      title: t("leave_management"),
      props: {
        prependIcon: "mdi-calendar-star-outline",
        link: true,
        to: "/admin/leave-management",
        exact: false,
      },
      value: "/admin/leave-management",
      roles: [ERole.ADMIN],
    },
    { type: "divider", roles: [ERole.MANAGER] },
    { type: "subheader", title: t("roles.MANAGER"), roles: [ERole.MANAGER] },
    {
      title: t("employee_leave_management"),
      props: {
        prependIcon: "mdi-badge-account-horizontal-outline",
        link: true,
        to: "/manager/employee-leave-management",
        exact: true,
      },
      value: "/manager/employee-leave-management",
      roles: [ERole.MANAGER],
    },
    {
      title: t("request_confirm"),
      props: {
        prependIcon: "mdi-message-check-outline",
        link: true,
        to: "/manager/request-confirm",
        exact: true,
      },
      value: "/manager/request-confirm",
      roles: [ERole.MANAGER],
    },
    { type: "divider", roles: [ERole.MEMBER] },
    { type: "subheader", title: t("roles.MEMBER"), roles: [ERole.MEMBER] },
    {
      title: t("leave_request"),
      props: {
        prependIcon: "mdi-email-arrow-right-outline",
        link: true,
        to: "/member/leave-applications",
        exact: true,
      },
      value: "/member/leave-applications",
      roles: [ERole.MEMBER],
    },
    { type: "divider" },
    {
      title: t("submit_document"),
      props: {
        prependIcon: "mdi-invoice-text-multiple-outline",
        link: true,
        to: "/document",
        exact: true,
      },
      value: "/document",
      roles: [ERole.MEMBER, ERole.MANAGER, ERole.ADMIN],
    },
    {
      title: t("calendar"),
      props: {
        prependIcon: "mdi-calendar-month-outline",
        link: true,
        to: "/calendar",
        exact: true,
      },
      value: "/calendar",
      roles: [ERole.MEMBER, ERole.MANAGER, ERole.ADMIN],
    },
    {
      title: t("change_password"),
      props: {
        prependIcon: "mdi-account-convert",
        link: true,
        to: "/change-password",
        exact: true,
      },
      value: "/change-password",
      roles: [ERole.MEMBER, ERole.MANAGER, ERole.ADMIN],
    },
  ];
}
