package com.liu.yuojbackendserviceclient.service;

import com.liu.yuojbackendmodel.entity.Question;
import com.liu.yuojbackendmodel.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



/**
* @author 刘渠好
* 针对表【question(题目表)】的数据库操作Service

*/
@FeignClient(name = "YuOj-backend-question-service",path = "/api/question/inner")
public interface QuestionFeignClient  {

    /**
     * 根据id获取question
     * @param questionId
     */
    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    /**
     * 更新question
     * @param question
     */
    @PostMapping("/update/question")
    boolean updateQuestionById(@RequestBody Question question);

    /**
     * 根据id获取questionSubmit
     * @param questionSubmitId
     * @return
     */
    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);

    /**
     * 更新question
     * @param questionSubmit
     */
    @PostMapping("/update/questionSubmit")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);

}
