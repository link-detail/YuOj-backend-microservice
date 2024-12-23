package com.liu.yuojbackendjudgeservice.judge.strategy.impl;

import cn.hutool.json.JSONUtil;
import com.liu.yuojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.liu.yuojbackendmodel.codesandbox.JudgeContext;
import com.liu.yuojbackendmodel.codesandbox.JudgeInfo;
import com.liu.yuojbackendmodel.dto.question.JudgeCase;
import com.liu.yuojbackendmodel.dto.question.JudgeConfig;
import com.liu.yuojbackendmodel.entity.Question;
import com.liu.yuojbackendmodel.enums.JudgeInfoMessageEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author 刘渠好
 * @Date 2024-07-23 22:13
 * java判断策略
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        //原题目信息跟代码沙箱执行之后返回的信息对比，来确定提交题目的答题信息
        List<String> outputList = Optional.ofNullable (judgeContext.getOutputList ()).orElse (new ArrayList<> ());
        JudgeInfo judgeInfo = Optional.ofNullable (judgeContext.getJudgeInfo ()).orElse (new JudgeInfo ());
        //获取答题信息
        Long time = Optional.ofNullable (judgeInfo.getTime ()).orElse (0L);  //时间
        Long memory = Optional.ofNullable (judgeInfo.getMemory ()).orElse (0L); //内存
        List<JudgeCase> judgeCases = judgeContext.getJudgeCases ();
        Question question = judgeContext.getQuestion ();
        JudgeInfo judgeResponse = new JudgeInfo ();
        judgeResponse.setMemory (memory);
        judgeResponse.setTime (time);
        //获取原题目的输出用例
        List<String> outCollect = judgeCases.stream ().map (JudgeCase::getOutput).collect (Collectors.toList ());
        //答题正确
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        //判断题目的输入和输出数量是否一致
        if (outCollect.size ()!=outputList.size ()){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeResponse.setMessage (judgeInfoMessageEnum.getText ());
            return judgeResponse;
        }
        //具体判断题目输出和答题输出结果是否一致
        for (int i = 0; i < outCollect.size (); i++) {
            String output = outCollect.get (i);
            if (!outputList.get (i).equals (output)){
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeResponse.setMessage (judgeInfoMessageEnum.getText ());
                return judgeResponse;
            }
        }
        //之后来判断所需内存，时间是否合理
        String config = question.getJudgeConfig ();
        JudgeConfig judgeConfig = JSONUtil.toBean (config, JudgeConfig.class);
        Long needMemory = judgeConfig.getMemoryLimit (); //题目所需内存
        Long needTime = judgeConfig.getTimeLimit (); //题目所需时间
        if (memory>needMemory){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeResponse.setMessage (judgeInfoMessageEnum.getText ());
            return judgeResponse;
        }
        //java程序本身需要额外执行10秒中
        long JAVA_PROGRAM_TIME_COST=10L;
        if ((time-JAVA_PROGRAM_TIME_COST)>needTime){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeResponse.setMessage (judgeInfoMessageEnum.getText ());
            return judgeResponse;
        }
        judgeResponse.setMessage (judgeInfoMessageEnum.getText ());
        return judgeResponse;
    }
}
