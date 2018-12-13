package com.lc.service;

import com.google.common.base.Preconditions;
import com.lc.beans.PageQuery;
import com.lc.beans.PageResult;
import com.lc.common.RequestHolder;
import com.lc.dao.SysAclMapper;
import com.lc.exception.ParamException;
import com.lc.model.SysAcl;
import com.lc.param.AclParam;
import com.lc.util.BeanValidator;
import com.lc.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysAclService {

    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysLogService logService;

    public void save(AclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限模块下存在相同名称的权限点");
        }
        SysAcl acl = SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId()).type(param.getType()).url(param.getUrl())
                     .status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark()).build();
        acl.setCode(generateCode());
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        acl.setOperateTime(new Date());
        aclMapper.insertSelective(acl);
        logService.saveAclLog(null,acl);

    }

    public void update(AclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限模块下存在相同名称的权限点");
        }
        SysAcl before = aclMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限点不存在");
        SysAcl after = SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId()).type(param.getType()).url(param.getUrl())
                .status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        aclMapper.updateByPrimaryKeySelective(after);
        logService.saveAclLog(before,after);
    }

    public boolean checkExist(int aclModuleId,String name,Integer id){
        return aclMapper.countByNameAndAclNoduleId(aclModuleId,name,id)>0;
    }

    private String generateCode(){

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date()) + "_" + (int)Math.random() * 100;
    }

    public PageResult<SysAcl> findAclPageByAclModuleId(int aclModuleId, PageQuery pageQuery) {
        BeanValidator.check(pageQuery);
        int count = aclMapper.countByAclModuleId(aclModuleId);
        if(count > 0){
            List<SysAcl> aclList = aclMapper.selectByAclModuleId(aclModuleId,pageQuery);
            return PageResult.<SysAcl>builder().total(count).data(aclList).build();
        }

        return PageResult.<SysAcl>builder().build();
    }
}
