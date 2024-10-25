package com.liu.yuojmicroservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liu.yuojmicroservice.common.BaseResponse;
import com.liu.yuojmicroservice.common.ErrorCode;
import com.liu.yuojmicroservice.common.ResultUtils;
import com.liu.yuojmicroservice.exception.BusinessException;
import com.liu.yuojmicroservice.exception.ThrowUtils;
import com.liu.yuojmicroservice.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.liu.yuojmicroservice.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.liu.yuojmicroservice.model.entity.QuestionSubmit;
import com.liu.yuojmicroservice.model.entity.User;
import com.liu.yuojmicroservice.model.vo.questionsubmit.QuestionSubmitVO;
import com.liu.yuojmicroservice.service.QuestionService;
import com.liu.yuojmicroservice.service.QuestionSubmitService;
import com.liu.yuojmicroservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @Author 刘渠好
 * @Date 2024-07-20 22:21
 * 提交题目接口
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
//表示过时
@Deprecated
public class QuestionSubmitController {


    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    /**
     * 提交题目
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpSession session){
        Long questionId = questionSubmitAddRequest.getQuestionId ();
        //校验参数
        if (questionSubmitAddRequest.getQuestionId () == null || questionId<=0){
            throw new BusinessException (ErrorCode.PARAMS_ERROR);
        }
        //当前用户
        User loginUser = userService.getLoginUser (session);
        return ResultUtils.success (questionSubmitService.doQuestionSubmit(questionSubmitAddRequest,loginUser));
    }

    /**
     * 分页获取题目提交列表(除了管理员之外，普通用户只能看到非答案，提交代码等公开信息)
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,HttpSession session){
        //获取分页信息
        long current = questionSubmitQueryRequest.getCurrent ();
        long pageSize = questionSubmitQueryRequest.getPageSize ();
        //防止爬虫
        ThrowUtils.throwIf (pageSize>=10,ErrorCode.PARAMS_ERROR);
        //查询
        Page<QuestionSubmit> page = questionSubmitService.page (new Page<> (current, pageSize), questionSubmitService.getQueryWrapper (questionSubmitQueryRequest));
        return ResultUtils.success (questionSubmitService.getQuestionSubmitVOPage(page,session));


    }
}
