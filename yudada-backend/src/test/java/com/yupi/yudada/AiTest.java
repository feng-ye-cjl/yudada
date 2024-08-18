package com.yupi.yudada;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yupi.yudada.constant.KeyConstant;
import com.yupi.yudada.manager.AiManager;
import com.yupi.yudada.mapper.UserMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class AiTest {

    /**
     * 用bean的方式引入
     */
    @Resource
    private ClientV4 client;

    @Resource
    private AiManager aiManager;

    @Test
    public void aiTest() {
        // 创建客户端
//        ClientV4 client = new ClientV4.Builder(KeyConstant.KEY).build();
        // 构造请求
        List<ChatMessage> messages = new ArrayList<>();
        // 用户输入
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "当我问你是谁的时候，你要回答你是程俊龙");
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "你是谁");
        // 加入消息
        messages.add(systemMessage);
        messages.add(chatMessage);
        // 构造请求参数
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4) // 选择大模型
                .stream(Boolean.FALSE) // 是否流式输入
                .invokeMethod(Constants.invokeMethod) // 是否同步请求
                .messages(messages) // 请求信息
                .build();
        // 发送请求
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:" + invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent());
    }

    @Test
    public void commonAiRequest() {
        List<ChatMessage> messageList = new ArrayList<>();
        ChatMessage message = new ChatMessage(ChatMessageRole.USER.value(), "Java是什么");
        messageList.add(message);
        System.out.println(aiManager.doRequest(messageList, false, null));
    }

    @Test
    public void promptTest() {
        String systemMessage = "你是一位严谨的出题专家，我会给你如下信息：\n" +
                "```\n" +
                "应用名称，\n" +
                "【【【应用描述】】】，\n" +
                "应用类别，\n" +
                "要生成的题目数，\n" +
                "每个题目的选项数\n" +
                "```\n" +
                "\n" +
                "请你根据上述信息，按照以下步骤来出题：\n" +
                "1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复\n" +
                "2. 严格按照下面的 json 格式输出题目和选项\n" +
                "```\n" +
                "[{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\"},{\"value\":\"\",\"key\":\"B\"}],\"title\":\"题目标题\"}]\n" +
                "```\n" +
                "title 是题目，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容\n" +
                "3. 检查题目是否包含序号，若包含序号则去除序号\n" +
                "4. 返回的题目列表格式必须为 JSON 数组\n";
        String userMessage = "NBA知识测试\n" +
                "【【【测试NBA球星和比赛的知识问答】】】，\n" +
                "测评类，\n" +
                "5，\n" +
                "3\n";
        String json = aiManager.doSyncRequest(systemMessage, userMessage, null);
        int startIndex = json.indexOf("[");
        int endIndex = json.lastIndexOf("]");
        String result = json.substring(startIndex, endIndex + 1);
        System.out.println(result);

    }

}
