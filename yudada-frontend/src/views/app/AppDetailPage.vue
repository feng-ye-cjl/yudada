<template>
  <div id="appDetailPage">
    <a-card class="appCard">
      <a-row>
        <a-col flex="auto" class="leftContent">
          <h2>{{ app?.appName }}</h2>
          <p>应用类型：{{ APP_TYPE_MAP[app?.appType] }}</p>
          <p>评分策略：{{ APP_SCORING_STRATEGY_MAP[app?.scoringStrategy] }}</p>
          <a-space>
            作者：{{ app?.user.userName }}
            <a-avatar :size="24" :image-url="app?.user?.userAvatar" />
          </a-space>
          <p style="margin-bottom: 80px">
            创建时间：{{ dayjs(app?.createTime).format("YYYY-MM-DD HH:mm:ss") }}
          </p>
          <a-space size="large">
            <a-button type="primary" :href="`/answer/do/${id}`"
              >开始答题
            </a-button>
            <a-button>分享应用</a-button>
            <a-button v-if="IsMe" :href="`/add/question/${id}`"
              >设置题目
            </a-button>
            <a-button v-if="IsMe" :href="`/add/scoring_result/${id}`"
              >设置评分
            </a-button>
            <a-button v-if="IsMe" :href="`/add/app/${id}`">修改应用</a-button>
          </a-space>
        </a-col>
        <a-col flex="320px">
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
import message from "@arco-design/web-vue/es/message";
import { deleteAppUsingPost, getAppVoByIdUsingGet } from "@/api/appController";
import API from "@/api";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "../../constant/app";
import dayjs from "dayjs";
import { useLoginUserStore } from "@/store/userStore";
import { storeToRefs } from "pinia";
import { computed, defineProps, ref, watchEffect, withDefaults } from "vue";

interface Props {
  id: number;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => 0,
});

// app列表
const app = ref<API.AppVO>();
/**
 * 获取应用列表
 */
const getApp = async () => {
  if (props.id === 0) {
    message.warning("请选择应用");
    return;
  }
  console.log(props.id);
  const res = await getAppVoByIdUsingGet({ id: props.id });
  if (res.data.code === 0) {
    app.value = res.data.data;
  } else {
    message.error(res.data.message as string);
  }
  console.log(app.value);
};

// 获取当前登录用户
const userStore = useLoginUserStore();
const { loginUser } = storeToRefs(userStore);
const IsMe = computed(() => loginUser.value.id === app.value?.userId);

watchEffect(() => {
  getApp();
});
</script>

<style lang="scss" scoped>
#appDetailPage {
  .appCard {
    max-width: 1000px;
    margin: 0 auto;
    padding: 10px;
  }

  .leftContent {
    padding-right: 20px;
    border-right: 1px solid #e8e8e8;
  }

  .leftContent > p {
    margin-bottom: 20px;
  }
}
</style>
