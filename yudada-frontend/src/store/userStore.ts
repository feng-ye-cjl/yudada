import { defineStore } from "pinia";
import { ref } from "vue";
import { getLoginUserUsingGet } from "@/api/userController";
import accessEnum from "@/access/accessEnum";

/**
 * 登录用户store
 */
export const useLoginUserStore = defineStore("loginUser", () => {
  const loginUser = ref<API.LoginUserVO>({
    // 默认未登录
    userName: "未登录",
  });

  /**
   * 获取并设置登录用户信息
   */
  const fetchLoginUser = async () => {
    const res = await getLoginUserUsingGet();
    // console.log("login user = ", res.data);
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data;
    } else {
      loginUser.value = { userRole: accessEnum.NOT_LOGIN };
    }
  };

  const setLoginUser = (newLoginUser: API.LoginUserVO) => {
    loginUser.value = newLoginUser;
  };

  return { loginUser, fetchLoginUser, setLoginUser };
});
