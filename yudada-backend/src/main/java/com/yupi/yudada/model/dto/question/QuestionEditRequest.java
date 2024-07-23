package com.yupi.yudada.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑题目请求
 *
 *
 */
@Data
public class QuestionEditRequest implements Serializable {
    /**
     * 编辑题目只需要填写：
     * 1.id
     * 2.questionContentDTO（题目内容）
     */

    /**
     * id
     */
    private Long id;

    /**
     * 题目内容（json格式）
     */
    private List<QuestionContentDTO> questionContent;

    private static final long serialVersionUID = 1L;
}