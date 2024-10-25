package com.liu.yuojmicroservice.person.judge;

import com.liu.yuojmicroservice.judge.JudgeService;
import com.liu.yuojmicroservice.judge.codesandbox.CodeSandBox;
import com.liu.yuojmicroservice.judge.codesandbox.factory.CodeSandBoxFactory;
import com.liu.yuojmicroservice.judge.codesandbox.proxy.CodeSandboxProxy;
import com.liu.yuojmicroservice.judge.model.ExecuteCodeRequest;
import com.liu.yuojmicroservice.judge.model.ExecuteCodeResponse;
import com.liu.yuojmicroservice.model.entity.QuestionSubmit;
import com.liu.yuojmicroservice.model.enums.QuestionSubmitLanguageEnum;
import com.liu.yuojmicroservice.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @Author 刘渠好
 * @Date 2024-07-22 21:23
 */
@SpringBootTest
class CodeSandTest {

    @Resource
    private QuestionService questionService;

    @Resource
    private JudgeService judgeService;


    //获取配置文件中的内容
    @Value ("${codesandbox.type}")
    private String type;

    @Test
    void test01(){
         CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance (type);
        List<String> list = Arrays.asList ("1 2", "3 4");
         ExecuteCodeRequest build = ExecuteCodeRequest.builder ().code ("System.out.println (12);")
                .language (QuestionSubmitLanguageEnum.GO)
                .inputList (list).build ();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode (build);
        System.out.println (executeCodeResponse);

    }

    @Test
    void test02(){
        List<String> list = Arrays.asList ("1 2", "3 4");
        String code="import java.util.Scanner;\n" +
                "\n" +
                "public class Main {\n" +
                "    public static void main(String[] args){\n" +
                "        Scanner sc = new Scanner(System.in);\n" +
                "        int a = sc.nextInt();\n" +
                "        int b = sc.nextInt();\n" +
                "        System.out.println(a + b);\n" +
                "    }\n" +
                "}";
        ExecuteCodeRequest build = ExecuteCodeRequest.builder ().code (code)
                .language (QuestionSubmitLanguageEnum.JAVA)
                .inputList (list).
                build ();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode (build);
        System.out.println (executeCodeResponse);


    }

    @Test
    void test03(){
        QuestionSubmit questionSubmit = judgeService.doJudge (1843667680341925890L);
        System.out.println (questionSubmit);
    }
}
