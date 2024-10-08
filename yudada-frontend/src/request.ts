import axios from "axios";

const myAxios = axios.create({
  baseURL: "http://localhost:8101",
  timeout: 10000,
  // 携带cookie保存用户请求态
  withCredentials: true,
});

// Add a request interceptor
myAxios.interceptors.request.use(
  function (config) {
    // Do something before request is sent
    return config;
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error);
  }
);

// Add a response interceptor
myAxios.interceptors.response.use(
  function (response) {
    const { data } = response;
    // console.log("响应数据 = ", data);
    // 未登录
    if (data.code === 40100) {
      // 不是获取用户信息接口，或者不是登录页面，则跳转到登录页面
      const goLogin =
        !response.request.responseURL.includes("user/get/login") &&
        !window.location.pathname.includes("/user/login");
      console.log("goLogin = ", goLogin);
      console.log(!response.request.responseURL.includes("user/get/login"));
      console.log(response.request.responseURL);
      if (goLogin) {
        window.location.href = `/user/login?redirect=${window.location.href}`;
      }
    }
    // 过滤axios响应信息
    return response;
  },
  function (error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error);
  }
);

export default myAxios;
