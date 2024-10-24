package com.liu.yuojmicroservice.person.test;

import com.liu.yuojmicroservice.judge.codesandbox.CodeSandBox;
import com.liu.yuojmicroservice.judge.model.ExecuteCodeRequest;
import com.liu.yuojmicroservice.judge.model.ExecuteCodeResponse;

/**
 * @author 刘渠好
 * @since 2024-10-11 22:06
 */
public class MyCodeSandBoxProxy implements CodeSandBox {

    private final CodeSandBox codeSandBox;

    public MyCodeSandBoxProxy(CodeSandBox codeSandBox) {
        this.codeSandBox = codeSandBox;
    }
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println ("执行代码请求信息"+executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode (executeCodeRequest);
        System.out.println ("执行代码响应信息"+executeCodeResponse);
        return executeCodeResponse;
    }
}
