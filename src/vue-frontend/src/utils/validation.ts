// validation.ts
export const userValidator = (t: Function) => ({
  required: (value: string) => (value && value.length ? true : t('validation.required_input')),

  emailFormat: (value: string) =>
    (/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) || !value ? true : t('validation.email_format')),

  checkLength: (min: number, max: number) => (value: string) => {
    if (value.length < min || value.length > max) {
      return t('validation.length_between', { n : min , m : max});
    }
    return true;
  },

  halfSize: (value: string) => {
    const halfSizeRegex = /^[\x20-\x7E]+$/;
    if (!halfSizeRegex.test(value)) {
      return t('validation.half_size');
    }
    return true;
  },

  checkFurigana: (value: string) => {
    const furiganaRegex = /^[ぁ-ゖァ-ヴーｧ-ﾝﾞﾟ]+$/u;
    if (!furiganaRegex.test(value)) {
      return t('validation.furigana_format');
    }
    return true;
  },

  checkEqual: (password: string) => (value: string) => {
    if(password != value) {
      return t('validation.password_confirm');
    }
    return true;
  }
});
