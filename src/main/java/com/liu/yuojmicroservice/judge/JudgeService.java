package com.liu.yuojmicroservice.judge;

import com.liu.yuojmicroservice.model.entity.QuestionSubmit;

/**
 * @Author 刘渠好
 * @Date 2024-07-22 22:11
 * 判题服务
 */
public interface JudgeService {

    /**
     * 判题
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
