package com.lc.dao;

import com.lc.model.SysAclModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    int countByNameAndParentId(@Param("parentId")int parentId, @Param("name")String name, @Param("id")Integer id);

    void batchUpdateLevel(@Param("level") String level, @Param("id") Integer id);

    List<SysAclModule> selectChildAclModuleByLevel(@Param("level") String level,@Param("id") Integer id);

    List<SysAclModule> selectAllAclModule();

    int countByParentId(int id);
}