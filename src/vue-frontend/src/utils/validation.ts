// validation.ts
export const useValidator = (t: Function) => ({
  required: (value: any) =>
    value !== null &&
    value !== undefined &&
    value !== "" &&
    (!Array.isArray(value) || value.length > 0) // 配列の空チェックを追加
      ? true
      : t("validation.required_input"),

  emailFormat: (value: string) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) || !value
      ? true
      : t("validation.email_format"),

  checkLength: (min: number, max: number) => (value: string) => {
    if (value.length < min || value.length > max) {
      return t("validation.length_between", { n: min, m: max });
    }
    return true;
  },

  halfSize: (value: string) => {
    const halfSizeRegex = /^[\x20-\x7E]+$/;
    if (!halfSizeRegex.test(value)) {
      return t("validation.half_size");
    }
    return true;
  },

  checkFurigana: (value: string) => {
    const furiganaRegex = /^[ぁ-ゖァ-ヴーｧ-ﾝﾞﾟ]+$/u;
    if (!furiganaRegex.test(value)) {
      return t("validation.furigana_format");
    }
    return true;
  },

  checkEqual: (password: string) => (value: string) => {
    if (password != value) {
      return t("validation.password_confirm");
    }
    return true;
  },
  checkDateRange: (startDate: string) => (endDate: string) => {
    const start = new Date(startDate);
    const end = new Date(endDate);

    if (start > end) {
      return t("validation.invalid_date_range"); // 開始日が終了日より未来
    }

    return true;
  },
  validateNoWeekend: (value: string) => {
    if (!value) return true; // 空の状態ではバリデーションを通す
    const day = new Date(value).getDay();

    if (day === 0 || day === 6) {
      return t("validation.invalid_date"); // i18n のエラーメッセージ
    }

    return true;
  },
});
