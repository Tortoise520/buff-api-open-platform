package com.buff.common.service;


/**
*/
public interface InnerUserInterfaceInfoService {
    /**
     * 接口调用次数 + 1 invokeCount（accessKey、secretKey（标识用户），请求接口路径）
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
    
}
