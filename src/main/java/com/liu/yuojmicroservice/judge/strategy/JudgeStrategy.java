package com.liu.yuojmicroservice.judge.strategy;

import com.liu.yuojmicroservice.judge.strategy.model.JudgeContext;
import com.liu.yuojmicroservice.model.dto.questionsubmit.JudgeInfo;

/**
 * @Author 刘渠好
 * @Date 2024-07-23 22:12
 * 判题策略
 */
public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);
}
