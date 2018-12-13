package com.lc.dao;

import com.lc.model.SysRoleUser;
import com.lc.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

public interface SysRoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);

    List<Integer> selectRoleIdByUserId(int userId);

    List<Integer> selectUserIdsByRoleId(int roleId);

    void deleteByRoleId(int roleId);

    void batchInsert(@Param("roleUserList") List<SysRoleUser> roleUserList);

    List<Integer> selectUserIdsByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);
}