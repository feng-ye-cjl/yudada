<template>
  <a-row id="globalHeader" align="center" :wrap="false">
    <!--自动适应-->
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :selected-keys="selectKeys"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="titleBar">
            <img class="logo" src="../assets/logo.png" />
            <div class="title">鱼答答</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in routes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <!--固定100px-->
    <a-col flex="100px">
      <div>
        <a-button type="primary" href="/user/login">登录</a-button>
      </div>
    </a-col>
  </a-row>
</template>
<script lang="ts" setup>
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { ref } from "vue";

// 点击菜单栏跳转路由
const router = useRouter();
const doMenuClick = (key: string) => {
  router.push({ path: key });
};

// tab栏选中菜单高亮
const selectKeys = ref(["/"]);
// 路由守卫：在每次路由跳转后，更新选中菜单高亮
router.afterEach((to, from, failure) => {
  selectKeys.value = [to.path];
});
</script>
<style scoped>
#globalHeader {
  box-sizing: border-box;
  width: 100%;

  .titleBar {
    display: flex;
    align-items: center;

    .title {
      margin-left: 16px;
      color: black;
    }

    .logo {
      height: 45px;
    }
  }
}
</style>
