package com.buff.buffinterface.controller;

import com.buff.buffclientsdk.client.BuffApiClient;
import com.buff.buffclientsdk.model.User;
import com.buff.buffclientsdk.utils.SignUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 名称api
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/")
    public String getNameByGet(String name) {
        return "GET name:" + name;
    }
    @PostMapping("/")
    public String getNameByPost(@RequestParam String name) {//url传参
        return "POST name:" + name;
    }
    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {//Restful
        String accessKey = request.getHeader("accessKey");
//        String secretKey = request.getHeader("secretKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");
        
        // todo 验证accessKey,实际情况得查库
//        if (!accessKey.equals("buff")) {
//            throw new RuntimeException("无权限");
//        }
//        if (Long.parseLong(nonce) > 10000) {
//            throw new RuntimeException("无权限");
//        }
        // todo timestamp时间不能超过5分钟
        // todo 验证签名,实际情况得查库
//        String serverSign = SignUtils.genSign(body, "abcdefg");
//        if (!serverSign.equals(sign)) {
//            throw new RuntimeException("无权限");
//        }
        return "POST username:" + user.getUsername();
    }
}
