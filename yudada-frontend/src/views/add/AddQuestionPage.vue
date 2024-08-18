<template>
  <div id="addQuestionPage">
    <a-form
      :model="form"
      label-align="left"
      style="width: 580px; margin: 0 auto"
      @submit="handleSubmit"
    >
      <h2 style="margin-bottom: 16px; text-align: center">设置题目</h2>
      <a-space size="large" style="margin-bottom: 20px">
        <a-alert :title="app?.appName" />
        <a-button @click="addQuestion(questionList.length)" status="success"
          >底部添加题目
        </a-button>
        <AiGenerateDrawer
          :appId="appId"
          :appName="app?.appName"
          :on-generate="onGenerateSuccess"
        />
      </a-space>
      <a-collapse destroy-on-hide>
        <!-- 一级折叠 -->
        <a-collapse-item v-for="(question, index) of questionList" :key="index">
          <template #header>
            <a-input
              style="width: 300px"
              v-model="question.title"
              @click.stop="prevent"
              placeholder="请输入标题"
            />
          </template>
          <template #extra>
            <a-space>
              <a-button
                size="small"
                @click.stop="prevent"
                @click="addQuestion(index + 1)"
                >添加题目
              </a-button>
              <a-button
                size="small"
                status="danger"
                @click.stop="prevent"
                @click="deleteQuestion(index)"
                >删除题目
              </a-button>
            </a-space>
          </template>
          <!-- 二级折叠 -->
          <a-collapse destroy-on-hide>
            <a-button
              status="success"
              @click="addOption(question, question.options.length)"
              >底部添加选项
            </a-button>
            <a-collapse-item
              :header="`选项${optionIndex + 1}`"
              :key="optionIndex"
              v-for="(option, optionIndex) of question.options"
            >
              <template #extra>
                <a-space size="large">
                  <a-button
                    size="mini"
                    @click.stop="prevent"
                    @click="addOption(question, optionIndex + 1)"
                    >添加选项
                  </a-button>
                  <a-button
                    size="mini"
                    status="danger"
                    @click.stop="prevent"
                    @click="deleteOption(question, optionIndex as number)"
                    >删除选项
                  </a-button>
                </a-space>
              </template>
              <a-form-item label="选项key">
                <a-input v-model="option.title" placeholder="请输入选项key" />
              </a-form-item>
              <a-form-item label="选项值">
                <a-input v-model="option.value" placeholder="请输入选项value" />
              </a-form-item>
              <a-form-item label="选项结果">
                <a-input v-model="option.result" placeholder="请输入选项结果" />
              </a-form-item>
              <a-form-item label="选项得分">
                <a-input-number
                  v-model="option.score"
                  placeholder="请输入选项得分"
                />
              </a-form-item>
            </a-collapse-item>
          </a-collapse>
        </a-collapse-item>
      </a-collapse>
      <div style="width: 100%; text-align: center">
        <a-button
          type="primary"
          html-type="submit"
          style="width: 100px; margin-top: 20px"
          >提交
        </a-button>
      </div>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { defineProps, onMounted, reactive, ref, withDefaults } from "vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import {
  addQuestionUsingPost,
  editQuestionUsingPost,
  listQuestionVoByPageUsingPost,
} from "@/api/questionController";
import { useRouter } from "vue-router";
import AiGenerateDrawer from "@/views/add/components/AiGenerateDrawer.vue";
import { getAppVoByIdUsingGet } from "@/api/appController";

interface Props {
  appId: number;
}

const props = withDefaults(defineProps<Props>(), {
  appId: 0,
});
const form = reactive({
  name: "",
  posts: [{ value: "" }],
});

// app信息
const app = ref<API.AppVO>();
// 题目列表
const questionList = ref<API.QuestionContentDTO[]>([]);

// 阻止展开
const prevent = () => {
  console.log("阻止展开");
};

/**
 * 添加题目
 * @param index
 */
const addQuestion = (index: number) => {
  questionList.value.splice(index, 0, {
    title: "",
    options: [
      {
        key: "",
        result: "",
        score: 0,
        value: "",
      },
    ],
  });
};

/**
 * 删除题目
 * @param index
 */
const deleteQuestion = (index: number) => {
  questionList.value.splice(index, 1);
};

/**
 * 添加选项
 * @param question
 * @param index
 */
const addOption = (question: API.QuestionContentDTO, index: number) => {
  if (!question.options) {
    question.options = [];
  }
  if (question.options.length === 4) {
    message.warning("最多只能添加4个选项");
    return;
  }
  question.options.splice(index, 0, {
    key: "",
    result: "",
    score: 0,
    value: "",
  });
};

/**
 * 删除选项
 * @param question
 * @param index
 */
const deleteOption = (question: API.QuestionContentDTO, index: number) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 1);
};

const router = useRouter();

// 老题目列表
const oldQuestion = ref<API.QuestionVO>();
// 获取信息
const loadData = async () => {
  if (props.appId === 0) {
    return;
  }
  let res;
  // 用分页查找获取一条题目记录
  res = await listQuestionVoByPageUsingPost({
    appId: props.appId,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.data.code === 0) {
    if (res.data.data?.records) {
      oldQuestion.value = res.data.data.records[0];
      // 如果老列表有值则设置
      if (oldQuestion.value) {
        questionList.value = oldQuestion.value.questionContent ?? [];
      }
    }
  } else {
    message.error("获取应用信息失败");
  }
  // 获取app信息
  res = await getAppVoByIdUsingGet({ id: props.appId });
  if (res.data.code === 0) {
    app.value = res.data.data;
  } else {
    message.error("获取应用信息失败");
  }
};
onMounted(() => loadData());

/**
 * 提交
 */
const handleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  let res;
  if (oldQuestion.value?.id) {
    // 编辑
    res = await editQuestionUsingPost({
      id: oldQuestion.value?.id,
      // 一个题目列表
      questionContent: questionList.value,
    });
  } else {
    // 创建
    res = await addQuestionUsingPost({
      appId: props.appId,
      // 一个题目列表
      questionContent: questionList.value,
    });
  }
  if (res.data.code === 0) {
    message.success("设置成功，即将跳转到应用详情页");
    setTimeout(() => {
      router.push(`/app/detail/${props.appId}`);
    }, 3000);
  } else {
    message.error("创建失败，" + res.data.message);
  }
};
// 生成题目成功的回调
const onGenerateSuccess = (result: API.QuestionContentDTO[]) => {
  // 填充题目
  questionList.value = [...questionList.value, ...result];
};
</script>
<style lang="scss" scoped>
#addQuestionPage {
}
</style>
