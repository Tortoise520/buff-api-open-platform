package com.buff.common.service;

import com.buff.common.model.entity.InterfaceInfo;

/**
 * 内部公共类
*/
public interface InnerInterfaceInfoService {
    
    /**
     * 实现接口中的 getInterfaceInfo 方法，用于根据URL和请求方法获取内部接口信息。
     *
     * @param url    请求URL
     * @param method 请求方法
     * @return 内部接口信息，如果找不到匹配的接口则返回 null
     */
    InterfaceInfo getInterfaceInfo(String url, String method);
}
