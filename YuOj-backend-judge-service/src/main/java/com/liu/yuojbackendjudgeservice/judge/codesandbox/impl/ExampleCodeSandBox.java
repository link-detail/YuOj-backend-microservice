package com.liu.yuojbackendjudgeservice.judge.codesandbox.impl;

import com.liu.yuojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.liu.yuojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.liu.yuojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.liu.yuojbackendmodel.codesandbox.JudgeInfo;
import com.liu.yuojbackendmodel.enums.JudgeInfoMessageEnum;
import com.liu.yuojbackendmodel.enums.QuestionSubmitLanguageEnum;
import com.liu.yuojbackendmodel.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author 刘渠好
 * @Date 2024-07-22 19:35
 * 测试代码沙箱（为了跑通流程）
 */
@Slf4j
public class ExampleCodeSandBox implements CodeSandBox {
    /**
     * 沙箱执行代码
     * @param executeCodeRequest  执行代码请求参数
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse ();
        List<String> inputList = executeCodeRequest.getInputList ();
        QuestionSubmitLanguageEnum language = executeCodeRequest.getLanguage ();
        log.info ("处理代码中，语言类似为{}",language.getValue ());
        JudgeInfo judgeInfo = new JudgeInfo ();
        judgeInfo.setMessage (JudgeInfoMessageEnum.ACCEPTED.getText ());
        //设置代码运行的一些信息（内存，时间....）
        judgeInfo.setTime (100L);
        judgeInfo.setMemory (100L);

        return executeCodeResponse
                .builder ()
                .judgeInfo (judgeInfo)
                .outputList (inputList)
                .message ("测试执行成功!")
                .status (QuestionSubmitStatusEnum.SUCCEED.getValue ())
                .build ();
    }
}
