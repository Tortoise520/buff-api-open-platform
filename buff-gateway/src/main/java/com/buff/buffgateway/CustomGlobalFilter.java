package com.buff.buffgateway;

import com.buff.buffclientsdk.utils.SignUtils;
import com.buff.common.model.entity.InterfaceInfo;
import com.buff.common.model.entity.User;
import com.buff.common.service.InnerInterfaceInfoService;
import com.buff.common.service.InnerUserInterfaceInfoService;
import com.buff.common.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    public static final List IP_WHITE_LIST = Arrays.asList(
            "127.0.0.1",
            "0:0:0:0:0:0:0:1"
    );
    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求参数并打印到日志
        ServerHttpRequest request = exchange.getRequest();
        String id = request.getId();
        String path = request.getPath().value();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        String method = request.getMethod().toString();
        log.info("id: {}, 请求地址: {}, 请求参数: {}, 请求源地址: {}, 请求方法: {}", id, path, queryParams, remoteAddress.getHostString(), method);
        // 2. 判断是否是白名单中的IP
        ServerHttpResponse response = exchange.getResponse();
        if (!IP_WHITE_LIST.contains(remoteAddress.getHostString())) {
            return handleNoAuth(response);
        }
        // 3.用户鉴权（判断ak,sk是否合法）
        HttpHeaders headers = request.getHeaders();
        String ak = headers.getFirst("accessKey");
        String nounce = headers.getFirst("nounce");//随机数
        String timestamps = headers.getFirst("timestamps");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        User invokeUser = innerUserService.getInvokeUser(ak);
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
        //验证secrectKey
        String secretKey = invokeUser.getSecretKey();
        String genSign = SignUtils.genSign(body, secretKey);
        if (!sign.equals(genSign)) {
            return handleNoAuth(response);
        }
    
        //  时间不能超过5分钟
        long FIVE_MINUTES = 5 * 60 * 1000L;
        long now = System.currentTimeMillis();
        if (timestamps != null && now - Long.parseLong(timestamps) > FIVE_MINUTES) {
            return handleNoAuth(response);
        }
        
        
        // 4. 请求的接口是否存在，请求的方法是否匹配
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }
        // todo 是否还有调用次数（数据库中要leftNum > 0）
    
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
    
    }
    
    @Override
    public int getOrder() {
        return -1;
    }
    
    Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }
    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatusCode statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }
    Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}