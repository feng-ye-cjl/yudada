<template>
  <div id="addQuestionPage">
    <h2 style="margin-bottom: 16px">创建题目</h2>
    <a-form :model="form" :style="{ width: '600px' }">
      <a-form-item label="应用id"> {{ appId }}</a-form-item>
      <a-form-item label="题目列表" :content-flex="false" :merge-props="false">
        <a-button @click="addQuestion(questionList.length)"
          >底部添加题目
        </a-button>
        <div v-for="(question, index) of questionList" :key="index">
          <a-form-item field="name" label="Username">
            <a-input
              v-model="form.name"
              placeholder="please enter your username..."
            />
          </a-form-item>
          <a-form-item
            v-for="(post, index) of form.posts"
            :field="`posts[${index}].value`"
            :label="`Post-${index}`"
            :key="index"
          >
            <a-input
              v-model="post.value"
              placeholder="please enter your post..."
            />
          </a-form-item>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { useLoginUserStore } from "@/store/userStore";
import { useRouter } from "vue-router";
import { defineProps, reactive, ref, withDefaults } from "vue";
import API from "@/api";

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: "",
});

// userStore
const userStore = useLoginUserStore();
// 路由
const router = useRouter();
const form = reactive({
  name: "",
  posts: [{ value: "" }],
});

const questionList = ref<API.QuestionContentDTO[]>([]);

/**
 * 添加题目
 * @param index
 */
const addQuestion = (index: number) => {
  questionList.value.splice(index, 0, { title: "", options: [] });
};

/**
 * 删除题目
 * @param index
 */
const deleteQuestion = (index: number) => {
  questionList.value.splice(index, 1);
};
</script>
<style lang="scss" scoped>
#addQuestionPage {
}
</style>
