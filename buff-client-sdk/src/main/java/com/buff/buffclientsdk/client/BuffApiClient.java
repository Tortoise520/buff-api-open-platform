package com.buff.buffclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.buff.buffclientsdk.model.User;
import com.buff.buffclientsdk.utils.SignUtils;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: buff
 */
@Data
public class BuffApiClient {
    
    public static final String GATEWAY_HOST = "http://localhost:8091";
    
    private String accessKey;
    private String secretKey;
    
    public BuffApiClient() {
    }
    
    public BuffApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
    
    public String getNameByGet(String name) {
    
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
    
        return HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
    }
    public String getNameByPost( String name) {//url传参
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
    
        return HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
    }
    public Map<String, String> getHeaderMap(String body) {
        Map<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
        //secretKey一定不能直接发送
//        map.put("secretKey", secretKey);
        map.put("nonce", RandomUtil.randomNumbers(4));
        map.put("body", body);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
        map.put("sign", SignUtils.genSign(body, secretKey));
        return map;
    }
    
   
    
    public String getUserNameByPost(User user) {//Restful
        String json = JSONUtil.toJsonStr(user);
        return HttpRequest
                .post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute().body();
    }
}
