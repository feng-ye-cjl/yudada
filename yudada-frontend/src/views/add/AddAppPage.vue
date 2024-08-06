<template>
  <div id="addAppPage">
    <h2 style="margin-bottom: 28px; text-align: center">
      {{ id === 0 ? "创建应用" : "修改应用" }}
    </h2>
    <a-form
      :model="form"
      :style="{ width: '400px', margin: '0 auto' }"
      label-align="left"
      auto-label-width
    >
      <a-form-item label="应用名称">
        <a-input v-model="form.appName" placeholder="请输入应用名" />
      </a-form-item>
      <a-form-item label="应用描述">
        <a-input v-model="form.appDesc" placeholder="请输入应用描述" />
      </a-form-item>
      <a-form-item label="应用图标">
        <Upload :icon="form.appIcon" />
      </a-form-item>
      <a-form-item label="应用类型">
        <a-select v-model="form.appType" placeholder="请选择应用类型">
          <!--直接遍历映射类-->
          <a-option
            v-for="(value, index) of APP_TYPE_MAP"
            :key="index"
            :value="Number(index)"
            :label="value"
          />
        </a-select>
      </a-form-item>
      <a-form-item label="评分策略">
        <a-select v-model="form.scoringStrategy" placeholder="请选择评分策略">
          <!--直接遍历映射类-->
          <a-option
            v-for="(value, index) of APP_SCORING_STRATEGY_MAP"
            :key="index"
            :value="Number(index)"
            :label="value"
          />
        </a-select>
      </a-form-item>
      <a-form-item>
        <div style="width: 100%; text-align: left; margin-top: 20px">
          <a-button
            v-if="id === 0"
            type="primary"
            style="width: 120px"
            @click="addApp"
            >创建
          </a-button>
          <a-button
            v-else
            type="primary"
            style="width: 120px"
            @click="updateApp"
            >修改
          </a-button>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import API from "@/api";

import { defineProps, onMounted, ref, withDefaults } from "vue";
import {
  addAppUsingPost,
  getAppVoByIdUsingGet,
  updateAppUsingPost,
} from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import Upload from "@/views/add/components/Upload.vue";
import { useRouter } from "vue-router";

const router = useRouter();

// region 应用表单
interface Props {
  id: number;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => 0,
});

/**
 * 应用表单：必须先填充默认值
 */
const form = ref<API.AppAddRequest>({});

// endregion

/**
 * 创建应用
 */
const addApp = async () => {
  if (props.id !== 0) {
    return;
  }
  const res = await addAppUsingPost(form.value);
  console.log(res);
  if (res.data.code === 0) {
    message.success("创建成功！即将跳转应用详情页");
    setTimeout(() => {
      router.push(`/app/detail/${res.data.data}`);
    }, 1500);
  } else {
    message.error("创建失败");
  }
};

/**
 * 修改应用
 */
const updateApp = async () => {
  if (props.id === 0) {
    return;
  }
  const res = await updateAppUsingPost(form.value);
  console.log(res);
  if (res.data.code === 0) {
    message.success("修改成功！即将跳转应用详情页");
    setTimeout(() => {
      router.back();
    }, 1500);
  } else {
    message.error("修改失败");
  }
};

// 获取应用信息
const getAppInfo = async () => {
  if (props.id === 0) {
    return;
  }
  const res = await getAppVoByIdUsingGet({ id: props.id });
  if (res.data.code === 0) {
    form.value = res.data.data as API.AppAddRequest;
    console.log("form = ", form.value);
  } else {
    message.error("获取应用信息失败");
  }
};
onMounted(() => getAppInfo());
</script>
<style scoped>
#addAppPage {
}
</style>
