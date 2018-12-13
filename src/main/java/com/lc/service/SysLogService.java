package com.lc.service;

import com.google.common.base.Preconditions;
import com.lc.beans.LogType;
import com.lc.beans.PageQuery;
import com.lc.beans.PageResult;
import com.lc.common.RequestHolder;
import com.lc.dao.*;
import com.lc.dto.SearchLogDto;
import com.lc.exception.ParamException;
import com.lc.model.*;
import com.lc.param.SearchLogParam;
import com.lc.util.BeanValidator;
import com.lc.util.IpUtil;
import com.lc.util.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysLogService {

    @Resource
    private SysLogMapper logMapper;
    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysAclModuleMapper aclModuleMapper;
    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysRoleAclService roleAclService;
    @Resource
    private SysRoleUserService roleUserService;

    public void saveDeptLog(SysDept before,SysDept after){
        SysLogWithBLOBs log = save(before,after,LogType.TYPE_DEPT);
        log.setTargetId(after.getId()==null?before.getId():after.getId());
        logMapper.insertSelective(log);
    }

    public void saveUserLog(SysUser before, SysUser after){
        SysLogWithBLOBs log =save(before,after,LogType.TYPE_USER);
        log.setTargetId(after.getId()==null?before.getId():after.getId());
        logMapper.insertSelective(log);
    }

    public void saveAclModuleLog(SysAclModule before, SysAclModule after){
        SysLogWithBLOBs log =save(before,after,LogType.TYPE_ACL_MODULE);
        log.setTargetId(after.getId()==null?before.getId():after.getId());
        logMapper.insertSelective(log);
    }

    public void saveAclLog(SysAcl before,SysAcl after){
        SysLogWithBLOBs log =save(before,after,LogType.TYPE_ACL);
        log.setTargetId(after.getId()==null?before.getId():after.getId());
        logMapper.insertSelective(log);
    }

    public void saveRoleLog(SysRole before, SysRole after){
        SysLogWithBLOBs log =save(before,after,LogType.TYPE_ROLE);
        log.setTargetId(after.getId()==null?before.getId():after.getId());
        logMapper.insertSelective(log);
    }



    private <T> SysLogWithBLOBs save(T before,T after,Integer type){
        SysLogWithBLOBs log = new SysLogWithBLOBs();
        log.setType(type);
        log.setOldValue(before==null?"": JsonMapper.obj2String(before));
        log.setNewValue(after==null?"":JsonMapper.obj2String(after));
        log.setOperator(RequestHolder.getCurrentUser().getUsername());
        log.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        log.setOperateTime(new Date());
        log.setStatus(1);
        return log;
    }

    public PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam param, PageQuery query) {
        BeanValidator.check(query);
        SearchLogDto dto = new SearchLogDto();
        dto.setType(param.getType());
        if(StringUtils.isNotBlank(param.getBeforeSeg())) {
            dto.setBeforeSeg("%"+param.getBeforeSeg()+"%");
        }
        if(StringUtils.isNotBlank(param.getAfterSeg())) {
            dto.setAfterSeg("%"+param.getAfterSeg()+"%");
        }
        if(StringUtils.isNotBlank(param.getOperator())) {
            dto.setOperator("%"+param.getOperator()+"%");
        }
        if(StringUtils.isNotBlank(param.getFromTime())) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                dto.setFromTime(format.parse(param.getFromTime()));
            } catch (ParseException e) {
                throw new ParamException("传入的日期格式有问题,正确格式为：yyyy-MM-dd HH:mm:ss");
            }
        }
        if(StringUtils.isNotBlank(param.getToTime())) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                dto.setToTime(format.parse(param.getToTime()));
            } catch (ParseException e) {
                throw new ParamException("传入的日期格式有问题,正确格式为：yyyy-MM-dd HH:mm:ss");
            }
        }
        int count = logMapper.countBySearchDto(dto);
        if(count>0){
            List<SysLogWithBLOBs> logList = logMapper.selectBySearchDto(dto,query);
            return PageResult.<SysLogWithBLOBs>builder().total(count).data(logList).build();
        }
        return PageResult.<SysLogWithBLOBs>builder().build();
    }

    public void recover(int logId) {
        SysLogWithBLOBs log = logMapper.selectByPrimaryKey(logId);
        Preconditions.checkNotNull(log,"待还原的数据不存在");
        switch (log.getType()){
            case LogType.TYPE_DEPT:
                SysDept beforeDept = deptMapper.selectByPrimaryKey(log.getTargetId());
                if(StringUtils.isBlank(log.getNewValue()) || StringUtils.isBlank(log.getOldValue())){
                    throw new ParamException("新增和删除数据不做还原操作");
                }
                Preconditions.checkNotNull(beforeDept,"待更新的部门数据不存在");
                SysDept afterDept = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysDept>() {
                });
                afterDept.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterDept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                afterDept.setOperateTime(new Date());
                deptMapper.updateByPrimaryKeySelective(afterDept);
                saveDeptLog(beforeDept,afterDept);
                break;
            case LogType.TYPE_USER:
                SysUser beforeUser = userMapper.selectByPrimaryKey(log.getTargetId());
                if(StringUtils.isBlank(log.getNewValue()) || StringUtils.isBlank(log.getOldValue())){
                    throw new ParamException("新增和删除数据不做还原操作");
                }
                Preconditions.checkNotNull(beforeUser,"待更新的用户数据不存在");
                SysUser afterUser = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysUser>() {
                });
                afterUser.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                afterUser.setOperateTime(new Date());
                userMapper.updateByPrimaryKeySelective(afterUser);
                saveUserLog(beforeUser,afterUser);
                break;
            case LogType.TYPE_ACL_MODULE:
                SysAclModule beforeAclModule = aclModuleMapper.selectByPrimaryKey(log.getTargetId());
                if(StringUtils.isBlank(log.getNewValue()) || StringUtils.isBlank(log.getOldValue())){
                    throw new ParamException("新增和删除数据不做还原操作");
                }
                Preconditions.checkNotNull(beforeAclModule,"待更新的权限模块数据不存在");
                SysAclModule afterAclModule = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysAclModule>() {
                });
                afterAclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterAclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                afterAclModule.setOperateTime(new Date());
                aclModuleMapper.updateByPrimaryKeySelective(afterAclModule);
                saveAclModuleLog(beforeAclModule,afterAclModule);
                break;
            case LogType.TYPE_ACL:
                SysAcl beforeAcl = aclMapper.selectByPrimaryKey(log.getTargetId());
                if(StringUtils.isBlank(log.getNewValue()) || StringUtils.isBlank(log.getOldValue())){
                    throw new ParamException("新增和删除数据不做还原操作");
                }
                Preconditions.checkNotNull(beforeAcl,"待更新的权限点数据不存在");
                SysAcl afterAcl = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysAcl>() {
                });
                afterAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                afterAcl.setOperateTime(new Date());
                aclMapper.updateByPrimaryKeySelective(afterAcl);
                saveAclLog(beforeAcl,afterAcl);
                break;
            case LogType.TYPE_ROLE:
                SysRole beforeRole = roleMapper.selectByPrimaryKey(log.getTargetId());
                if(StringUtils.isBlank(log.getNewValue()) || StringUtils.isBlank(log.getOldValue())){
                    throw new ParamException("新增和删除数据不做还原操作");
                }
                Preconditions.checkNotNull(beforeRole,"待更新的角色数据不存在");
                SysRole afterRole = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysRole>() {
                });
                afterRole.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterRole.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                afterRole.setOperateTime(new Date());
                roleMapper.updateByPrimaryKeySelective(afterRole);
                saveRoleLog(beforeRole,afterRole);
                break;
            case LogType.TYPE_ROLE_ACL:
                SysRole aclRole = roleMapper.selectByPrimaryKey(log.getTargetId());
                Preconditions.checkNotNull(aclRole,"角色数据不存在");
                roleAclService.changeRoleAcls(log.getTargetId(),JsonMapper.string2Obj(log.getOldValue(), new TypeReference<List<Integer>>() {
                }));
                break;
            case LogType.TYPE_ROLE_USER:
                SysRole userRole = roleMapper.selectByPrimaryKey(log.getTargetId());
                Preconditions.checkNotNull(userRole,"角色数据不存在");
                roleUserService.changeRoleUsers(log.getTargetId(),JsonMapper.string2Obj(log.getOldValue(), new TypeReference<List<Integer>>() {
                }));

                break;
            default:
        }

    }
}
