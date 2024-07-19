package com.yupi.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.buff.common.model.entity.InterfaceInfo;
import com.buff.common.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class InterfaceInfoVO extends InterfaceInfo {
    private Integer totalNum;
}