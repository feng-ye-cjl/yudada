<template>
  <div id="appCard">
    <a-card class="card" hoverable @click="toAppDetail">
      <template #actions>
        <span class="icon-hover"> <IconThumbUp /> </span>
        <span class="icon-hover"> <IconShareInternal /> </span>
      </template>
      <template #cover>
        <div
          :style="{
            height: '204px',
            overflow: 'hidden',
          }"
        >
          <img
            :style="{ width: '100%', transform: 'translateY(-20px)' }"
            alt="dessert"
            :src="app.appIcon"
          />
        </div>
      </template>
      <a-card-meta :title="app.appName" :description="app.appDesc">
        <template #avatar>
          <div
            :style="{ display: 'flex', alignItems: 'center', color: '#1D2129' }"
          >
            <a-avatar
              :size="24"
              :style="{ marginRight: '8px' }"
              :image-url="app.user?.userAvatar"
            />
            <a-typography-text>{{ app.user.userName }}</a-typography-text>
          </div>
        </template>
      </a-card-meta>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { IconShareInternal, IconThumbUp } from "@arco-design/web-vue/es/icon";
import API from "@/api";
import { defineProps, withDefaults } from "vue";
import { useRouter } from "vue-router";

// 接受父组件传递的AppVo
interface Props {
  app: API.AppVO;
}

const props = withDefaults(defineProps<Props>(), {
  // 默认返回一个空对象
  app: () => ({}),
});

const router = useRouter();
/**
 * 跳转到app详情页
 */
const toAppDetail = () => {
  router.push(`/app/detail/${props.app.id}`);
};
</script>

<style lang="scss" scoped>
#appCard {
  .card {
    cursor: pointer;
  }

  .card:hover {
    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.5); /* 鼠标移入时的阴影 */
  }

  .icon-hover {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    transition: all 0.1s;
  }

  .icon-hover:hover {
    background-color: rgb(var(--gray-2));
  }
}
</style>
