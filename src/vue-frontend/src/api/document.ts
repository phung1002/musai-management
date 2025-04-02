import axiosIns from "@/plugins/axios";
import { IDocument } from "@/types/type";

// ドキュメントアップロードAPI呼び出し
export async function uploadDocument(file: File): Promise<IDocument> {
  const formData = new FormData();
  formData.append("file", file);
  try {
    const response = await axiosIns.post("/documents/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return  response.data;
  } catch (error : any) {
    console.error("アップロード失敗:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
// ドキュメント取得API呼び出し
export async function getDocuments(): Promise<IDocument[]> {
  try {
    const response = await axiosIns.get<IDocument[]>("/documents/all");
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
export async function getDocumentsOfMember(
  userId?: number
): Promise<IDocument[]> {
  try {
    let response = await axiosIns.get<IDocument[]>(`/documents`);
    return response.data;
  } catch (error) {
    console.error("List documents failed:", error);
    throw error;
  }
}
// ドキュメント削除API呼び出し
export async function deleteDocument(id: number): Promise<void> {
  try {
    await axiosIns.delete(`/documents/${id}`);
  } catch (error : any) {
    console.error("Delete document failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
// ドキュメントプレビューAPI
export async function getDocumentPreview(id: number): Promise<{ file: File }> {
  try {
    const response = await axiosIns.get(`/documents/preview/${id}`, {
      responseType: "blob",
    });

    // 取得したFileオブジェクトを返す
    const file = new File([response.data], "fileName.pdf", {
      type: "application/pdf",
    });

    return {
      file: file, // Fileオブジェクトを返す
    };
  } catch (error: any) {
    console.error("Document preview failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
