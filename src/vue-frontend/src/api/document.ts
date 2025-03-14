import axiosIns from "axios";
import { IDocument } from "@/types/type";

// ドキュメントアップロードAPI呼び出し
export async function uploadDocument(file: File): Promise<IDocument> {
  console.log("アップロード開始 - ファイル情報:", file);

  const formData = new FormData();
  formData.append("file", file);
  console.log("フォームデータ:", formData);

  try {
    const response = await axiosIns.post("/documents/upload", formData);
    console.log("アップロード成功 - レスポンスデータ:", response.data);

    return {
      id: response.data.id,
      title: file.name,
      filePath: response.data.filePath,
      submitDate: new Date().toISOString().split("T")[0],
    };
  } catch (error) {
    console.error("アップロード失敗:", error);
    throw error;
  }
}
// ドキュメント取得API呼び出し
export async function getDocuments(): Promise<IDocument[]> {
  try {
    const response = await axiosIns.get<IDocument[]>("/documents/all");
    console.log("response.data", response.data);
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
// ドキュメント削除API呼び出し
export async function deleteDocument(id: number): Promise<void> {
  try {
    await axiosIns.delete(`/documents/${id}`);
    console.log("Delete document successfully");
  } catch (error) {
    console.error("Delete document failed:", error);
    throw error;
  }
}
// ドキュメントプレビューAPI
export async function getDocumentPreview(
  id: number
): Promise<{ filePath: File }> {
  try {
    const response = await axiosIns.get(`/documents/${id}/preview`, {
      responseType: "blob",
    });

    // 取得したFileオブジェクトを返す
    const file = new File([response.data], "fileName.pdf", {
      type: "application/pdf",
    });

    return {
      filePath: file, // Fileオブジェクトを返す
    };
  } catch (error) {
    console.error("Document preview failed:", error);
    throw error;
  }
}
// ドキュメントダウンロードAPI呼び出し
export async function downloadDocument(
  id: number,
  documentName: string
): Promise<void> {
  try {
    const response = await axiosIns.get(`/documents/${id}/download`, {
      responseType: "blob", // バイナリデータを取得
    });

    // ダウンロード処理
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", documentName); // ファイル名指定
    document.body.appendChild(link);
    link.click();
    window.URL.revokeObjectURL(url); // メモリ解放
    console.log("Download document successfully");
  } catch (error) {
    console.error("ドキュメントのダウンロードに失敗しました:", error);
    throw new Error("ドキュメントのダウンロードに失敗しました");
  }
}
