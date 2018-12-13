package com.lc.dao;

import com.lc.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    List<SysDept> selectAllDept();

    List<SysDept> selectChildDeptByLevel(@Param("level") String level,@Param("id") Integer id);

    void batchUpdateLevel(@Param("level") String level,@Param("id")Integer id);

    int countByNameAndParentId(@Param("parentId")int parentId,@Param("name")String name,@Param("id")Integer id);

    int countByParentId(@Param("id")Integer id);
}