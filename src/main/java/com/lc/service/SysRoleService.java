package com.lc.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.lc.common.RequestHolder;
import com.lc.dao.SysRoleAclMapper;
import com.lc.dao.SysRoleMapper;
import com.lc.dao.SysRoleUserMapper;
import com.lc.exception.ParamException;
import com.lc.model.SysRole;
import com.lc.param.RoleParam;
import com.lc.util.BeanValidator;
import com.lc.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysRoleAclMapper roleAclMapper;
    @Resource
    private SysLogService logService;

    public void save(RoleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已存在");
        }
        SysRole role = SysRole.builder().name(param.getName()).type(param.getType()).status(param.getStatus()).remark(param.getRemark()).build();
        role.setOperator(RequestHolder.getCurrentUser().getUsername());
        role.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        role.setOperateTime(new Date());
        roleMapper.insertSelective(role);
        logService.saveRoleLog(null,role);
    }

    public void update(RoleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已存在");
        }
        SysRole before = roleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的角色不存在");
        SysRole role = SysRole.builder().id(param.getId()).name(param.getName()).type(param.getType()).status(param.getStatus()).remark(param.getRemark()).build();
        role.setOperator(RequestHolder.getCurrentUser().getUsername());
        role.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        role.setOperateTime(new Date());
        roleMapper.updateByPrimaryKeySelective(role);
        logService.saveRoleLog(before,role);
    }

    private boolean checkExist(String name,Integer id){
        return roleMapper.countByNameAndId(name,id)>0;
    }

    public List<SysRole> findAll(){
        return roleMapper.selectRoles();
    }

    public List<SysRole> findRoleListByUserId(int userId){
        List<Integer> roleIdList = roleUserMapper.selectRoleIdByUserId(userId);
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return roleMapper.selectByRoleIdList(roleIdList);
    }

    public List<SysRole> findRoleListByAcId(int aclId){
        List<Integer> roleIdList = roleAclMapper.selectRoleIdByAclId(aclId);
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return roleMapper.selectByRoleIdList(roleIdList);
    }
}
