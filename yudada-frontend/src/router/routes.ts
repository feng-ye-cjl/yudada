import { RouteRecordRaw } from "vue-router";
import UserLayout from "@/layouts/UserLayout.vue";
import UserLoginPage from "@/views/user/UserLoginPage.vue";
import UserRegisterPage from "@/views/user/UserRegisterPage.vue";
import HelloWorld from "@/components/HelloWorld.vue";
import accessEnum from "@/access/accessEnum";
import AdminUserPage from "@/views/admin/AdminUserPage.vue";
import NoAuth from "@/components/NoAuth.vue";
import AdminAppPage from "@/views/admin/AdminAppPage.vue";
import AdminQuestionPage from "@/views/admin/AdminQuestionPage.vue";
import AdminScoringResultPage from "@/views/admin/AdminScoringResultPage.vue";
import AdminUserAnswerPage from "@/views/admin/AdminUserAnswerPage.vue";
import HomePage from "@/views/HomePage.vue";
import AppDetailPage from "@/views/app/AppDetailPage.vue";
import AddAppPage from "@/views/add/AddAppPage.vue";
import AddQuestionPage from "@/views/add/AddQuestionPage.vue";
import AddScoringResultPage from "@/views/add/AddScoringResultPage.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "主页",
    component: HomePage,
  },
  {
    path: "/admin/user",
    name: "用户管理",
    component: AdminUserPage,
    meta: {
      access: accessEnum.ADMIN,
    },
  },
  {
    path: "/admin/app",
    name: "应用管理",
    component: AdminAppPage,
    meta: {
      access: accessEnum.ADMIN,
    },
  },
  {
    path: "/admin/question",
    name: "题目管理",
    component: AdminQuestionPage,
    meta: {
      access: accessEnum.ADMIN,
    },
  },
  {
    path: "/admin/scoring_result",
    name: "评分管理",
    component: AdminScoringResultPage,
    meta: {
      access: accessEnum.ADMIN,
    },
  },
  {
    path: "/admin/user_answer",
    name: "回答管理",
    component: AdminUserAnswerPage,
    meta: {
      access: accessEnum.ADMIN,
    },
  },
  {
    path: "/app/detail/:id",
    name: "应用详情",
    props: true,
    component: AppDetailPage,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/add/app",
    name: "创建应用",
    component: AddAppPage,
  },
  {
    path: "/add/app/:id",
    name: "修改应用",
    props: true,
    component: AddAppPage,
  },
  {
    path: "/add/question/:appId",
    name: "创建题目",
    component: AddQuestionPage,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/add/scoring_result/:appId",
    name: "创建评分",
    component: AddScoringResultPage,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/hide",
    name: "隐藏页面",
    component: HelloWorld,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/noAuth",
    name: "无权限",
    component: NoAuth,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: UserLoginPage,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: UserRegisterPage,
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
];
