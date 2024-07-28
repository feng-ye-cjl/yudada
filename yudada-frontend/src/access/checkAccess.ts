import accessEnum from "@/access/accessEnum";

/**
 * 权限校验函数
 * @param loginUser 当前校验用户
 * @param needAccess 需要的权限
 * @return boolean 是否有权限
 */
const checkAccess = (loginUser: any, needAccess = accessEnum.NOT_LOGIN) => {
  // 获取当前登录用户具有的权限(如果没有则是未登录)
  const loginUserAccess = loginUser?.userRole ?? accessEnum.NOT_LOGIN;

  // 不需要登录就可以访问
  if (needAccess === accessEnum.NOT_LOGIN) return true;

  // 需要登录才能访问
  if (needAccess === accessEnum.USER) {
    if (loginUserAccess === accessEnum.NOT_LOGIN) {
      return false;
    }
  }

  // 需要管理员权限才能访问
  if (needAccess === accessEnum.ADMIN) {
    if (loginUserAccess !== accessEnum.ADMIN) {
      return false;
    }
  }
  return true;
};
export default checkAccess;
