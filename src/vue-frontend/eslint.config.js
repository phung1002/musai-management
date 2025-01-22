import js from '@eslint/js';
import pluginVue from 'eslint-plugin-vue';

export default [
  // file need to check
  {
    files: ['**/*.{js,mjs,jsx,ts,tsx,vue}'], // Add TypeScript
    rules: {
      ...js.configs.recommended.rules, // Use recommended JavaScript's rules
      ...pluginVue.configs['flat/essential'].rules, // Use basic Vue's rules
      ...typescript.configs.recommended.rules,
    },
    languageOptions: {
      parserOptions: {
        ecmaVersion: 'latest', // Use new ECMAScript version
        sourceType: 'module',
        ecmaFeatures: {
          jsx: true,
        },
      },
    },
  },
  // Ignores file/folder don't need to check lint
  {
    ignores: ['**/dist/**', '**/dist-ssr/**', '**/coverage/**'],
  },
];
