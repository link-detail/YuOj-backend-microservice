package com.liu.yuojbackendquestionservice.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.liu.yuojbackendcommon.annotation.AuthCheck;
import com.liu.yuojbackendcommon.common.BaseResponse;
import com.liu.yuojbackendcommon.common.DeleteRequest;
import com.liu.yuojbackendcommon.common.ErrorCode;
import com.liu.yuojbackendcommon.common.ResultUtils;
import com.liu.yuojbackendcommon.constant.UserConstant;
import com.liu.yuojbackendcommon.exception.BusinessException;
import com.liu.yuojbackendcommon.exception.ThrowUtils;
import com.liu.yuojbackendmodel.dto.question.*;
import com.liu.yuojbackendmodel.dto.questionsubmit.QuestionSubmitAddRequest;
import com.liu.yuojbackendmodel.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.liu.yuojbackendmodel.entity.Question;
import com.liu.yuojbackendmodel.entity.QuestionSubmit;
import com.liu.yuojbackendmodel.entity.User;
import com.liu.yuojbackendmodel.vo.question.QuestionVO;
import com.liu.yuojbackendmodel.vo.questionsubmit.QuestionSubmitVO;
import com.liu.yuojbackendserviceclient.service.QuestionService;
import com.liu.yuojbackendserviceclient.service.QuestionSubmitService;
import com.liu.yuojbackendserviceclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * @Author 刘渠好
 * @Date 2024-07-18 23:35
 */
