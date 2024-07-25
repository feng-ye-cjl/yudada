package com.yupi.yudada.model.dto.question;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionContentDTO {
    /**
     * 题目标题
     */
    private String title;
    /**
     * 题目选项列表
     */
    private List<Option> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option {
        /**
         * 选项结果（测评类）
         */
        private String result;
        /**
         * 选项得分（得分类）
         */
        private int score;
        /**
         * 选项值
         */
        private String value;
        /**
         * 选项的key
         */
        private String key;
    }
}
