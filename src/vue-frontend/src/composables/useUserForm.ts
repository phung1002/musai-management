import { IUser } from '@/types/type';
import { createUser, getAllUsers } from '@/api/user';
import { defaultUser } from '../configs/userFormConfig';
import { useSnackbar } from '@/composables/useSnackbar';

const { showSnackbar } = useSnackbar();

const formatDate = (date: string | null) => date ? new Date(date).toISOString() : null;

export const handleSubmit = async (formValid: boolean, formModel: IUser, submiting: any) => {
  if (!formValid) {
    showSnackbar('message.add_failure', 'error');
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
    showSnackbar('message.add_success', 'success');
    getAllUsers();
    Object.assign(formModel, defaultUser);
  } catch (error) {
    showSnackbar('message.add_failure', 'error');
  } finally {
    submiting.value = false;
  }
};

export const showErrorSnackbar = (snackbar: any, message: string) => {
  snackbar.value.isColorSnackbarVisible = true;
  snackbar.value.color = 'error';
  snackbar.value.message = message;
};

export const showSuccessSnackbar = (snackbar: any) => {
  snackbar.value.isColorSnackbarVisible = true;
  snackbar.value.color = 'success';
  snackbar.value.message = 'message.add_success';
};

