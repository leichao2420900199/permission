package com.lc.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lc.beans.LogType;
import com.lc.common.RequestHolder;
import com.lc.dao.SysLogMapper;
import com.lc.dao.SysRoleUserMapper;
import com.lc.dao.SysUserMapper;
import com.lc.model.SysLogWithBLOBs;
import com.lc.model.SysRoleAcl;
import com.lc.model.SysRoleUser;
import com.lc.model.SysUser;
import com.lc.util.IpUtil;
import com.lc.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleUserService {

    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysLogMapper logMapper;

    public List<SysUser> findUsersByRoleId(Integer roleId){
        List<Integer> userIdsList  = roleUserMapper.selectUserIdsByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdsList)){
            return Lists.newArrayList();
        }
        return userMapper.selectByIdList(userIdsList);
    }


    private void saveRoleUserLog(int roleId,List<Integer> before, List<Integer> after){
        SysLogWithBLOBs log = new SysLogWithBLOBs();
        log.setType(LogType.TYPE_ROLE_USER);
        log.setOldValue(before==null?"": JsonMapper.obj2String(before));
        log.setNewValue(after==null?"":JsonMapper.obj2String(after));
        log.setOperator(RequestHolder.getCurrentUser().getUsername());
        log.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        log.setOperateTime(new Date());
        log.setStatus(1);
        log.setTargetId(roleId);
        logMapper.insertSelective(log);
    }

    public void changeRoleUsers(int roleId, List<Integer> userIdList) {
        List<Integer> originUserIdList = roleUserMapper.selectUserIdsByRoleId(roleId);
        if(originUserIdList.size() == userIdList.size()){
            Set<Integer> originUserIdSet = Sets.newHashSet(originUserIdList);
            Set<Integer> userIdSet = Sets.newHashSet(userIdList);
            originUserIdSet.remove(userIdSet);
            if(org.springframework.util.CollectionUtils.isEmpty(originUserIdSet)){
                return;
            }
        }
        updateRoleUsers(roleId,userIdList);
        saveRoleUserLog(roleId,originUserIdList,userIdList);
    }

    @Transactional
    public void updateRoleUsers(int roleId,List<Integer> userIdList){
        roleUserMapper.deleteByRoleId(roleId);
        if(org.springframework.util.CollectionUtils.isEmpty(userIdList)){
            return;
        }

        List<SysRoleUser> roleUserList = Lists.newArrayList();
        for(Integer userId:userIdList){
            SysRoleUser sysRoleUser = SysRoleUser.builder().roleId(roleId).userId(userId).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            roleUserList.add(sysRoleUser);

        }

        roleUserMapper.batchInsert(roleUserList);
    }
}
