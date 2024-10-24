package com.liu.yuojmicroservice.judge.codesandbox.impl;

import com.liu.yuojmicroservice.judge.codesandbox.CodeSandBox;
import com.liu.yuojmicroservice.judge.model.ExecuteCodeRequest;
import com.liu.yuojmicroservice.judge.model.ExecuteCodeResponse;

/**
 * @Author 刘渠好
 * @Date 2024-07-22 20:15
 * 远程代码沙箱
 */
public class RemoteCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println ("启动远程代码沙箱成功！");
        return null;
    }
}
