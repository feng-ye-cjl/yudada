const { generateService } = require("@umijs/openapi");

generateService({
  // 自定义请求方法路径
  requestLibPath: "import request from '@/request'",
  schemaPath: "http://localhost:8101/api/v2/api-docs",
  serversPath: "./src",
});
