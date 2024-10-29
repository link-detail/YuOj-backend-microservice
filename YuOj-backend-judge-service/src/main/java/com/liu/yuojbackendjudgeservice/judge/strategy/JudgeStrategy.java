package com.liu.yuojbackendjudgeservice.judge.strategy;

import com.liu.yuojbackendmodel.codesandbox.JudgeContext;
import com.liu.yuojbackendmodel.codesandbox.JudgeInfo;

/**
 * @Author 刘渠好
 * @Date 2024-07-23 22:12
 * 判题策略
 */
public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);
}
