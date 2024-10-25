package com.liu.yuojmicroservice.judge.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.liu.yuojmicroservice.common.ErrorCode;
import com.liu.yuojmicroservice.exception.BusinessException;
import com.liu.yuojmicroservice.exception.ThrowUtils;
import com.liu.yuojmicroservice.judge.JudgeService;
import com.liu.yuojmicroservice.judge.codesandbox.CodeSandBox;
import com.liu.yuojmicroservice.judge.codesandbox.factory.CodeSandBoxFactory;
import com.liu.yuojmicroservice.judge.codesandbox.proxy.CodeSandboxProxy;
import com.liu.yuojmicroservice.judge.model.ExecuteCodeRequest;
import com.liu.yuojmicroservice.judge.model.ExecuteCodeResponse;
import com.liu.yuojmicroservice.judge.strategy.model.JudgeContext;
import com.liu.yuojmicroservice.judge.strategy.manager.JudgeManager;
import com.liu.yuojmicroservice.model.dto.question.JudgeCase;
import com.liu.yuojmicroservice.model.dto.questionsubmit.JudgeInfo;
import com.liu.yuojmicroservice.model.entity.Question;
import com.liu.yuojmicroservice.model.entity.QuestionSubmit;
import com.liu.yuojmicroservice.model.enums.JudgeInfoMessageEnum;
import com.liu.yuojmicroservice.model.enums.QuestionSubmitLanguageEnum;
import com.liu.yuojmicroservice.model.enums.QuestionSubmitStatusEnum;
import com.liu.yuojmicroservice.service.QuestionService;
import com.liu.yuojmicroservice.service.QuestionSubmitService;
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
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //1.判断题目提交信息是否存在
        QuestionSubmit questionSubmit = questionSubmitService.getById (questionSubmitId);
        if (questionSubmit==null){
            throw new BusinessException (ErrorCode.PARAMS_ERROR,"提交信息不存在！");
        }
        //判断题目是否存在
        Question question = questionService.getById (questionSubmit.getQuestionId ());
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
        boolean b1 = questionSubmitService.updateById (questionSubmitUpdate);
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
        boolean update = questionService.updateById (question1);
        ThrowUtils.throwIf (!update,ErrorCode.OPERATION_ERROR);
        questionSubmitResult.setJudgeInfo (JSONUtil.toJsonStr (judgeResponse));
        boolean b2 = questionSubmitService.updateById (questionSubmitResult);
        ThrowUtils.throwIf (!b2,ErrorCode.OPERATION_ERROR,"修改失败！");
        //获取最近数据返回
        return questionSubmitService.getById (questionSubmitId);
    }
}
