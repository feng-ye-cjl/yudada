<template>
  <div id="userLoginPage">
    <h2 style="margin-bottom: 16px">用户登录</h2>
    <a-form
      :model="form"
      :style="{ width: '480px', margin: '0 auto' }"
      label-align="left"
      auto-label-width
    >
      <a-form-item field="userAccount" label="账号">
        <a-input v-focus v-model="form.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item field="userPassword" tooltip="密码不小于 8 位" label="密码">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
          @keydown.enter="handleSubmit"
        />
      </a-form-item>
      <a-form-item>
        <div
          style="
            display: flex;
            width: 100%;
            align-items: center;
            justify-content: space-between;
          "
        >
          <a-button type="primary" @click="handleSubmit" style="width: 120px">
            登录
          </a-button>
          <a-link href="/user/register">新用户注册</a-link>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { userLoginUsingPost } from "@/api/userController";
import API from "@/api";
import { useLoginUserStore } from "@/store/userStore";
import vFocus from "@/directives/VFocus";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";

// userStore
const userStore = useLoginUserStore();
// 路由
const router = useRouter();

const form = ref({
  userAccount: "",
  userPassword: "",
} as API.UserLoginRequest);

/**
 * 提交
 */
const handleSubmit = async () => {
  // console.log("用户登录");
  // 用户登录
  const res = await userLoginUsingPost(form.value);
  // console.log("login res = ", res);
  if (res.data.code === 0) {
    console.log("login success");
    // 获取登录用户
    await userStore.fetchLoginUser();
    Message.success("登录成功");
    await router.push({ path: "/", replace: true });
  } else {
    Message.error(res.data.message as string);
  }
};
</script>
