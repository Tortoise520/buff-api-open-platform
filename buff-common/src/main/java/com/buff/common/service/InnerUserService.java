package com.buff.common.service;

import com.buff.common.model.entity.User;

/**
 * 用户服务
 *
 */
public interface InnerUserService {
    
    /**
     * 数据库中查是否已分配给用户秘钥（根据 accessKey 拿到用户信息，返回用户信息，为空表示不存在）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

}
