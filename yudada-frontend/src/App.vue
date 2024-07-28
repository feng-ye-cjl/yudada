<template>
  <div id="app">
    <!--根据路由判断是否使用全局路由-->
    <template v-if="route.path.startsWith('/user')">
      <router-view />
    </template>
    <template v-else>
      <BasicLayout />
    </template>
  </div>
</template>

<script setup lang="ts">
import BasicLayout from "@/layouts/BasicLayout.vue";
import { useRoute } from "vue-router";
import { onMounted } from "vue";

const route = useRoute();

// 全局初始化
const init = async () => {
  console.log("project init");
};

onMounted(() => {
  init();
});
</script>
<style scoped>
#app {
  box-sizing: border-box;
}
</style>
