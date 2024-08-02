<template>
  <div id="homePage">
    <!--搜索框-->
    <div style="text-align: center">
      <a-input-search
        style="width: 200px; margin-bottom: 28px"
        placeholder="请输入应用名"
        search-button
      />
    </div>
    <!--应用列表-->
    <a-list
      class="list-demo-action-layout"
      :grid-props="{ gutter: [20, 20], sm: 24, md: 12, lg: 8, xl: 6 }"
      :bordered="false"
      :data="appList"
      :pagination-props="{
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      @page-change="pageChange"
    >
      <template #item="{ item }">
        <AppCard :app="item" />
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import AppCard from "@/components/AppCard.vue";
import { listAppVoByPageUsingPost } from "@/api/appController";
import { Message } from "@arco-design/web-vue";
import API from "@/api";
import { REVIEW_STATUS_ENUM } from "@/constant/app";
// 初始分页参数（不可修改）
const iniSearchParams = {
  current: 1,
  pageSize: 9,
};

const searchParams = ref<API.AppQueryRequest>({
  ...iniSearchParams,
});
// 总记录数
const total = ref(0);
// 分页改变方法
const pageChange = (page: number) => {
  searchParams.value.current = page;
};
// app列表
const appList = ref<API.AppVO[]>([]);
// 列表加载
const isLoading = ref(false);
/**
 * 获取应用列表
 */
const getAppList = async () => {
  isLoading.value = true;
  // 只能查看过审的应用
  const params = {
    ...searchParams.value,
    reviewStatus: REVIEW_STATUS_ENUM.REVIEWING,
  };
  const res = await listAppVoByPageUsingPost(params);
  if (res.data?.code === 0) {
    appList.value = res.data.data?.records || [];
    total.value = Number(res.data.data?.total) || 0;
  } else {
    Message.error(res.data.message as string);
  }
  isLoading.value = false;
  console.log("appList = ", appList.value);
};

watchEffect(() => {
  getAppList();
});
</script>

<style scoped>
#homePage {
  width: 100%;
  height: 100%;

  .list-demo-action-layout .image-area {
    width: 183px;
    height: 119px;
    overflow: hidden;
    border-radius: 2px;
  }

  .list-demo-action-layout .list-demo-item {
    padding: 20px 0;
    //border-bottom: 1px solid var(--color-fill-3);
  }

  .list-demo-action-layout .image-area img {
    width: 100%;
  }

  .list-demo-action-layout .arco-list-item-action .arco-icon {
    margin: 0 4px;
  }
}
</style>
