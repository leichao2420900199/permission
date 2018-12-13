package com.lc.service;

import com.google.common.collect.Lists;
import com.lc.beans.CacheKeyConstants;
import com.lc.common.RequestHolder;
import com.lc.dao.SysAclMapper;
import com.lc.dao.SysRoleAclMapper;
import com.lc.dao.SysRoleUserMapper;
import com.lc.dao.SysUserMapper;
import com.lc.model.SysAcl;
import com.lc.util.JsonMapper;
import com.lc.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysCoreService {

    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysRoleAclMapper roleAclMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysCacheService cacheService;

    public List<SysAcl> findCurrentUserAclList(){
        int userId = RequestHolder.getCurrentUser().getId();
        return findUserAclList(userId);
    }

    public List<SysAcl> findCurrentUserRoleAclList(int roleId){
        List<Integer> aclIdList = roleAclMapper.selectAclIdByRoleId(Lists.<Integer>newArrayList(roleId));
        if(CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        return aclMapper.selectByAclIds(aclIdList);
    }

    public List<SysAcl> findUserAclList(int userId){
        if(isSuperAdmin(userId)){
            return aclMapper.selectAllAcl();
        }
        List<Integer> roleIdList = roleUserMapper.selectRoleIdByUserId(userId);
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }

        List<Integer> aclIdList = roleAclMapper.selectAclIdByRoleId(roleIdList);
        if(CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }

        return aclMapper.selectByAclIds(aclIdList);

    }

    public boolean isSuperAdmin(int userId){
        return "superAdmin".equals(userMapper.selectByPrimaryKey(userId).getUsername());
    }

    public boolean hasUrlAcl(String url){
        if(isSuperAdmin(RequestHolder.getCurrentUser().getId())){
            return true;
        }
        List<SysAcl> aclList = aclMapper.selectByUrl(url);
        if(CollectionUtils.isEmpty(aclList)){
            return true;
        }

        List<SysAcl> userAclList = findCurrentUserAclListFormCache();
        Set<Integer> userAclIdSet = userAclList.stream().map(acl -> acl.getId()).collect(Collectors.toSet());
        boolean hasValidAcl = false;
        //规则：当前用户只要满足一个就可以
        for (SysAcl acl:aclList){
            //判断一个用户是否具有某个权限点的访问权限
            if(acl==null || acl.getStatus()!=1){ //权限点无效
                continue;
            }
            hasValidAcl = true;
            if(userAclIdSet.contains(acl.getId())){
                return true;
            }
        }

        if(!hasValidAcl){
            return true;
        }
        return false;
    }

    public List<SysAcl> findCurrentUserAclListFormCache(){
        int userId = RequestHolder.getCurrentUser().getId();
        String cacheValue = cacheService.getFromCache(CacheKeyConstants.USER_ACLS,String.valueOf(userId));
        if(StringUtils.isBlank(cacheValue)){
            List<SysAcl> aclList = findCurrentUserAclList();
            if(CollectionUtils.isNotEmpty(aclList)){
                cacheService.saveCache(JsonMapper.obj2String(aclList),600,CacheKeyConstants.USER_ACLS,String.valueOf(userId));
            }
            return aclList;
        }
        return JsonMapper.string2Obj(cacheValue, new TypeReference<List<SysAcl>>() {
        });
    }
}
