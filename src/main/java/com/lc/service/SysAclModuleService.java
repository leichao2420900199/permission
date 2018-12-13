package com.lc.service;

import com.google.common.base.Preconditions;
import com.lc.common.RequestHolder;
import com.lc.dao.SysAclMapper;
import com.lc.dao.SysAclModuleMapper;
import com.lc.exception.ParamException;
import com.lc.model.SysAclModule;
import com.lc.param.AclModuleParam;
import com.lc.util.BeanValidator;
import com.lc.util.IpUtil;
import com.lc.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleService {

    @Resource
    private SysAclModuleMapper aclModuleMapper;
    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysLogService logService;

    public void save(AclModuleParam param){
        SysAclModule aclModule = checkParam(param);
        aclModule.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        aclModuleMapper.insertSelective(aclModule);
        logService.saveAclModuleLog(null,aclModule);
    }

    public void update(AclModuleParam param){
        SysAclModule aclModule = checkParam(param);
        SysAclModule before = aclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限模块不存在");
        aclModule.setId(param.getId());
        aclModule.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        updateWithChild(before,aclModule);
        logService.saveAclModuleLog(before,aclModule);
    }

    @Transactional
    public void updateWithChild(SysAclModule oldAclModule, SysAclModule newAclModule){
        String newLevelPrefix = newAclModule.getLevel();
        String oldLevelPrefix = oldAclModule.getLevel();
        if (!newAclModule.getLevel().equals(oldAclModule.getLevel())) {
            List<SysAclModule> aclModuleList = aclModuleMapper.selectChildAclModuleByLevel(oldAclModule.getLevel(),oldAclModule.getId());
            if (CollectionUtils.isNotEmpty(aclModuleList)) {
                for (SysAclModule aclModule : aclModuleList) {
                    String level = aclModule.getLevel();
                    if (level.indexOf(oldLevelPrefix) == 0) {
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                    aclModuleMapper.batchUpdateLevel(aclModule.getLevel(),aclModule.getId());
                }
            }
        }

        aclModuleMapper.updateByPrimaryKeySelective(newAclModule);
    }

    private boolean checkExist(Integer parentId,String name,Integer id){
        return aclModuleMapper.countByNameAndParentId(parentId,name,id)>0;
    }

    private String getLevel(Integer aclModuleId){
        SysAclModule aclModule = aclModuleMapper.selectByPrimaryKey(aclModuleId);
        if(aclModule==null) return null;
        return aclModule.getLevel();
    }

    private SysAclModule checkParam(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下权限模块名称重复");
        }
        SysAclModule aclModule = SysAclModule.builder().name(param.getName()).parentId(param.getParentId()).remark(param.getRemark())
                                             .seq(param.getSeq()).status(param.getStatus()).build();
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclModule.setOperateTime(new Date());
        return aclModule;
    }

    public void delete(int id) {
        SysAclModule aclModule = aclModuleMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(aclModule,"待删除的权限模块不存在");
        if(aclModuleMapper.countByParentId(id)>0){
            throw new ParamException("当前权限模块下存在子模块，请先删除子模块");
        }
        if(aclMapper.countByAclModuleId(id)>0){
            throw new ParamException("当前权限模块下存在权限点，无法删除");
        }
        aclModuleMapper.deleteByPrimaryKey(id);
    }
}
