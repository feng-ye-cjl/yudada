<template>
  <div id="adminUserPage">
    <!--搜索列表-->
    <a-form :model="form" :layout="layout" @keydown.enter="handleSearch">
      <a-form-item field="post" label="用户Id">
        <!--<a-input v-model="form.id" placeholder="请输入用户Id" />-->
        <a-input-number
          v-model="form.id"
          placeholder="请输入用户Id"
          class="input-demo"
          :allow-clear="true"
        />
      </a-form-item>
      <a-form-item field="name" label="用户名">
        <a-input
          v-model="form.userName"
          placeholder="请输入用户名"
          :allow-clear="true"
        />
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
      :data="userList"
      :pagination="{
        showTotal: true,
        current: searchParams.current,
        pageSize: searchParams.pageSize,
        total,
      }"
      @page-change="pageChange"
    >
      <template #userAvatar="{ record }">
        <a-avatar>
          <img alt="avatar" :src="record.userAvatar" />
        </a-avatar>
      </template>
      <template #createTime="{ record }">
        <span>
          {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
        </span>
      </template>
      <template #updateTime="{ record }">
        <span>
          {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
        </span>
      </template>
      <template #option="{ record }">
        <a-popconfirm content="是否确定删除？" @ok="deleteUser(record.id)">
          <a-button size="mini" status="danger">删除</a-button>
        </a-popconfirm>
      </template>
    </a-table>
  </div>
</template>
<script setup lang="ts">
import { ref, watchEffect } from "vue";
import {
  deleteUserUsingPost,
  listUserByPageUsingPost,
} from "@/api/userController";
import API from "@/api";
import { Message } from "@arco-design/web-vue";
import dayjs from "dayjs";

// region 搜索表单
const layout = ref("inline");
const form = ref({
  userName: "",
  id: "",
});
// 搜索方法
const handleSearch = () => {
  searchParams.value = {
    // 传入初始分页数据和查询数据
    ...form.value,
    ...iniSearchParams,
  };
  // 清空数据框
  form.value = {
    userName: "",
    id: "",
  };
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
    title: "ID",
    dataIndex: "id",
    align: "center",
  },
  {
    title: "账号",
    dataIndex: "userAccount",
    align: "center",
  },
  {
    title: "用户名",
    dataIndex: "userName",
    align: "center",
  },
  {
    title: "头像",
    dataIndex: "userAvatar",
    slotName: "userAvatar",
    align: "center",
  },
  {
    title: "用户简介",
    dataIndex: "userProfile",
    align: "center",
  },
  {
    title: "角色",
    dataIndex: "userRole",
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
  {
    title: "操作",
    slotName: "option",
    align: "center",
  },
];

const userList = ref<API.User[]>();
const getUserInfo = async () => {
  const res = await listUserByPageUsingPost(searchParams.value);
  if (res.data?.code === 0) {
    userList.value = res.data.data?.records || [];
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
    await getUserInfo();
  } else {
    Message.error("删除失败，" + res.data.message);
  }
};

watchEffect(() => {
  getUserInfo();
});
// endregion
</script>
<style scoped>
#adminUserPage {
}
</style>
