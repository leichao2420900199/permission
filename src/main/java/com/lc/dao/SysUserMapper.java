package com.lc.dao;

import com.lc.beans.PageQuery;
import com.lc.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    int countByEmailAndUserId(@Param("mail") String mail, @Param("id") Integer userId);

    int countByTelephoneAndUserId(@Param("telephone") String telephone, @Param("id") Integer userId);

    SysUser selectByKeyword(@Param("keyword") String keyword);

    List<SysUser> selectByDeptId(@Param("deptId") int deptId, @Param("page")PageQuery pageQuery);

    List<SysUser> selectByIdList(@Param("idList") List<Integer> idList);

    int countByDeptId(@Param("deptId") int deptId);

    List<SysUser> selectAllUsers();
}