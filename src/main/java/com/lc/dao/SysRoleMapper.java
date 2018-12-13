package com.lc.dao;

import com.lc.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    int countByNameAndId(@Param("name") String name, @Param("id") Integer id);

    List<SysRole> selectRoles();

    List<SysRole> selectByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);
}