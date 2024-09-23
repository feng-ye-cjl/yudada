package com.yupi.yudada.model.dto.userAnswer;

import lombok.Data;

@Data
public class AppAnswerCountDTO {

    private Long appId;
    private String appName;
    private Long answerCount;
}
