package com.liu.yuojbackendjudgeservice.judge.controller.inner;

import com.liu.yuojbackendjudgeservice.judge.JudgeService;
import com.liu.yuojbackendmodel.entity.QuestionSubmit;
import com.liu.yuojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 刘渠好
 * @since 2024-10-31 16:37
 * 该服务仅是内部调用
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;

    @Override
    @PostMapping("/doJudge")
    public QuestionSubmit doJudge(long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }
}
