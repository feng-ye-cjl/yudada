package com.yupi.yudada.mapper;

import com.yupi.yudada.model.dto.userAnswer.AppAnswerCountDTO;
import com.yupi.yudada.model.dto.userAnswer.AppAnswerResultCountDTO;
import com.yupi.yudada.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author cjl
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
 * @createDate 2024-07-12 17:47:24
 * @Entity com.yupi.yudada.model.entity.UserAnswer
 */
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {
    @Select("select user_answer.appId, app.appName as appName, count(user_answer.userId) as answerCount\n" +
            "from user_answer\n" +
            "         join app on\n" +
            "    app.id = user_answer.appId\n" +
            "group by appId\n" +
            "order by answerCount desc;")
    List<AppAnswerCountDTO> doAppAnswerCount();

    @Select("select resultName, count(resultName) as resultCount from user_answer " +
            "where appId = #{appId} group by resultName order by resultCount desc")
    List<AppAnswerResultCountDTO> doAppAnswerResultCount(Long appId);
}




