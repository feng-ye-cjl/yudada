<template>
  <div id="doAnswerPage">
    <a-card class="appCard">
      <a-row>
        <a-col flex="auto" class="leftContent">
          <h2>{{ app?.appName }}</h2>
          <p>{{ app?.appDesc }}</p>
          <h3>
            {{
              currentQuestion?.title.split(".").length === 2
                ? ""
                : currentNum + "."
            }}{{ currentQuestion?.title }}
          </h3>
          <a-radio-group
            direction="vertical"
            v-model="currentAnswer"
            :options="currentOptions"
            @change="(value) => (answerList[currentNum - 1] = value)"
          />
          <div style="margin-bottom: 20px"></div>
          <a-space size="large">
            <a-button
              v-if="currentNum < questionList.length"
              type="primary"
              :disabled="!currentAnswer"
              @click="() => currentNum++"
              >下一题
            </a-button>
            <a-button v-if="currentNum > 1" @click="() => currentNum--"
              >上一题
            </a-button>
            <a-button
              type="primary"
              v-if="currentNum === questionList.length"
              :loading="isSubmit"
              @click="handleSubmit"
              >提交答案
            </a-button>
          </a-space>
        </a-col>
        <a-col flex="300px">
          <a-image
            width="100%"
            :src="app?.appIcon"
            style="
              cursor: pointer;
              border: 1px solid #e8e8e8;
              border-radius: 10px;
            "
          />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import {
  computed,
  defineProps,
  onMounted,
  ref,
  watchEffect,
  withDefaults,
} from "vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import { useRouter } from "vue-router";
import { getAppVoByIdUsingGet } from "@/api/appController";
import {
  addUserAnswerUsingPost,
  generateUserAnswerIdUsingGet,
} from "@/api/userAnswerController";

interface Props {
  appId: number;
}

const props = withDefaults(defineProps<Props>(), {
  appId: 0,
});
// 题目列表
const questionList = ref<API.QuestionContentDTO[]>([]);
// app信息
const app = ref<API.AppVO>();
// 当前题号（默认第一条）
const currentNum = ref(1);
// 当前题目
const currentQuestion = ref<API.QuestionVO>({});
// 当前选项
const currentOptions = computed(() => {
  return currentQuestion.value?.options
    ? currentQuestion.value?.options.map((option: any) => {
        return {
          label: `${option.key}.${option.value}`,
          value: option.key,
        };
      })
    : [];
});
// 当前答案
const currentAnswer = ref();
// 答案列表
const answerList = ref([]);
// 唯一id
const id = ref<number>();

/**
 * 生成唯一id
 */
const generateId = async () => {
  let res = await generateUserAnswerIdUsingGet();
  if (res.data.code === 0) {
    id.value = res.data.data;
  } else {
    message.error("生成唯一id失败，" + res.data.message);
  }
};

watchEffect(() => {
  generateId();
});

// 监听题号切换题目
watchEffect(() => {
  currentQuestion.value = questionList.value[currentNum.value - 1];
  currentAnswer.value = answerList.value[currentNum.value - 1];
});

const router = useRouter();

// app和题目信息
const loadData = async () => {
  if (props.appId === 0) {
    return;
  }
  // 获取app信息
  const appRes = await getAppVoByIdUsingGet({ id: props.appId });
  if (appRes.data.code === 0) {
    console.log("app = ", appRes.data);
    app.value = appRes.data.data;
  }
  // 获取题目信息
  const res = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.data.code === 0) {
    if (res.data.data?.records) {
      questionList.value = res.data.data.records[0].questionContent;
    }
  } else {
    message.error("获取应用信息失败");
  }
};
onMounted(() => loadData());

const isSubmit = ref(false);
/**
 * 提交
 */
const handleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  isSubmit.value = true;
  // 提交时携带唯一id
  const res = await addUserAnswerUsingPost({
    appId: props.appId,
    choices: answerList.value,
    id: id.value,
  });
  if (res.data.code === 0) {
    message.success("提交成功");
    const answerId = res.data.data;
    // setTimeout(async () => {
    //   await router.push(`/answer/result/${answerId}`);
    // }, 2000);
    await router.push(`/answer/result/${answerId}`);
  } else {
    message.error(res.data.message + "");
  }
  isSubmit.value = false;
};
</script>
<style lang="scss" scoped>
#doAnswerPage {
  .appCard {
    max-width: 1000px;
    margin: 0 auto;
    padding: 10px;
  }
}
</style>
