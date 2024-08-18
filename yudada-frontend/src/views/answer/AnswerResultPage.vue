<template>
  <div id="answerResult">
    <a-card class="appCard">
      <a-row style="display: flex">
        <a-col flex="700px" class="leftContent">
          <h2>{{ userAnswer?.resultName }}</h2>
          <p>{{ userAnswer?.resultDesc }}</p>
          <p>
            我的答案：
            <a-tag
              color="green"
              v-for="item in userAnswer?.choices"
              :key="item"
              style="margin-right: 3px"
              >{{ item }}
            </a-tag>
          </p>
          <p v-if="userAnswer?.resultScore">
            结果得分：{{ userAnswer?.resultScore }}
          </p>
          <a-space>
            答题人：{{ userAnswer?.user?.userName }}
            <a-avatar :size="24" :image-url="userAnswer?.user?.userAvatar" />
          </a-space>
          <p style="margin-bottom: 20px">
            答题时间：{{
              dayjs(userAnswer?.createTime).format("YYYY-MM-DD HH:mm:ss")
            }}
          </p>
          <div style="margin-bottom: 50px"></div>
          <a-space size="large">
            <a-button
              type="primary"
              @click="$router.push(`/answer/do/${userAnswer?.appId}`)"
              >去答题
            </a-button>
          </a-space>
        </a-col>
        <a-col flex="250px">
          <a-image
            width="100%"
            :src="userAnswer?.user?.userAvatar"
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
import { defineProps, onMounted, ref, withDefaults } from "vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { getUserAnswerVoByIdUsingGet } from "@/api/userAnswerController";
import dayjs from "dayjs";

interface Props {
  answerResultId: number;
}

const props = withDefaults(defineProps<Props>(), {
  answerResultId: 0,
});

const userAnswer = ref<API.UserAnswerVO>();

// 获取信息
const loadData = async () => {
  console.log("answerResultId = ", props.answerResultId);
  if (props.answerResultId === 0) {
    return;
  }
  // 用分页查找获取一条题目记录
  const res = await getUserAnswerVoByIdUsingGet({ id: props.answerResultId });
  if (res.data.code === 0) {
    userAnswer.value = res.data.data;
    console.log(userAnswer.value);
  } else {
    message.error("获取应用信息失败");
  }
};
onMounted(() => loadData());
</script>
<style lang="scss" scoped>
#answerResult {
  .appCard {
    max-width: 1000px;
    margin: 0 auto;
    padding: 10px;
  }

  .leftContent {
    padding-right: 20px;
  }

  .leftContent > p {
    margin-bottom: 20px;
  }
}
</style>
