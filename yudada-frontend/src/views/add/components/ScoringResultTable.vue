<template>
  <h2>当前应用评分表格</h2>
  <a-form
    :model="formSearchParams"
    :style="{ marginBottom: '20px' }"
    layout="inline"
    @submit="doSearch"
  >
    <a-form-item field="resultName" label="结果名称">
      <a-input
        v-model="formSearchParams.resultName"
        placeholder="请输入结果名称"
        allow-clear
      />
    </a-form-item>
    <a-form-item field="resultDesc" label="结果描述">
      <a-input
        v-model="formSearchParams.resultDesc"
        placeholder="请输入结果描述"
        allow-clear
      />
    </a-form-item>
    <a-form-item>
      <a-button type="primary" html-type="submit" style="width: 100px">
        搜索
      </a-button>
    </a-form-item>
  </a-form>
  <a-table
    :columns="columns"
    :data="dataList"
    :pagination="{
      showTotal: true,
      pageSize: searchParams.pageSize,
      current: searchParams.current,
      total,
    }"
    @page-change="onPageChange"
  >
    <template #resultPicture="{ record }">
      {{ record.resultPicture }}
    </template>
    <template #resultProp="{ record }">
      <a-space size="mini">
        <a-tag
          size="mini"
          bordered
          color="orange"
          v-for="item in JSON.parse(record.resultProp)"
          :key="item"
          >{{ item }}
        </a-tag>
      </a-space>
    </template>
    <template #createTime="{ record }">
      {{ dayjs(record.createTime).format("YY-MM-DD") }}
    </template>
    <template #updateTime="{ record }">
      {{ dayjs(record.updateTime).format("YY-MM-DD") }}
    </template>
    <template #optional="{ record }">
      <a-space>
        <!--把当前记录传递出去-->
        <a-button status="success" @click="doUpdate?.(record)">修改</a-button>
        <a-button status="danger" @click="doDelete(record)">删除</a-button>
      </a-space>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { defineExpose, defineProps, ref, watchEffect, withDefaults } from "vue";
import {
  deleteScoringResultUsingPost,
  listScoringResultByPageUsingPost,
} from "@/api/scoringResultController";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";

// 当前appId
interface Props {
  appId: number;
  doUpdate: (scoringResult: API.ScoringResultVO) => void;
}

const props = withDefaults(defineProps<Props>(), {
  appId: 0,
});

const formSearchParams = ref<API.ScoringResultQueryRequest>({});

// 初始化搜索条件（不应该被修改）
const initSearchParams = {
  appId: props.appId,
  current: 1,
  pageSize: 10,
  sortField: "createTime",
  sortOrder: "descend",
};

const searchParams = ref<API.ScoringResultQueryRequest>({
  ...initSearchParams,
});
const dataList = ref<API.ScoringResult[]>([]);
const total = ref<number>(0);

/**
 * 加载数据
 */
const loadData = async () => {
  const res = await listScoringResultByPageUsingPost(searchParams.value);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
// 暴露当前加载数据的方法
defineExpose({
  loadData,
});
/**
 * 执行搜索
 */
const doSearch = () => {
  searchParams.value = {
    ...initSearchParams,
    ...formSearchParams.value,
  };
};

/**
 * 当分页变化时，改变搜索条件，触发数据加载
 * @param page
 */
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

/**
 * 删除
 * @param record
 */
const doDelete = async (record: API.ScoringResult) => {
  if (!record.id) {
    return;
  }

  const res = await deleteScoringResultUsingPost({
    id: record.id,
  });
  if (res.data.code === 0) {
    loadData();
  } else {
    message.error("删除失败，" + res.data.message);
  }
};

/**
 * 监听 searchParams 变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  loadData();
});

// 表格列配置
const columns = [
  {
    title: "id",
    dataIndex: "id",
  },
  {
    title: "名称",
    dataIndex: "resultName",
  },
  {
    title: "描述",
    dataIndex: "resultDesc",
  },
  {
    title: "图片",
    dataIndex: "resultPicture",
    slotName: "resultPicture",
  },
  {
    title: "结果属性",
    dataIndex: "resultProp",
    slotName: "resultProp",
    align: "center",
  },
  {
    title: "评分范围",
    dataIndex: "resultScoreRange",
  },
  {
    title: "用户 id",
    dataIndex: "userId",
  },
  {
    title: "创建时间",
    dataIndex: "createTime",
    slotName: "createTime",
  },
  {
    title: "更新时间",
    dataIndex: "updateTime",
    slotName: "updateTime",
  },
  {
    title: "操作",
    slotName: "optional",
    align: "center",
  },
];
</script>
