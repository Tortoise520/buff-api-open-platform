package com.yupi.springbootinit.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserUpdatePasswordRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户密码，不传则不更新
     */
    private String userPassword;
    
    private static final long serialVersionUID = 1L;
}