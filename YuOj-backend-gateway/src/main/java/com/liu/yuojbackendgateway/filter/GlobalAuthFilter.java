package com.liu.yuojbackendgateway.filter;

import cn.hutool.core.text.AntPathMatcher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author 刘渠好
 * @since 2024-11-01 22:14
 */
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher=new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest ();
        String path = request.getURI ().getPath ();
        //判断请求路径中是否包含inner，只允许内部调用
        if (antPathMatcher.match ("/**/inner/**",path)){
            ServerHttpResponse response = exchange.getResponse ();
            //设置响应值
            response.setStatusCode (HttpStatus.FORBIDDEN);
            DataBufferFactory dataBufferFactory = response.bufferFactory ();
            DataBuffer buffer = dataBufferFactory.wrap ("无权限".getBytes (StandardCharsets.UTF_8));
            return response.writeWith (Mono.just (buffer));
        }
        //todo 统一权限校验 通过JWT获取用户信息（不通过httpRequest来获取）
        return chain.filter(exchange);
    }

    /**
     * 优先级提到最高，项目中可能会有多个拦截器（多个拦截器之间是有优先顺序的），这个拦截器最重要，就给他放到最前面，防止在调用其他拦截器浪费性能
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
