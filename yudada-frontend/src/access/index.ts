import router from "@/router";
import { useLoginUserStore } from "@/store/userStore";
import accessEnum from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

router.beforeEach(async (to, from, next) => {
  // 获取当前登录用户
  const userStore = useLoginUserStore();
  let loginUser = userStore.loginUser;
  // 如果之前没登录过则自动登录
  if (!loginUser || !loginUser.userRole) {
    await userStore.fetchLoginUser();
    loginUser = userStore.loginUser;
  }

  // 跳转页面需要的权限, 如果没有权限原数据则是不需要登录
  const needAccess = (to.meta?.access as string) ?? accessEnum.NOT_LOGIN;
  // 如果页面需要登录而用户没登录则跳转到登录页面
  if (needAccess !== accessEnum.NOT_LOGIN) {
    // 如果用户未登录则跳转到登录页
    if (
      !loginUser ||
      !loginUser.userRole ||
      loginUser.userRole === accessEnum.NOT_LOGIN
    ) {
      console.log("用户未登录");
      next(`/user/login?redirect=${to.fullPath}`);
      return;
    }
    // 如果用户已经登录但是权限不足，则跳转动啊404页面
    if (!checkAccess(loginUser, needAccess)) {
      next("/noAuth");
      return;
    }
  }
  // 最后满足条件放行
  next();
});
