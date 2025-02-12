import { IAccessToken, ILeaveRequest } from '@/types/type';
import axiosIns from '@/plugins/axios';

export async function getRequstLists(): Promise<ILeaveRequest[]> {
  try {
    const response = await axiosIns.get<ILeaveRequest[]>('/user/requsts');
    return response.data;
  } catch (error) {
    console.error('List user failed:', error);
    throw error;
  }
}
// PDF　ファイルをアップロードAPI呼び出し

// export async function uploadPdf(file: File): Promise<string> {
//   try {
//     const formData = new FormData();
//     console.log(file);
//     formData.append('file', file);
//     const response = await axiosIns.post('/leave/request/upload', formData, {
//       headers: {
//         'Content-Type': 'multipart/form-data',
//       },
//     });
//     return response.data.url;
//   } catch (error: any) {
//     throw new Error(error.response?.data?.message || 'PDF アップロードに失敗しました');
//   }
// }
// 📤 PDF アップロード関数
export async function uploadPdf(file: File): Promise<string> {
  try {
    const formData = new FormData();
    formData.append('file', file);
    const response = await axiosIns.post('/request/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });

    return response.data.url; // アップロード成功時のURLを返す
  } catch (error: any) {
    throw error; // アップロード失敗時は null を返す
  }
};