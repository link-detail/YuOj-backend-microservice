package com.liu.yuojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.liu.yuojbackendcommon.common.ErrorCode;
import com.liu.yuojbackendcommon.exception.BusinessException;
import com.liu.yuojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.liu.yuojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.liu.yuojbackendmodel.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author 刘渠好
 * @Date 2024-07-22 20:15
 * 远程代码沙箱
 */
@Slf4j
public class RemoteCodeSandBox implements CodeSandBox {
        //防止被用户刷接口，根据判断请求头信息来决定是否返回执行结果（服务器和服务器之间）

        public static final String AUTH_REQUEST_HEADER = "auth";

        public static final String AUTH_REQUEST_SECRET = "secretKey";

        @Override
        public ExecuteCodeResponse executeCode (ExecuteCodeRequest executeCodeRequest){
            log.info ("调用远程代码沙箱");
            //调用远程API接口服务
            String url = "http://localhost:8090/executeCode";
            String json = JSONUtil.toJsonStr (executeCodeRequest);
            //代码沙箱处加密了
            String body = HttpUtil.createPost (url)
                    .setReadTimeout (60000)
                    .body (json).execute ()
                    .body ();
            if (StrUtil.isBlank (body)) {
                throw new BusinessException (ErrorCode.API_REUEST_ERROR, "execute remoteCodeSandbox error message=" + body);
            }
            /**
             * 注意：当字符串中的属性名与对应实体类中的属性名不一致的时候，是无法将字符串转为对应的实体类的
             */
            return JSONUtil.toBean (body, ExecuteCodeResponse.class);
        }
    }

