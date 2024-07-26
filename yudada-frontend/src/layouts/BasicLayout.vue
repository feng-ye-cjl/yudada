<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a
          href="https://www.yuque.com/fengye-tmlkt/bq624f/xxxwhslg8cope1xc?singleDoc# 《项目后端开发》"
          target="_blank"
        >
          AI答题应用平台 by 枫叶🍁
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>
<script setup lang="ts">
import GlobalHeader from "@/components/GlobalHeader.vue";
</script>
<style scoped lang="scss">
#basicLayout {
  .header {
    margin-bottom: 16px;
    box-shadow: #eee 1px 1px 5px;
  }

  .content {
    margin-bottom: 28px;
    padding: 20px;
    background: linear-gradient(to right, #fefefe, #fff);
  }

  .footer {
    position: fixed;
    right: 0;
    bottom: 0;
    left: 0;
    padding: 10px;
    text-align: center;
    background-color: #efefef;
  }
}
</style>
