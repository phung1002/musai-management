import { IUser } from '@/types/type';
import { ERole } from '@/constants/role';

export const roles = Object.keys(ERole).map(key => ({
  title: key,
  value: ERole[key as keyof typeof ERole],
}));

export const genders = [
  { value: 'male', title: 'male' },
  { value: 'female', title: 'female' },
];

export const defaultUser: IUser = {
  id: null,
  username: '',
  email: '',
  password: '',
  fullName: '',
  fullNameFurigana: '',
  birthday: null,
  roles: [],
  department: '',
  workPlace: '',
  joinDate: null,
  gender: '',
};

export const formRules = (validator: any, formModel: IUser) => ({
  username: [validator.required, validator.halfSize, validator.checkLength(5, 20)],
  email: [validator.required, validator.halfSize, validator.checkLength(5, 30), validator.emailFormat],
  password: [validator.required, validator.halfSize, validator.checkLength(6, 20)],
  confirmPassword: [validator.checkEqual(formModel.password)],
  fullName: [validator.required, validator.checkLength(2, 50)],
  fullNameFurigana: [validator.required, validator.checkFurigana, validator.checkLength(2, 50)],
});
