<template>
  <div id="adminAppPage">
    <!--搜索列表-->
    <a-form
      :model="form"
      :layout="layout"
      @keydown.enter="handleSearch"
      style="margin-bottom: 20px"
    >
      <a-form-item field="post" label="应用名称">
        <a-input
          v-model="form.appName"
          placeholder="请输入应用名称"
          class="input-demo"
        />
      </a-form-item>
      <a-form-item field="name" label="应用描述">
        <a-input v-model="form.appDesc" placeholder="请输入应用描述" />
      </a-form-item>
      <a-form-item>
        <a-button @click="handleSearch" type="primary">
          <icon-search style="margin-right: 10px" />
          搜索
        </a-button>
      </a-form-item>
    </a-form>
    <!--员工数据表格-->
    <a-table
      :columns="columns"
      :data="appList"
      :pagination="{
        showTotal: true,
        current: searchParams.current,
        pageSize: searchParams.pageSize,
        total,
      }"
      @page-change="pageChange"
    >
      <template #appType="{ record }">
        <a-tag color="magenta" v-if="record.appType === 0"
          >{{ APP_TYPE_MAP[record.appType] }}
        </a-tag>
        <a-tag v-else color="gray">{{ APP_TYPE_MAP[record.appType] }}</a-tag>
      </template>
      <template #scoringStrategy="{ record }">
        <a-tag color="cyan" v-if="record.scoringStrategy === 0"
          >{{ APP_SCORING_STRATEGY_MAP[record.scoringStrategy] }}
        </a-tag>
        <a-tag v-else color="purple"
          >{{ APP_SCORING_STRATEGY_MAP[record.scoringStrategy] }}
        </a-tag>
      </template>
      <template #reviewStatus="{ record }">
        <a-tag color="orange" v-if="record.reviewStatus === 0"
          >{{ REVIEW_STATUS_MAP[record.reviewStatus] }}
        </a-tag>
        <a-tag v-else-if="record.reviewStatus === 1" color="green"
          >{{ REVIEW_STATUS_MAP[record.reviewStatus] }}
        </a-tag>
        <a-tag v-else color="red"
          >{{ REVIEW_STATUS_MAP[record.reviewStatus] }}
        </a-tag>
      </template>
      <template #createTime="{ record }">
        <span>
          {{ dayjs(record.createTime).format("YYYY-MM-DD") }}
        </span>
      </template>
      <template #updateTime="{ record }">
        <span>
          {{ dayjs(record.updateTime).format("YYYY-MM-DD") }}
        </span>
      </template>
      <template #option="{ record }">
        <a-button
          type="primary"
          size="mini"
          status="danger"
          @click="deleteUser(record.id)"
          >删除
        </a-button>
      </template>
    </a-table>
  </div>
</template>
<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { deleteUserUsingPost } from "@/api/userController";
import API from "@/api";
import { Message } from "@arco-design/web-vue";
import dayjs from "dayjs";
import { listAppByPageUsingPost } from "@/api/appController";
import {
  APP_SCORING_STRATEGY_MAP,
  APP_TYPE_MAP,
  REVIEW_STATUS_MAP,
} from "@/constant/app";

// region 搜索表单
const layout = ref("inline");
const form = ref({
  appName: "",
  appDesc: "",
});
// 搜索方法
const handleSearch = () => {
  searchParams.value = {
    // 传入初始分页数据和查询数据
    ...form.value,
    ...iniSearchParams,
  };
  // 清空数据框
  form.value = { appDesc: "", appName: "" };
};

// endregion

// region 获取用户表数据
// 初始分页参数（不可修改）
const iniSearchParams = {
  current: 1,
  pageSize: 10,
};

const searchParams = ref({
  ...iniSearchParams,
});
// 分页改变方法
const pageChange = (page: number) => {
  searchParams.value.current = page;
};
// 总记录数
const total = ref();
const columns = [
  {
    title: "应用名称",
    dataIndex: "appName",
    align: "center",
  },
  {
    title: "应用描述",
    dataIndex: "appDesc",
    align: "center",
  },
  {
    title: "应用图标",
    dataIndex: "appIcon",
    align: "center",
  },
  {
    title: "应用类型",
    dataIndex: "appType",
    slotName: "appType",
    align: "center",
  },
  {
    title: "评分策略",
    dataIndex: "scoringStrategy",
    slotName: "scoringStrategy",
    align: "center",
  },
  {
    title: "审核状态",
    dataIndex: "reviewStatus",
    slotName: "reviewStatus",
    align: "center",
  },
  {
    title: "审核信息",
    dataIndex: "reviewMessage",
    align: "center",
  },
  {
    title: "审核人 ID",
    dataIndex: "reviewerId",
    align: "center",
  },
  {
    title: "审核时间",
    dataIndex: "reviewTime",
    align: "center",
  },
  {
    title: "创建用户 ID",
    dataIndex: "userId",
    align: "center",
  },
  {
    title: "创建时间",
    dataIndex: "createTime",
    slotName: "createTime",
    align: "center",
  },
  {
    title: "更新时间",
    dataIndex: "updateTime",
    slotName: "updateTime",
    align: "center",
  },
];

const appList = ref<API.App[]>();
const getAppList = async () => {
  const res = await listAppByPageUsingPost(searchParams.value);
  if (res.data?.code === 0) {
    appList.value = res.data.data?.records || [];
    total.value = Number(res.data.data?.total) || 0;
  } else {
    Message.error(res.data.message as string);
  }
};

/**
 * 删除用户
 * @param id
 */
const deleteUser = async (id: number) => {
  if (!id) {
    return;
  }
  const res = await deleteUserUsingPost({ id });
  if (res.data.code === 0) {
    Message.success("删除成功");
    // 重新获取用户信息
    await getAppList();
  } else {
    Message.error("删除失败，" + res.data.message);
  }
};

watchEffect(() => {
  getAppList();
});
// endregion
</script>
<style scoped>
#adminAppPage {
  width: 85vw;
  margin: 0 auto;
}
</style>
