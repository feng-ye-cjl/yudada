<template>
  <div id="adminQuestionPage">
    <a-form
      :model="formSearchParams"
      :style="{ marginBottom: '20px' }"
      layout="inline"
      @submit="doSearch"
    >
      <a-form-item field="appId" label="应用 id">
        <a-input
          v-model="formSearchParams.appId"
          placeholder="请输入应用 id"
          allow-clear
        />
      </a-form-item>
      <a-form-item field="userId" label="用户 id">
        <a-input
          v-model="formSearchParams.userId"
          placeholder="请输入用户 id"
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
      <template #questionContent="{ record }">
        <a-button type="outline" size="mini" @click="getContent(record)"
          >查看题目
        </a-button>
      </template>
      <template #appName="{ record }">
        {{ record.app.appName }}
      </template>
      <template #userName="{ record }">
        {{ record.user.userName }}
      </template>
      <template #createTime="{ record }">
        {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
      </template>
      <template #updateTime="{ record }">
        {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
      </template>
      <template #optional="{ record }">
        <a-space>
          <a-button status="danger" size="mini" @click="doDelete(record)"
            >删除
          </a-button>
        </a-space>
      </template>
    </a-table>
    <!--题目内容-->
    <a-modal
      v-model:visible="contentVisible"
      @ok="handleOk"
      @cancel="handleCancel"
      :footer="false"
    >
      <template #title>{{ currentAppName }}</template>
      <div style="height: 400px">
        <a-list
          v-for="(item, index) in currentContent"
          :key="item.title"
          style="margin-bottom: 20px"
        >
          <template #header>
            <span v-if="item.title.split('.').length === 1"
              >{{ index + 1 }}.</span
            >
            {{ item.title }}
          </template>
          <a-list-item v-for="option in item.options" :key="option">
            <span style="float: left">
              {{ option.key }}、 {{ option.value }}</span
            >
            <span style="float: right" v-if="option.result"
              >属性：
              <a-tag color="blue" bordered>{{ option.result }}</a-tag></span
            >
            <span style="float: right" v-else>得分：{{ option.score }}</span>
          </a-list-item>
        </a-list>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import {
  deleteQuestionUsingPost,
  listQuestionVoByPageUsingPost,
} from "@/api/questionController";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";

// region 题目表格数据
const formSearchParams = ref<API.QuestionQueryRequest>({});

// 初始化搜索条件（不应该被修改）
const initSearchParams = {
  current: 1,
  pageSize: 10,
};

const searchParams = ref<API.QuestionQueryRequest>({
  ...initSearchParams,
});
const dataList = ref<API.QuestionVO>();
const total = ref<number>(0);

// 表格列配置
const columns = [
  {
    title: "id",
    dataIndex: "id",
    align: "center",
  },
  {
    title: "题目列表",
    dataIndex: "questionContent",
    slotName: "questionContent",
    align: "center",
  },
  {
    title: "应用",
    slotName: "appName",
    align: "center",
  },
  {
    title: "用户",
    slotName: "userName",
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
    slotName: "optional",
    align: "center",
  },
];

/**
 * 加载数据
 */
const loadData = async () => {
  // const res = await listQuestionByPageUsingPost(searchParams.value);
  const res = await listQuestionVoByPageUsingPost(searchParams.value);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
  console.log("dataList = ", dataList.value);
};

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
const doDelete = async (record: API.Question) => {
  if (!record.id) {
    return;
  }

  const res = await deleteQuestionUsingPost({
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

// endregion question

// region 题目内容
// 当前题目详情
const currentContent = ref();
// 当前app名称
const currentAppName = ref();
// 查看详情内容
const getContent = (record: any) => {
  contentVisible.value = true;
  currentContent.value = record.questionContent;
  currentAppName.value = record.app.appName;
  console.log(record);
  // todo 根据id查询app信息
  console.log(currentContent.value);
};

const contentVisible = ref(false);
const handleOk = () => {
  contentVisible.value = false;
};
const handleCancel = () => {
  contentVisible.value = false;
};
// endregion
</script>
<style lang="scss" scoped>
#adminQuestionPage {
  .arco-list-header {
    background-color: #42b983 !important;
  }

  .arco-modal-footer {
    text-align: center !important;
  }
}
</style>
