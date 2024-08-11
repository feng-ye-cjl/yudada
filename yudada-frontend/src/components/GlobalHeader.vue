<template>
  <a-row id="globalHeader" align="center" :wrap="false">
    <!--自动适应-->
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :selected-keys="selectKeys"
        @menu-item-click="doMenuClick"
        breakpoint="xl"
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
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
        <a-sub-menu
          v-if="loginUser.userRole && loginUser.userRole === accessEnum.ADMIN"
        >
          <template #title>后台管理</template>
          <a-menu-item v-for="item in adminRouter" :key="item.path">
            {{ item.name }}
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-col>
    <!--固定100px-->
    <a-col flex="100px">
      <!--设置登录按钮和用户名显示-->
      <div v-if="loginUser.id">
        {{ loginUser.userName ?? "无名" }}
        <a-tooltip content="登出">
          <icon-export
            @click="loginOut"
            style="cursor: pointer; margin-left: 10px"
          />
        </a-tooltip>
      </div>
      <div v-else>
        <a-button type="primary" href="/user/login">登录</a-button>
      </div>
    </a-col>
  </a-row>
</template>
<script lang="ts" setup>
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { computed, onMounted, ref } from "vue";
import { useLoginUserStore } from "@/store/userStore";
import { storeToRefs } from "pinia";
import checkAccess from "@/access/checkAccess";
import { userLogoutUsingPost } from "@/api/userController";
import { Message } from "@arco-design/web-vue";
import accessEnum from "@/access/accessEnum";

// 登录用户信息
const userStore = useLoginUserStore();
const { loginUser } = storeToRefs(userStore);

// 点击菜单栏跳转路由
const router = useRouter();
const doMenuClick = (key: string) => {
  router.push({ path: key });
};

// 用户退出登录
const loginOut = async () => {
  const res = await userLogoutUsingPost();
  if (res.data.code === 0) {
    Message.success("登出成功");
    await router.push({ path: "/user/login", replace: true });
  } else {
    Message.error("登出失败");
  }
};

// tab栏选中菜单高亮
const selectKeys = ref(["/"]);
// 路由守卫：在每次路由跳转后，更新选中菜单高亮
router.afterEach((to, from, failure) => {
  selectKeys.value = [to.path];
});

// 根据权限控制菜单显示隐藏,设置成computed自动计算
const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    // 根据权限过滤菜单
    if (!checkAccess(userStore.loginUser, item.meta?.access as string)) {
      return false;
    }
    // 过滤管理页面
    if (item.path.startsWith("/admin")) {
      return false;
    }
    return true;
  });
});
const adminRouter = computed(() => {
  return routes.filter((item) => {
    return item.path.startsWith("/admin");
  });
});

onMounted(async () => {
  // 进入页面就尝试获取用户信息
  await userStore.fetchLoginUser();
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
