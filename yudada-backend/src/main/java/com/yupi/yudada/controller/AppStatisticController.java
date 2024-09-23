package com.yupi.yudada.controller;

import com.yupi.yudada.common.BaseResponse;
import com.yupi.yudada.common.ErrorCode;
import com.yupi.yudada.common.ResultUtils;
import com.yupi.yudada.exception.ThrowUtils;
import com.yupi.yudada.mapper.UserAnswerMapper;
import com.yupi.yudada.model.dto.userAnswer.AppAnswerCountDTO;
import com.yupi.yudada.model.dto.userAnswer.AppAnswerResultCountDTO;
import groovy.util.logging.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class AppStatisticController {

    @Resource
    private UserAnswerMapper userAnswerMapper;

    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }
}
