package com.liu.yuojbackendjudgeservice.judge.codesandbox.impl;

import com.liu.yuojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.liu.yuojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.liu.yuojbackendmodel.codesandbox.ExecuteCodeResponse;

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
