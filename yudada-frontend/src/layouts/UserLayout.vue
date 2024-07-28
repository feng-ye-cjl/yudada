<template>
  <div id="userLayout">
    <a-layout style="min-height: 100vh">
      <!--头部-->
      <a-layout-header class="header">
        <a-space>
          <img src="../assets/logo.png" class="logo" />
          <div>AI 答题应用平台</div>
        </a-space>
      </a-layout-header>
      <!--内容-->
      <a-layout-content class="content">
        <!--渲染子路由页面-->
        <router-view />
      </a-layout-content>
      <!--底部-->
      <a-layout-footer class="footer">
        <a
          href="https://www.yuque.com/fengye-tmlkt/bq624f/xxxwhslg8cope1xc?singleDoc#"
          target="_blank"
        >
          AI答题应用平台 by 枫叶🍁
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
#userLayout {
  text-align: center;
  //background: url("https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png") 0% 0% / 100% 100%;
  background: linear-gradient(to bottom right, #ff7e5f, #6dd5ed);

  .logo {
    width: 48px;
    height: 48px;
  }

  .header {
    margin-top: 16px;
    font-size: 21px;
  }

  .content {
    margin-bottom: 16px;
    padding: 20px;
  }

  .footer {
    position: fixed;
    right: 0;
    bottom: 0;
    left: 0;
    padding: 16px;
    text-align: center;
  }
}
</style>
