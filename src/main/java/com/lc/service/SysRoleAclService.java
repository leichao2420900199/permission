package com.lc.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lc.beans.LogType;
import com.lc.common.RequestHolder;
import com.lc.dao.SysLogMapper;
import com.lc.dao.SysRoleAclMapper;
import com.lc.model.SysLogWithBLOBs;
import com.lc.model.SysRoleAcl;
import com.lc.util.IpUtil;
import com.lc.util.JsonMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleAclService {

    @Resource
    private SysRoleAclMapper roleAclMapper;
    @Resource
    private SysLogMapper logMapper;

    public void changeRoleAcls(Integer roleId, List<Integer> aclIdList){
        List<Integer> originAclIdList = roleAclMapper.selectAclIdByRoleId(Lists.newArrayList(roleId));
        if(originAclIdList.size() == aclIdList.size()){
            Set<Integer> originAclIdSet = Sets.newHashSet(originAclIdList);
            Set<Integer> aclIdSet = Sets.newHashSet(aclIdList);
            originAclIdSet.remove(aclIdSet);
            if(CollectionUtils.isEmpty(originAclIdSet)){
                return;
            }
        }
        updateRoleAcls(roleId,aclIdList);
        saveRoleAclLog(roleId,originAclIdList,aclIdList);
    }
    private void saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after){
        SysLogWithBLOBs log =new SysLogWithBLOBs();
        log.setType(LogType.TYPE_ROLE_ACL);
        log.setTargetId(roleId);
        log.setOldValue(before==null?"": JsonMapper.obj2String(before));
        log.setNewValue(after==null?"":JsonMapper.obj2String(after));
        log.setOperator(RequestHolder.getCurrentUser().getUsername());
        log.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        log.setOperateTime(new Date());
        log.setStatus(1);
        logMapper.insertSelective(log);
    }
    @Transactional
    public void updateRoleAcls(int roleId,List<Integer> aclIdList){
        roleAclMapper.deleteByRoleId(roleId);
        if(CollectionUtils.isEmpty(aclIdList)){
            return;
        }

        List<SysRoleAcl> roleAclList = Lists.newArrayList();
        for(Integer aclId:aclIdList){
            SysRoleAcl sysRoleAcl = SysRoleAcl.builder().roleId(roleId).aclId(aclId).operator(RequestHolder.getCurrentUser().getUsername())
                                     .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            roleAclList.add(sysRoleAcl);

        }

        roleAclMapper.batchInsert(roleAclList);

    }
}
