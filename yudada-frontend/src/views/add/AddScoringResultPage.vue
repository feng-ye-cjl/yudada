<template>
  <div id="addScoringResultPage">
    <h2 style="margin-bottom: 28px; text-align: center">设置评分</h2>
    <a-form
      :model="form"
      :style="{ width: '400px', margin: '0 auto' }"
      label-align="left"
      auto-label-width
    >
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
        <a-input v-model="form.resultPicture" placeholder="请输入结果图标" />
        <!--<Upload :icon="form.resultPicture" />-->
      </a-form-item>
      <a-form-item label="结果集" style="width: 400px; height: 32px">
        <a-input-tag
          v-model="form.resultProp"
          allow-clear
          placeholder="请输入结果集合"
        />
      </a-form-item>
      <!--{{ form.resultProp }}-->
      <!--{{ typeof form.resultProp }}-->
      <a-form-item label="结果得分范围">
        <a-input-number
          v-model="form.resultScoreRange"
          placeholder="请输入结果得分范围"
        />
      </a-form-item>
      <a-form-item>
        <div style="width: 100%; text-align: left; margin-top: 20px">
          <a-button type="primary" style="width: 120px" @click="onSubmit"
            >提交
          </a-button>
        </div>
      </a-form-item>
    </a-form>
    <!--评分管理表格-->
    <ScoringResultTable :appId="appId" :doUpdate="doUpdate" ref="tableRef" />
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref, withDefaults } from "vue";
import API from "@/api";
import ScoringResultTable from "@/views/add/components/ScoringResultTable.vue";
import message from "@arco-design/web-vue/es/message";
import {
  addScoringResultUsingPost,
  updateScoringResultUsingPost,
} from "@/api/scoringResultController";

// 表格实例
const tableRef = ref();

// region 应用表单
interface Props {
  appId: number;
}

const updateId = ref();

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
 * 回显更新数据
 * @param scoringResult
 */
const doUpdate = (scoringResult: API.ScoringResultVO) => {
  console.log("结果属性", scoringResult.resultProp);
  console.log("结果属性类型", typeof scoringResult.resultProp);
  updateId.value = scoringResult.id;
  // form.value = scoringResult;
  // 解构赋值，不影响原来表格的元素
  form.value = { ...scoringResult };
  console.log("结果属性", form.value.resultProp);
  console.log("结果属性类型", typeof form.value.resultProp);
  if (typeof form.value.resultProp === "string") {
    form.value.resultProp = JSON.parse(form.value.resultProp);
  }
};

const arr = '["a", "b"]';
console.log(typeof arr);

/**
 * 提交修改或者新增
 */
const onSubmit = async () => {
  if (props.appId === 0) {
    message.error("请先选择应用");
    return;
  }
  let res;
  if (updateId.value) {
    res = await updateScoringResultUsingPost({
      id: updateId.value,
      ...form.value,
    });
  } else {
    res = await addScoringResultUsingPost({
      appId: props.appId,
      ...form.value,
    });
  }
  console.log(res);
  if (res.data.code === 0) {
    message.success("设置成功！");
    if (tableRef.value) {
      await tableRef.value.loadData();
    }
  } else {
    message.error("设置失败");
  }
  // 清空表单
  form.value = {};
  updateId.value = undefined;
};
</script>
<style scoped>
#addScoringResultPage {
}
</style>
