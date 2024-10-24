package com.liu.yuojmicroservice.judge.codesandbox.impl;

import com.liu.yuojmicroservice.judge.codesandbox.CodeSandBox;
import com.liu.yuojmicroservice.judge.model.ExecuteCodeRequest;
import com.liu.yuojmicroservice.judge.model.ExecuteCodeResponse;

/**
 * @Author 刘渠好
 * @Date 2024-07-22 20:16
 * 第三方代码沙箱
 */
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println ("启动第三方代码沙箱完毕");
        return null;
    }
}
