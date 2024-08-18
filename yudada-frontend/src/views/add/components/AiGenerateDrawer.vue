<template>
  <a-button type="outline" @click="handleClick">一键生成题目</a-button>
  <a-drawer
    :width="340"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    unmountOnClose
  >
    <template #title>AI生成题目</template>
    <a-form
      :model="form"
      :style="{ margin: '0 auto' }"
      label-align="left"
      auto-label-width
    >
      <a-form-item label="应用名称">
        <span>{{ appName }}</span>
      </a-form-item>
      <a-form-item label="题目数量">
        <a-input-number
          v-model="form.questionNumber"
          placeholder="请输入密码"
        />
      </a-form-item>
      <a-form-item label="选项数量">
        <a-input-number v-model="form.optionNumber" placeholder="请输入密码" />
      </a-form-item>
      <div style="width: 100%; text-align: center">
        <a-button
          :loading="isGenerate"
          type="primary"
          @click="aiGenerate"
          style="width: 120px"
        >
          开始生成
        </a-button>
      </div>
    </a-form>
  </a-drawer>
</template>

<script setup lang="ts">
import { defineProps, ref, withDefaults } from "vue";
import API from "@/api";
import { aiGenerateQuestionUsingPost } from "@/api/questionController";
import { Message } from "@arco-design/web-vue";

// 当前appId
interface Props {
  appId: number;
  appName: string;
  onGenerate?: (result: API.QuestionContentDTO[]) => void; // 箭头后面为返回值
}

const props = withDefaults(defineProps<Props>(), {
  appId: 0,
  appName: "",
});

console.log(props.appId);
const form = ref<API.AiGenerateQuestionRequest>({
  appId: props.appId,
  questionNumber: 5,
  optionNumber: 2,
});

const isGenerate = ref(false);

/**
 * 提交
 */
const aiGenerate = async () => {
  if (!props.appId) {
    return;
  }
  isGenerate.value = true;
  // 用户登录
  const res = await aiGenerateQuestionUsingPost(form.value);
  if (res.data.code === 0) {
    if (props.onGenerate) {
      props.onGenerate(res.data.data as API.QuestionContentDTO[]);
      Message.success("AI生成题目成功");
    }
  } else {
    Message.error(res.data.message as string);
  }
  // 关闭抽屉
  visible.value = false;
  // 取消loading
  isGenerate.value = false;
};

const visible = ref(false);

const handleClick = () => {
  visible.value = true;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};
</script>
