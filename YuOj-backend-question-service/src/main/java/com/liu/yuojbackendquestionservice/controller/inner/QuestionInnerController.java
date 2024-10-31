package com.liu.yuojbackendquestionservice.controller.inner;

import com.liu.yuojbackendmodel.entity.Question;
import com.liu.yuojbackendmodel.entity.QuestionSubmit;
import com.liu.yuojbackendquestionservice.service.QuestionService;
import com.liu.yuojbackendquestionservice.service.QuestionSubmitService;
import com.liu.yuojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 刘渠好
 * @since 2024-10-31 16:34
 *  * 该服务仅是内部调用
 */
@RestController
@RequestMapping("/inner")

public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;


    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(long questionId) {
        return questionService.getById (questionId);
    }

    @Override
    @PostMapping("/update/question")
    public boolean updateQuestionById(Question question) {
        return questionService.updateById(question);
    }

    @Override
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(long questionSubmitId) {
        return questionSubmitService.getById (questionSubmitId);
    }

    @Override
    @PostMapping("/update/questionSubmit")
    public boolean updateQuestionSubmitById(QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }
}
