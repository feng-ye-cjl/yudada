package com.yupi.yudada.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yupi.yudada.manager.AiManager;
import com.yupi.yudada.model.dto.question.QuestionAnswerDTO;
import com.yupi.yudada.model.dto.question.QuestionContentDTO;
import com.yupi.yudada.model.entity.App;
import com.yupi.yudada.model.entity.Question;
import com.yupi.yudada.model.entity.UserAnswer;
import com.yupi.yudada.model.vo.QuestionVO;
import com.yupi.yudada.service.QuestionService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


// 改为ai评分策略
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AiGenerateTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 本地缓存
     */
    private final Cache<String, String> answerCacheMap = Caffeine
            .newBuilder()
            .initialCapacity(1024)
            // 缓存5分钟移除
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .build();


    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";


    /**
     * 构建缓存key
     *
     * @param appId      appId
     * @param choicesStr 用户选择的选项列表
     * @return 缓存的key
     */
    private String buildCacheKey(Long appId, String choicesStr) {
        // 利用appId和用户选项列表构建缓存key
//        return DigestUtil.md5Hex(appId + ":" + choicesStr);
        return appId + ":" + choicesStr;
    }

    // 分布式锁前缀
    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    /**
     * 获取用户输入
     *
     * @param app                    app信息
     * @param questionContentDTOList 问题列表
     * @param choices                选项列表
     * @return
     */
    private String getAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        // 如果有缓存则直接命中返回结果
        // 构建缓存key
        String choicesStr = JSONUtil.toJsonStr(choices);
        String cacheKey = buildCacheKey(appId, choicesStr);
        String answerStr = answerCacheMap.getIfPresent(cacheKey);
        if (StrUtil.isNotBlank(answerStr)) {
            UserAnswer userAnswer = JSONUtil.toBean(answerStr, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(choicesStr);
            return userAnswer;
        }

        // 分布式锁
        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + cacheKey);

        try {
            // 竞争锁，等待3秒，15秒自动释放
            boolean flag = lock.tryLock(3, 15, TimeUnit.SECONDS);
            if (!flag) {
                return null;
            }

            // 1.根据Id查询题目
            LambdaQueryWrapper<Question> questionLambdaQueryWrapper = Wrappers.lambdaQuery(Question.class)
                    .eq(Question::getAppId, appId);
            Question question = questionService.getOne(questionLambdaQueryWrapper);
            // 获取题目列表
            QuestionVO questionVO = QuestionVO.objToVo(question);
            List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

            // 2.调用AI获取结果
            // 封装prompt
            String userMessage = getAiTestScoringUserMessage(app, questionContent, choices);
            // ai生成
            String json = aiManager.doSyncStableRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
            int start = json.indexOf("{");
            int end = json.indexOf("}");
            String result = json.substring(start, end + 1);

            // 缓存结果
            answerCacheMap.put(cacheKey, result);

            // 3.构造返回值
            UserAnswer userAnswer = JSONUtil.toBean(result, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(choicesStr);
            return userAnswer;
        } finally {
            // 锁不为空，且被锁上了
            if (lock != null && lock.isLocked()) {
                // 是当前线程的
                if (lock.isHeldByCurrentThread()) {
                    // 解锁
                    lock.unlock();
                }
            }
        }
    }
}
