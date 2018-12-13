package com.lc.dto;

import com.google.common.collect.Lists;
import com.lc.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@ToString
public class DeptLevelDTO extends SysDept {

    private List<DeptLevelDTO> deptList = Lists.newArrayList();

    public static DeptLevelDTO adapt(SysDept dept){
        DeptLevelDTO deptLevelDTO = new DeptLevelDTO();
        BeanUtils.copyProperties(dept,deptLevelDTO);
        return deptLevelDTO;
    }
}
