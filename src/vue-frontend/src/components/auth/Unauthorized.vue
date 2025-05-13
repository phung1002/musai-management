<!-- 権限 認証されてない　画面-->
<script setup lang="ts">
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { handleLogout } from "@/api/auth";
import logoImg from "@/assets/images/logo.png";
const { t } = useI18n();
const router = useRouter();
const backHome = () => {
  router.push({
    path: "/calendar",
  });
};
</script>

<template>
  <div class="page-notfound">
    <div class="notfound pr-4">
      <div class="notfound-emoji"></div>
      <VImg class="logo-img pb-4" :src="logoImg" alt="logo" max-width="200px" />
      <h2>{{ t("message.unauthorized") }}</h2>
      <p>{{ t("message.unauthorized_description") }}</p>
      <VBtn @click="backHome()" class="mb-2 mr-4">{{ t("back_home") }}</VBtn>
      <VBtn @click="handleLogout()" class="mb-2">{{ t("back_login") }}</VBtn>
    </div>
  </div>
</template>
<style lang="scss">
.page-notfound {
  position: relative;
  height: 100vh;
  .notfound {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    max-width: 560px;
    width: 100%;
    padding-left: 180px;
    line-height: 1.1;
    .notfound-emoji {
      position: absolute;
      left: 0;
      top: 0;
      display: inline-block;
      width: 140px;
      height: 140px;
      background-image: url(/assets/images/emoji.webp);
      background-size: cover;
      &::before {
        content: "";
        position: absolute;
        width: 100%;
        height: 100%;
        -webkit-transform: scale(2.4);
        -ms-transform: scale(2.4);
        transform: scale(2.4);
        border-radius: 50%;
        background-color: #f2f5f8;
        z-index: -1;
      }
    }
    h1 {
      font-size: 65px;
      font-weight: 700;
      margin-top: 0;
      margin-bottom: 10px;
      text-transform: uppercase;
    }
    h2 {
      font-size: 21px;
      font-weight: 300;
      margin: 0;
      text-transform: uppercase;
    }
    p {
      line-height: 1.2;
      margin: 20px 0;
    }
  }
}
</style>
