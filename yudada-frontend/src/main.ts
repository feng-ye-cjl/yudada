import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import ArcoVue from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css";
import { createPinia } from "pinia";
import ArcoVueIcon from "@arco-design/web-vue/es/icon";
// 全局登录校验
import "@/access";

// 新引入配置的复制区域
// 额外引入图标库

// ...

const app = createApp(App);
const pinia = createPinia();
app.use(router).use(ArcoVue).use(ArcoVueIcon).use(pinia).mount("#app");