@RestController
@Slf4j
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    private final static Gson GSON = new Gson ();

    //region crud

    /**
     * 新增题目（管理员和自己可以新增）
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpSession session){
        //校验参数
        if (questionAddRequest==null){
            throw new BusinessException (ErrorCode.PARAMS_ERROR,"参数不可以为空！");
        }
        Question question = new Question ();
        BeanUtils.copyProperties (questionAddRequest, question);
        //处理json格式数据及其校验参数
        setQuestionValue(questionAddRequest,question,true);
        //获取当前用户
        User loginUser = userService.getLoginUser (session);
        question.setUserId (loginUser.getId ());
        //保存
        boolean save = questionService.save (question);
        ThrowUtils.throwIf (!save, ErrorCode.OPERATION_ERROR);
        Long id = question.getId ();
        return ResultUtils.success (id);
    }

    /**
     * 处理json格式数据  (这里抽出一个基本公共类，更灵活，更便于维护)
     * @param request
     * @param question
     */
    private void setQuestionValue(QuestionBaseRequest request, Question question, boolean add){
        List<String> tags = request.getTags ();
        if (CollUtil.isNotEmpty (tags)){
            question.setTags (GSON.toJson (tags));
        }
        List<JudgeCase> judgeCase = request.getJudgeCase ();
        if (CollUtil.isNotEmpty (judgeCase)){
            question.setJudgeCase (GSON.toJson (judgeCase));
        }
        JudgeConfig judgeConfig = request.getJudgeConfig ();
        if (judgeConfig!=null){
            question.setJudgeConfig (GSON.toJson (judgeConfig));
        }
        //校验参数
        questionService.validQuestion (question,add);
    }
    /**
     * 删除题目（管理员和自己可以删除）
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpSession session){
        //获取当前用户
        User loginUser = userService.getLoginUser (session);
        //校验参数
        if (deleteRequest == null || deleteRequest.getId ()<=0){
            throw new BusinessException (ErrorCode.PARAMS_ERROR,"请求参数不可以为空!");
        }
        //判断是否有这个题目
        Question byId = questionService.getById (deleteRequest.getId ());
        if (byId==null){
            throw new BusinessException (ErrorCode.NOT_FOUND_ERROR,"该题目不存在!");
        }
        //仅仅是自己或者是管理员可以删除
        if (!userService.isAdmin (loginUser)&&!loginUser.getId ().equals (byId.getUserId ())){
            throw new BusinessException (ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success (questionService.removeById (deleteRequest.getId ()));
    }

    /**
     * 更新题目(只限于管理员）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest){
        //校验参数
        if (questionUpdateRequest == null||questionUpdateRequest.getId ()<=0){
            throw new BusinessException (ErrorCode.PARAMS_ERROR,"请求参数不可以为空!");
        }
        Question question = new Question ();
        BeanUtils.copyProperties (questionUpdateRequest, question);
        //处理json数据并且校验参数
        setQuestionValue (questionUpdateRequest,question,false);
        //只有存在才可以修改
        Question byId = questionService.getById (questionUpdateRequest.getId ());
        if (byId==null){
            throw new BusinessException (ErrorCode.NOT_FOUND_ERROR,"修改的题目不存在!");
        }
        //修改
        boolean b = questionService.updateById (question);
        ThrowUtils.throwIf (!b, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success (b);

    }

    /**
     * 根据查找题目（脱敏）
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id){
        //校验参数
        if (id<=0){
            throw new BusinessException (ErrorCode.PARAMS_ERROR,"题目id不能为空!");
        }
        //查找题目
        Question byId = questionService.getById (id);
        ThrowUtils.throwIf (byId==null, ErrorCode.NOT_FOUND_ERROR);
        //返回脱敏数据
        return ResultUtils.success (questionService.getQuestionVO (byId));
    }

    /**
     * 根据查找题目
     */
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionVOById(long id,HttpSession session){
        //获取当前用户
        User loginUser = userService.getLoginUser (session);
        //校验参数
        if (id<=0){
            throw new BusinessException (ErrorCode.PARAMS_ERROR,"题目id不能为空!");
        }
        //查找题目
        Question question = questionService.getById (id);
        ThrowUtils.throwIf (question==null, ErrorCode.NOT_FOUND_ERROR);
        //只有管理员或者是自己可以查看
        if (!userService.isAdmin (session)&&!loginUser.getId ().equals (question.getUserId ())){
            throw new BusinessException (ErrorCode.NO_AUTH_ERROR);
        }
        //返回脱敏数据
        return ResultUtils.success (question);
    }

    /**
     * 分页获取列表(封装类)
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest){
        //获取分页列表
        long pageSize = questionQueryRequest.getPageSize ();  //页面大小
        long current = questionQueryRequest.getCurrent ();  //当前页
        //限制爬虫
        ThrowUtils.throwIf (pageSize>20,ErrorCode.PARAMS_ERROR);
        Page<Question> page = questionService.page (new Page<> (current, pageSize), questionService.getQueryWrapper (questionQueryRequest));
        return ResultUtils.success (questionService.getQuestionVOPage (page));
    }

    /**
     * 分页获取当前用户的创建的资源列表
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,HttpSession session){
        //校验参数
        if (questionQueryRequest==null){
            throw new BusinessException (ErrorCode.PARAMS_ERROR);
        }
        //获取当前用户id(自己差自己的)
        User loginUser = userService.getLoginUser (session);
        questionQueryRequest.setUserId (loginUser.getId());

        //获取分页列表
        long pageSize = questionQueryRequest.getPageSize ();  //页面大小
        long current = questionQueryRequest.getCurrent ();  //当前页
        //限制爬虫
        ThrowUtils.throwIf (pageSize>20,ErrorCode.PARAMS_ERROR);
        Page<Question> page = questionService.page (new Page<> (current, pageSize), questionService.getQueryWrapper (questionQueryRequest));
        return ResultUtils.success (questionService.getQuestionVOPage (page));
    }

    /**
     * 分页获取题目列表（仅管理员）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest){
        //获取分页列表
        long pageSize = questionQueryRequest.getPageSize ();  //页面大小
        long current = questionQueryRequest.getCurrent ();  //当前页
        //限制爬虫
        ThrowUtils.throwIf (pageSize>20,ErrorCode.PARAMS_ERROR);
        Page<Question> page = questionService.page (new Page<> (current, pageSize), questionService.getQueryWrapper (questionQueryRequest));
        return ResultUtils.success (page);
    }

    /**
     * 编辑（用户）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpSession session){
        Long id = questionEditRequest.getId ();
        //校验参数
        if (questionEditRequest.getId ()==null || id<=0){
            throw new BusinessException (ErrorCode.PARAMS_ERROR);
        }
        //校验参数值
        Question question = new Question ();
        BeanUtils.copyProperties (questionEditRequest,question);
        //json数据处理及其校验
        setQuestionValue (questionEditRequest,question,false);

        Question question1 = questionService.getById (id);
        //判断题目是否存在
        if (question1==null){
            throw new BusinessException (ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser (session);

        //只有自己或者是管理员可以编辑
        if (!userService.isAdmin (session)&&!loginUser.getId ().equals (question.getUserId ())){
            throw new BusinessException (ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success (questionService.updateById (question));
    }

    /**
     * 提交题目
     */
    @PostMapping("/question_submit/do")
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
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpSession session){
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

