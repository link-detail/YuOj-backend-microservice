package com.liu.yuojbackendserviceclient.service;

import com.liu.yuojbackendmodel.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author 刘渠好
 * @Date 2024-07-22 22:11
 * 判题服务
 */
@FeignClient(name = "YuOj-backend-judge-service",path = "/api/judge/inner")
public interface JudgeFeignClient {

    /**
     * 判题
     */
    @PostMapping("/doJudge")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);
}
