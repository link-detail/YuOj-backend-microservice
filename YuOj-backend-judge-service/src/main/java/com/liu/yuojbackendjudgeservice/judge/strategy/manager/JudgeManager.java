package com.liu.yuojbackendjudgeservice.judge.strategy.manager;

import com.liu.yuojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.liu.yuojbackendjudgeservice.judge.strategy.impl.DefaultJudgeStrategy;
import com.liu.yuojbackendjudgeservice.judge.strategy.impl.JavaLanguageJudgeStrategy;
import com.liu.yuojbackendmodel.codesandbox.JudgeContext;
import com.liu.yuojbackendmodel.codesandbox.JudgeInfo;
import com.liu.yuojbackendmodel.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * @Author 刘渠好
 * @Date 2024-07-23 23:14
 * 判题管理(简化应用)
 */
@Service
public class JudgeManager{
    /**
     * 执行判题
     */
    public JudgeInfo doJudge(JudgeContext judgeContext){
        //根据代码语言来选择策略模式
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit ();
        String language = questionSubmit.getLanguage ();
        //模式策略
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy ();
        if (language.equals ("java")){
            judgeStrategy=new JavaLanguageJudgeStrategy ();
            return judgeStrategy.doJudge (judgeContext);
        }
        return judgeStrategy.doJudge (judgeContext);

    }
}
