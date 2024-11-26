package com.liu.yuojbackenduserservice;

import com.liu.yuojbackendmodel.entity.QuestionSubmit;
import com.liu.yuojbackendserviceclient.service.JudgeFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class YuOjBackendUserServiceApplicationTests {

    @Resource
    private JudgeFeignClient judgeFeignClient;



    @Test
    void judgeTest(){
        QuestionSubmit questionSubmit = judgeFeignClient.doJudge (1850193819357052930L);
        System.out.println (questionSubmit);

    }

}
