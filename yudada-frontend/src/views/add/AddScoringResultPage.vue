<template>
  <div id="addScoringResultPage">
    <h2 style="margin-bottom: 28px; text-align: center">设置评分</h2>
    <a-form
      :model="form"
      :style="{ width: '400px', margin: '0 auto' }"
      label-align="left"
      auto-label-width
    >
      {{ form }}
      <a-form-item label="应用id">
        {{ appId }}
      </a-form-item>
      <a-form-item label="结果名称">
        <a-input v-model="form.resultName" placeholder="请输入结果名称" />
      </a-form-item>
      <a-form-item label="结果描述">
        <a-input v-model="form.resultDesc" placeholder="请输入结果描述" />
      </a-form-item>
      <a-form-item label="结果图标">
        <Upload :icon="form.resultPicture" />
      </a-form-item>
      <a-form-item label="结果集">
        <a-input-tag
          v-model="form.resultProp"
          allow-clear
          placeholder="请输入结果集合"
        />
      </a-form-item>
      <a-form-item label="结果得分范围">
        <a-input-number
          v-model="form.resultScoreRange"
          placeholder="请输入结果得分范围"
        />
      </a-form-item>
      <a-form-item>
        <div style="width: 100%; text-align: left; margin-top: 20px">
          <a-button type="primary" style="width: 120px">提交</a-button>
        </div>
      </a-form-item>
    </a-form>
    <!--评分管理表格-->
    <ScoringResultTable :appId="appId" />
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref, withDefaults } from "vue";
import { addAppUsingPost, updateAppUsingPost } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import Upload from "@/views/add/components/Upload.vue";
import { useRouter } from "vue-router";
import API from "@/api";
import ScoringResultTable from "@/views/add/components/ScoringResultTable.vue";

const router = useRouter();

// region 应用表单
interface Props {
  appId: number;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => 0,
});

/**
 * 应用表单：必须先填充默认值
 */
const form = ref<API.ScoringResultAddRequest>({
  resultDesc: "",
  resultName: "",
  resultPicture: "",
});

// endregion

/**
 * 创建应用
 */
const addApp = async () => {
  if (props.appId !== 0) {
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
  if (props.appId === 0) {
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
</script>
<style scoped>
#addScoringResultPage {
}
</style>
