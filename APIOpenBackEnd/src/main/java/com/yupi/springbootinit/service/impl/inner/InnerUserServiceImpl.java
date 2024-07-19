package com.yupi.springbootinit.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.buff.common.service.InnerUserService;
import com.buff.common.model.entity.User;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 *
 */
@DubboService
@Slf4j
public class InnerUserServiceImpl implements InnerUserService {
    
    @Resource
    private UserMapper userMapper;
    
    @Override
    public User getInvokeUser(String accessKey) {
        // 参数校验
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        // 查询匹配的User对象
        User user = userMapper.selectOne(queryWrapper);
        return user; // 这将返回匹配的User对象，如果没有匹配项，则返回null
    }
}
