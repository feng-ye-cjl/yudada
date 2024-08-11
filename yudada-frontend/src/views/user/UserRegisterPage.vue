<template>
  <div id="userRegisterPage">
    <h2 style="margin-bottom: 16px">用户注册</h2>
    <a-form
      :model="form"
      :style="{ width: '480px', margin: '0 auto' }"
      label-align="left"
      auto-label-width
      @submit="handleSubmit"
    >
      <a-form-item field="userAccount" label="账号">
        <a-input v-focus v-model="form.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item field="userPassword" tooltip="密码不小于 8 位" label="密码">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
        />
      </a-form-item>
      <a-form-item
        field="checkPassword"
        tooltip="确认密码不小于 8 位"
        label="确认密码"
      >
        <a-input-password
          v-model="form.checkPassword"
          placeholder="请输入确认密码"
        />
      </a-form-item>
      <a-form-item>
        <div style="display: flex; width: 100%; justify-content: space-between">
          <a-button type="primary" html-type="submit" style="width: 120px">
            注册
          </a-button>
          <a-link href="/user/login">老用户登录</a-link>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { userRegisterUsingPost } from "@/api/userController";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import vFocus from "@/directives/VFocus";

const router = useRouter();

const form = ref({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
});
const handleSubmit = async () => {
  console.log("用户注册");
  // 用户登录
  const res = await userRegisterUsingPost(form.value);
  console.log("login res = ", res);
  if (res.data.code === 0) {
    Message.success("注册成功");
    await router.push({ path: "/user/login" });
  } else {
    Message.error(res.data.message as string);
  }
};
</script>
