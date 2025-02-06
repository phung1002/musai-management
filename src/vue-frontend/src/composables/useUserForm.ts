import { IUser } from '@/types/type';
import { createUser, getAllUsers } from '@/api/user';
import { defaultUser } from '../configs/userFormConfig';
import { useSnackbar } from '@/composables/useSnackbar';

const { showSnackbar } = useSnackbar();

const formatDate = (date: string | null) => date ? new Date(date).toISOString() : null;

export const handleSubmit = async (formValid: boolean, formModel: IUser, submiting: any) => {
  if (!formValid) {
    showSnackbar('validation_error', 'error');
    return;
  }
  const payload: IUser = {
    ...formModel,
    birthday: formatDate(formModel.birthday),
    joinDate: formatDate(formModel.joinDate),
  };
  submiting.value = true;
  try {
    await createUser(payload);
    showSnackbar('add_success', 'success');
    getAllUsers();
    Object.assign(formModel, defaultUser);
  } catch (error) {
    showSnackbar('add_failure', 'error');
  } finally {
    submiting.value = false;
  }
};

