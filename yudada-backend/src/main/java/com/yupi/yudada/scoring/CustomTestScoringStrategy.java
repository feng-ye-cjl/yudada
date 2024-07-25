package com.yupi.yudada.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yupi.yudada.model.dto.question.QuestionContentDTO;
import com.yupi.yudada.model.entity.App;
import com.yupi.yudada.model.entity.Question;
import com.yupi.yudada.model.entity.ScoringResult;
import com.yupi.yudada.model.entity.UserAnswer;
import com.yupi.yudada.model.vo.QuestionVO;
import com.yupi.yudada.service.QuestionService;
import com.yupi.yudada.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ScoringStrategyConfig(appType = 1, scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;


    /**
     * 执行评分
     *
     * @param choices
     * @param app
     * @return
     * @throws Exception
     */
    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        // 1. 根据id查询题目和评分结果信息
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper =
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, app.getId());
        Question question = questionService.getOne(questionLambdaQueryWrapper);
        LambdaQueryWrapper<ScoringResult> scoringResultLambdaQueryWrapper =
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, app.getId());
        List<ScoringResult> scoringResults = scoringResultService.list(scoringResultLambdaQueryWrapper);

        // 2.统计用户每个选择对应的属性个数（I 10个， E 5个）
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        Map<String, Integer> optionCount = new HashMap<>();
        // 遍历题目列表
        for (QuestionContentDTO questionContentDTO : questionContent) {
            // 遍历答案列表
            for (String answer : choices) {
                // 遍历选项列表
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    // 如果选项和答案匹配
                    if (option.getKey().equals(answer)) {
                        // 获取结果属性
                        String result = option.getResult();
                        // 如果map中没有则放入一个0
                        if (!optionCount.containsKey(result)) {
                            optionCount.put(result, 0);
                        }
                        // 如果有则+1
                        optionCount.put(result, optionCount.get(result) + 1);
                    }
                }
            }
        }

        // 3.遍历每种评分策略的结果,计算哪个结果的得分更高
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResults.get(0);

        for (ScoringResult result : scoringResults) {
            // 获取结果属性
            List<String> resultProp = JSONUtil.toList(result.getResultProp(), String.class);
            int score = resultProp.stream()
                    .mapToInt(prop -> optionCount.getOrDefault(prop, 0))
                    .sum();

            if (score > maxScore) {
                maxScore = score;
                maxScoringResult = result;
            }
        }

        // 4.构造并填充信息,返回用户回答结果
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setId(maxScoringResult.getId());
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        return userAnswer;
    }
}
