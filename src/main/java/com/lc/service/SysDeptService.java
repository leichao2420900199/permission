package com.lc.service;

import com.google.common.base.Preconditions;
import com.lc.common.RequestHolder;
import com.lc.dao.SysDeptMapper;
import com.lc.dao.SysUserMapper;
import com.lc.exception.ParamException;
import com.lc.model.SysDept;
import com.lc.param.DeptParam;
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
public class SysDeptService {

    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysLogService logService;

    public void save(DeptParam param){
        SysDept dept = checkParam(param);
        dept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        deptMapper.insertSelective(dept);
        logService.saveDeptLog(null,dept);
    }

    private boolean checkExist(Integer parentId,String name,Integer id){
        return deptMapper.countByNameAndParentId(parentId,name,id)>0;
    }

    private String getLevel(Integer deptId){
        SysDept dept = deptMapper.selectByPrimaryKey(deptId);
        if(dept==null) return null;
        return dept.getLevel();
    }

    public void update(DeptParam param) {
        SysDept dept = checkParam(param);
        SysDept old = deptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(old,"待更新的部门不存在");
        dept.setId(param.getId());
        dept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        updateWithChild(old,dept);
        logService.saveDeptLog(old,dept);
    }

    @Transactional
    public void updateWithChild(SysDept oldDept,SysDept newDept){

        String newLevelPrefix = newDept.getLevel();
        String oldLevelPrefix = oldDept.getLevel();
        if (!newDept.getLevel().equals(oldDept.getLevel())) {
            List<SysDept> deptList = deptMapper.selectChildDeptByLevel(oldDept.getLevel(),oldDept.getId());
            if (CollectionUtils.isNotEmpty(deptList)) {
                for (SysDept dept : deptList) {
                    String level = dept.getLevel();
                    if (level.indexOf(oldLevelPrefix) == 0) {
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                    deptMapper.batchUpdateLevel(dept.getLevel(),dept.getId());
                }
            }
        }
        deptMapper.updateByPrimaryKeySelective(newDept);
    }

    private SysDept checkParam(DeptParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下部门名称重复");
        }
        SysDept dept = SysDept.builder().name(param.getName()).parentId(param.getParentId()).remark(param.getRemark()).seq(param.getSeq()).build();
        dept.setOperator(RequestHolder.getCurrentUser().getUsername());
        dept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        dept.setOperateTime(new Date());
        return dept;
    }

    public void delete(int id) {
        SysDept dept = deptMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(dept,"待删除的部门不存在");
        if(deptMapper.countByParentId(id)>0){
            throw new ParamException("当前部门下存在子部门，请先删除子部门");
        }
        if(userMapper.countByDeptId(id)>0){
            throw new ParamException("当前部门下存在用户，无法删除");
        }
        deptMapper.deleteByPrimaryKey(id);
    }
}
