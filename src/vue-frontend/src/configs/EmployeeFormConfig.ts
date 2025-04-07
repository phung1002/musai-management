import { IEmployee } from "@/types/type";
import { ERole } from "@/constants/role";

export const roles = Object.keys(ERole).map((key) => ({
  title: key,
  value: ERole[key as keyof typeof ERole],
}));

export const genders = [
  { value: "male", title: "male" },
  { value: "female", title: "female" },
];

export const defaultEmployee: IEmployee = {
  id: null,
  username: "",
  employeeId: "",
  email: "",
  password: "",
  fullName: "",
  fullNameFurigana: "",
  birthday: null,
  roles: [],
  department: "",
  workPlace: "",
  joinDate: null,
  gender: "",
};

export const formRules = (validator: any, isEdit: boolean) => ({
  username: [
    validator.required,
    validator.halfSize,
    validator.checkLength(5, 20),
  ],
  employeeId: [validator.required, validator.checkNumber, validator.halfSize],
  email: [
    validator.required,
    validator.checkLength(5, 30),
    validator.emailFormat,
  ],
  password: isEdit
    ? [
        (value: string) => !value || validator.halfSize(value),
        (value: string) => !value || validator.checkLength(6, 20)(value),
      ]
    : [validator.required, validator.halfSize, validator.checkLength(6, 20)],
  fullName: [validator.required, validator.checkLength(2, 50)],
  fullNameFurigana: [
    validator.required,
    validator.checkFurigana,
    validator.checkLength(2, 50),
  ],
});
