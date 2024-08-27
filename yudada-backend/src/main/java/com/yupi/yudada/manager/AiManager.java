package com.yupi.yudada.manager;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class AiManager {

    private static final float STABLE_TEMPERATURE = 0.05f;
    private static final float UNSTABLE_TEMPERATURE = 0.99f;

    @Resource
    private ClientV4 client;


    /**
     * 稳定请求
     */
    public String doSyncStableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, STABLE_TEMPERATURE);
    }

    /**
     * 不稳定请求
     */
    public String doSyncUnStableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, UNSTABLE_TEMPERATURE);
    }

    /**
     * 同步请求
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @param temperature   稳定参数
     * @return 结果
     */
    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, temperature);
    }

    /**
     * 通用请求
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @param stream        是否流
     * @param temperature   稳定参数
     * @return 结果
     */
    public String doRequest(String systemMessage, String userMessage, Boolean stream, Float temperature) {
        List<ChatMessage> messageList = new ArrayList<>();
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        ChatMessage userMsg = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messageList.add(systemMsg);
        messageList.add(userMsg);
        // 直接返回结果
        return doRequest(messageList, stream, temperature);
    }

    /**
     * 通用ai请求方法
     *
     * @param messagesList 用户信息
     * @param stream       是否流式输入
     * @param temperature  回答稳定程度
     * @return ai回答结果
     */
    public String doRequest(List<ChatMessage> messagesList, Boolean stream, Float temperature) {
        // 构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4) // 选择大模型
                .stream(stream) // 是否流式输入
                .invokeMethod(Constants.invokeMethod) // 是否同步请求
                .temperature(temperature)
                .messages(messagesList) // 请求信息
                .build();
        // 发送请求
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        // 返回结果
        return invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
    }


    /**
     * 通用流式ai请求方法（简化消息传递）
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @param temperature   稳定参数
     */
    public Flowable<ModelData> doStreamRequest(String systemMessage, String userMessage, Float temperature) {
        List<ChatMessage> messageList = new ArrayList<>();
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        ChatMessage userMsg = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messageList.add(systemMsg);
        messageList.add(userMsg);
        // 调用通用请求
        return doStreamRequest(messageList, temperature);
    }


    /**
     * 通用流式ai请求方法
     *
     * @param messagesList 用户信息
     * @param temperature  回答稳定程度
     * @return ai回答结果
     */
    public Flowable<ModelData> doStreamRequest(List<ChatMessage> messagesList, Float temperature) {
        // 构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4) // 选择大模型
                .stream(Boolean.TRUE) // 确定流式输出
                .invokeMethod(Constants.invokeMethod) // 是否同步请求
                .temperature(temperature)
                .messages(messagesList) // 请求信息
                .build();
        // 发送请求
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        // 返回Flowable对象
        return invokeModelApiResp.getFlowable();
    }
}










