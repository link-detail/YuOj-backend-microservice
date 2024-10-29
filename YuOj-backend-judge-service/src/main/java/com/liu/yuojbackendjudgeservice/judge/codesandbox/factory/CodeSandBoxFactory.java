package com.liu.yuojbackendjudgeservice.judge.codesandbox.factory;

import com.liu.yuojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.liu.yuojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandBox;
import com.liu.yuojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandBox;
import com.liu.yuojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandBox;

/**
 * @Author 刘渠好
 * @Date 2024-07-22 20:22
 * 代码沙箱工厂
 */

public class CodeSandBoxFactory {
    public static CodeSandBox newInstance(String type){
        switch (type){
            case "example":
                return new ExampleCodeSandBox ();
            case "thirdParty":
                return new ThirdPartyCodeSandBox ();
            case "remote":
            default:
                return new RemoteCodeSandBox ();
        }
    }
}
