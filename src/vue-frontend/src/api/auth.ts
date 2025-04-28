import axiosIns from "@/plugins/axios";
import { useEmployeeStore } from "@/store/employeeStore";

// ログインに必要なパラメータの型定義
interface LoginParams {
  employeeId: string;
  password: string;
}

// ログインAPIを呼び出す関数
export async function login(params: LoginParams): Promise<void> {
  try {
    // ログインAPIを呼び出し、成功した場合はユーザープロフィールを取得
    await axiosIns.post("/auth/login", params);
    // ログイン成功後、ユーザープロフィールを取得
    await fetchEmployeeProfile();
  } catch (error: any) {
    // エラーが発生した場合、エラーメッセージを表示
    console.error("Login failed:", error.response?.data || error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// ログイン後またはページリロード後に現在のユーザープロフィールを取得する関数
export async function fetchEmployeeProfile(): Promise<void> {
  const employeeStore = useEmployeeStore();
  try {
    // プロフィールAPIを呼び出し、成功したらユーザー情報をストアに保存
    const response = await axiosIns.get("/auth/profile");
    const data = response.data;

    // ユーザー情報をストアに保存
    employeeStore.setAuthenticated(true);
    employeeStore.setId(data.id);
    employeeStore.setRoles(data.roles);
    employeeStore.setEmployeeId(data.employeeId);
    employeeStore.setFullName(data.fullName);
    employeeStore.setGender(data.gender);
  } catch (error: any) {
    // エラーが発生した場合、エラーメッセージを表示
    console.error("Failed to fetch employee profile:", error);
    // もしエラーが401なら、セッションが切れている可能性があるので、ログアウト処理を実行
    if (error.response?.status === 401) {
      await logout();
    }
  }
}

// ログアウトAPIを呼び出す関数
export async function logout(): Promise<void> {
  const employeeStore = useEmployeeStore();
  try {
    // ログアウトAPIを呼び出し
    await axiosIns.post("/auth/logout");
    // ログアウト後、ユーザー情報をリセット
    employeeStore.setId("");
    employeeStore.setAuthenticated(false);
    employeeStore.setRoles([]);
    employeeStore.setEmployeeId("");
    employeeStore.setFullName("");
    employeeStore.setGender("");
  } catch (error) {
    // エラーが発生した場合、エラーメッセージを表示
    console.error("Logout failed:", error);
  }
}

// トークンが有効かどうかを検証するAPIを呼び出す関数
export async function validate() {
  try {
    // トークン検証APIを呼び出し、成功した場合はレスポンスを返す
    const response = await axiosIns.get("/auth/validate");
    return response;
  } catch (error : any) {
    // エラーが発生した場合、エラーメッセージを表示
    console.error("Token validation failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// トークンを検証するヘルパー関数
export async function validateToken() {
  try {
    // トークンが有効か検証し、有効であればtrueを返す
    await validate();
    return true;
  } catch (error) {
    // 検証に失敗した場合、falseを返す
    return false;
  }
}

// ログアウト処理を行い、ログイン画面にリダイレクトするヘルパー関数
export async function handleLogout() {
  // ログアウトを実行
  await logout();
  // ログイン画面にリダイレクト
  window.location.href = "/login";
}

// ユーザープロフィールを取得する関数（レスポンスのデータ部分だけを返す）
export async function profile() {
  try {
    // プロフィールAPIを呼び出し、成功した場合はデータを返す
    const response = await axiosIns.get("/auth/profile");
    return response.data;
  } catch (error :any) {
    // エラーが発生した場合、エラーメッセージを表示
    console.error("Failed to fetch profile:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
