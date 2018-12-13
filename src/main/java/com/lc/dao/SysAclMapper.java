package com.lc.dao;

import com.lc.beans.PageQuery;
import com.lc.model.SysAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    int countByNameAndAclNoduleId(@Param("aclModuleId") int aclModuleId, @Param("name") String name, @Param("id") Integer id);

    int countByAclModuleId(int aclModuleId);

    List<SysAcl> selectByAclModuleId(@Param("aclModuleId") int aclModuleId, @Param("page") PageQuery pageQuery);

    List<SysAcl> selectAllAcl();

    List<SysAcl> selectByAclIds(@Param("idList") List<Integer> aclIdList);

    List<SysAcl> selectByUrl(String url);
}