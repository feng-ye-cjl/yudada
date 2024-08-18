package com.yupi.yudada.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * ai生成题目请求类
 */
@Data
public class AiGenerateQuestionRequest implements Serializable {

    /**
     * id
     */
    private Long appId;

    /**
     * 题目数
     */
    int questionNumber = 5;

    /**
     * 选项数
     */
    int optionNumber = 2;

    private static final long serialVersionUID = 1L;
}
