package com.liu.yuojbackendjudgeservice.judge.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.liu.yuojbackendcommon.common.ErrorCode;
import com.liu.yuojbackendcommon.exception.BusinessException;
import com.liu.yuojbackendcommon.exception.ThrowUtils;
import com.liu.yuojbackendjudgeservice.judge.JudgeService;
import com.liu.yuojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.liu.yuojbackendjudgeservice.judge.codesandbox.factory.CodeSandBoxFactory;
import com.liu.yuojbackendjudgeservice.judge.codesandbox.proxy.CodeSandboxProxy;
import com.liu.yuojbackendjudgeservice.judge.strategy.manager.JudgeManager;
import com.liu.yuojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.liu.yuojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.liu.yuojbackendmodel.codesandbox.JudgeContext;
import com.liu.yuojbackendmodel.codesandbox.JudgeInfo;
import com.liu.yuojbackendmodel.dto.question.JudgeCase;
import com.liu.yuojbackendmodel.entity.Question;
import com.liu.yuojbackendmodel.entity.QuestionSubmit;
import com.liu.yuojbackendmodel.enums.JudgeInfoMessageEnum;
import com.liu.yuojbackendmodel.enums.QuestionSubmitLanguageEnum;
import com.liu.yuojbackendmodel.enums.QuestionSubmitStatusEnum;
import com.liu.yuojbackendserviceclient.service.QuestionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author 刘渠好
 * @Date 2024-07-22 22:12
 */
@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

    @Value ("${codesandbox.type}")
    private String type;

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //1.判断题目提交信息是否存在
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById (questionSubmitId);
        if (questionSubmit==null){
            throw new BusinessException (ErrorCode.PARAMS_ERROR,"提交信息不存在！");
        }
        //判断题目是否存在
        Question question = questionFeignClient.getQuestionById (questionSubmit.getQuestionId ());
        if (question==null){
            throw new BusinessException (ErrorCode.NOT_FOUND_ERROR,"题目不存在!");
        }
        //要是重复提交，就重复继续答题，不用在次数进行判断也行（自己的看法）
//        //2.如果提交题目状态不是等待中，就不用重复执行
//        if (!questionSubmit.getStatus ().equals (QuestionSubmitStatusEnum.WAITING.getValue ())){
//            throw new BusinessException (ErrorCode.OPERATION_ERROR,"该题目正在判题中!");
//        }
        //3.更改题目的状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit ();
        questionSubmitUpdate.setId (questionSubmitId);
        questionSubmitUpdate.setStatus (QuestionSubmitStatusEnum.RUNNING.getValue ());
        boolean b1 = questionFeignClient.updateQuestionSubmitById (questionSubmitUpdate);
        if (!b1){
            throw new BusinessException (ErrorCode.SYSTEM_ERROR,"题目状态信息更新失败!");
        }
        //4.调用沙箱，获取执行结果
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance (type);
        CodeSandboxProxy codeSandboxProxy = new CodeSandboxProxy (codeSandBox);
        //获取题目的输入用例
        List<JudgeCase> list = JSONUtil.toList (question.getJudgeCase (), JudgeCase.class);
        List<String> inputList = list.stream ().map (JudgeCase::getInput).collect (Collectors.toList ());
        ExecuteCodeRequest build = ExecuteCodeRequest.builder ().
                code (questionSubmit.getCode ()).
                language (QuestionSubmitLanguageEnum.getEnumByValue (questionSubmit.getLanguage ())).
                inputList (inputList)
                .build ();
        //代码沙箱执行结果
        ExecuteCodeResponse executeCodeResponse = codeSandboxProxy.executeCode (build);
        List<String> ouputList = executeCodeResponse.getOutputList ();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo ();
        //5.根据代码沙箱的执行结果设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext ();
        judgeContext.setOutputList (ouputList);
        judgeContext.setJudgeInfo (judgeInfo);
        judgeContext.setJudgeCases (JSONUtil.toList (question.getJudgeCase (),JudgeCase.class));
        judgeContext.setQuestion (question);
        judgeContext.setQuestionSubmit (questionSubmit);
        JudgeInfo judgeResponse = judgeManager.doJudge (judgeContext);

        QuestionSubmit questionSubmitResult = new QuestionSubmit ();
        if (judgeResponse.getMessage ().equals (JudgeInfoMessageEnum.ACCEPTED.getText ())){
            questionSubmitResult.setStatus (QuestionSubmitStatusEnum.SUCCEED.getValue ());
        }else {
            questionSubmitResult.setStatus (QuestionSubmitStatusEnum.FAILED.getValue ());
        }
        //6.修改数据库中的判题结果
        questionSubmitResult.setId (questionSubmitId);
        //题目提交数+1
        Question question1 = new Question ();
        question1.setId (questionSubmit.getQuestionId ());
        question1.setSubmitNum (ObjectUtil.defaultIfNull (question1.getSubmitNum (),0)+1);
        boolean update = questionFeignClient.updateQuestionById (question1);
        ThrowUtils.throwIf (!update,ErrorCode.OPERATION_ERROR);
        questionSubmitResult.setJudgeInfo (JSONUtil.toJsonStr (judgeResponse));
        boolean b2 = questionFeignClient.updateQuestionSubmitById (questionSubmitResult);
        ThrowUtils.throwIf (!b2,ErrorCode.OPERATION_ERROR,"修改失败！");
        //获取最近数据返回
        return questionFeignClient.getQuestionSubmitById (questionSubmitId);
    }
}
