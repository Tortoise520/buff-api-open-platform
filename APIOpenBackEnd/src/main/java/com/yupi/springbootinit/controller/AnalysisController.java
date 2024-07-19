package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.buff.common.model.entity.InterfaceInfo;
import com.buff.common.model.entity.UserInterfaceInfo;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.common.*;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.UserInterfaceInfoMapper;
import com.yupi.springbootinit.model.vo.InterfaceInfoVO;
import com.yupi.springbootinit.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {
    
    @Resource
    private InterfaceInfoService interfaceInfoService;
    
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;
    
    
    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<InterfaceInfoVO>> listTopInterfaceInfoInvoke() {
        List<InterfaceInfoVO> interfaceInfoVOList = null;
        List<UserInterfaceInfo> userInterfaceInfos = userInterfaceInfoMapper.listTopInvokedUserInterfaceInfo(3);
        if (userInterfaceInfos == null || userInterfaceInfos.size() == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //将接口信息按照接口ID分组，便于关联查询
        Map<Long, List<UserInterfaceInfo>> listMap = userInterfaceInfos.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        //查询接口信息
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", listMap.keySet());
        List<InterfaceInfo> interfaceInfos = interfaceInfoService.list(queryWrapper);
        if (interfaceInfos == null || interfaceInfos.size() == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //构建接口信息VO列表，使用流式处理将接口信息映射为接口信息VO对象，并加入列表中
        interfaceInfoVOList = interfaceInfos.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            int totalNum = listMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVO.setTotalNum(totalNum);
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        
        return ResultUtils.success(interfaceInfoVOList);
    }
    
    
}
