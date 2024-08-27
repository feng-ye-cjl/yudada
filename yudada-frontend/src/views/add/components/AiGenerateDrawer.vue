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
        <a-space>
          <a-button
            :loading="isGenerate"
            type="primary"
            @click="aiGenerate"
            style="width: 120px"
          >
            {{ isGenerate ? "生成中" : "开始生成" }}
          </a-button>
          <a-button type="primary" @click="aiGenerateSSE" style="width: 120px">
            实时生成
          </a-button>
        </a-space>
      </div>
    </a-form>
  </a-drawer>
</template>

<script setup lang="ts">
import { defineProps, ref, withDefaults } from "vue";
import API from "@/api";
import { aiGenerateQuestionUsingPost } from "@/api/questionController";
import { Message } from "@arco-design/web-vue";
import message from "@arco-design/web-vue/es/message";

// 当前appId
interface Props {
  appId: number;
  appName: string;
  onGenerate?: (result: API.QuestionContentDTO[]) => void; // 箭头后面为返回值
  onSSEGenerate?: (result: API.QuestionContentDTO) => void;
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
  message.info("AI生成题目中，请耐心等待");
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

/**
 * sse提交
 */
const aiGenerateSSE = async () => {
  if (!props.appId) {
    return;
  }
  // 关闭抽屉
  visible.value = false;
  // 创建eventSource对象
  let bashUrl = "http://localhost:8101/api/question/ai_generate/sse";
  let url = `${bashUrl}?appId=${form.value.appId}&optionNumber=${form.value.optionNumber}&questionNumber=${form.value.questionNumber}`;
  const eventSource = new EventSource(url);
  // 建立连接
  eventSource.onopen = function () {
    console.log("建立连接");
    message.info("流式生成题目中，请稍后");
  };

  // 接受消息
  eventSource.onmessage = function (e) {
    console.log(JSON.parse(e.data));
    // 传递每一条题目给外部组件
    if (props.onSSEGenerate) {
      props.onSSEGenerate(JSON.parse(e.data));
    }
  };

  // 发生错误关闭连接
  eventSource.onerror = function (e) {
    if (e.eventPhase === EventSource.CLOSED) {
      console.log("关闭连接");
      console.error(e);
      eventSource.close();
      message.info("生成题目完成");
    }
  };

  // 关闭抽屉
  visible.value = false;
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
