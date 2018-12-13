package com.lc.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.lc.beans.PageQuery;
import com.lc.beans.PageResult;
import com.lc.common.JsonData;
import com.lc.common.RequestHolder;
import com.lc.dao.SysRoleUserMapper;
import com.lc.dao.SysUserMapper;
import com.lc.exception.ParamException;
import com.lc.model.SysUser;
import com.lc.param.UserParam;
import com.lc.util.BeanValidator;
import com.lc.util.IpUtil;
import com.lc.util.MD5Util;
import com.lc.util.PasswordUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysUserService {

    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysLogService logService;

    public void save(UserParam param){
        BeanValidator.check(param);
        if(checkEmailExist(param.getMail(),param.getId()) || checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("用户已存在，请直接登陆");
        }
        String password = PasswordUtil.randomPassword();
        password = "1234560";
        String encryptPassword = MD5Util.encrypt(password);
        SysUser user = SysUser.builder().username(param.getUsername()).mail(param.getMail()).telephone(param.getTelephone())
                .password(encryptPassword).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator(RequestHolder.getCurrentUser().getUsername());
        user.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        user.setOperateTime(new Date());

        //发送email  todo

        userMapper.insertSelective(user);
        logService.saveUserLog(null,user);

    }

    public void update(UserParam param){
        BeanValidator.check(param);
        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已存在");
        }
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已存在");
        }

        SysUser oldUser = userMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(oldUser,"要更新的用户不存在");
        SysUser user = SysUser.builder().id(param.getId()).username(param.getUsername()).mail(param.getMail()).telephone(param.getTelephone())
                       .deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();

        user.setOperator(RequestHolder.getCurrentUser().getUsername());
        user.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        user.setOperateTime(new Date());

        userMapper.updateByPrimaryKeySelective(user);
        logService.saveUserLog(oldUser,user);

    }

    public boolean checkEmailExist(String mail,Integer userId){
        return userMapper.countByEmailAndUserId(mail,userId)>0;
    }

    public boolean checkTelephoneExist(String telephone,Integer userId){
        return userMapper.countByTelephoneAndUserId(telephone,userId)>0;
    }

    public SysUser findByKeyword(String keyword) {
        return userMapper.selectByKeyword(keyword);
    }

    public PageResult<SysUser> findUserPageByDeptId(int deptId, PageQuery pageQuery) {
        BeanValidator.check(pageQuery);
        int count = userMapper.countByDeptId(deptId);
        if(count > 0){
            List<SysUser> userList = userMapper.selectByDeptId(deptId,pageQuery);
            return PageResult.<SysUser>builder().total(count).data(userList).build();
        }

        return PageResult.<SysUser>builder().build();
    }

    public List<SysUser> findAllUsers() {
        return userMapper.selectAllUsers();
    }

    public List<SysUser> findUsersByRoleIdList(List<Integer> roleIdList) {
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        List<Integer> userIdList = roleUserMapper.selectUserIdsByRoleIdList(roleIdList);
        if(CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return userMapper.selectByIdList(userIdList);
    }
}
