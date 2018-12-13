package com.lc.dto;

import com.google.common.collect.Lists;
import com.lc.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@ToString
public class AclModuleLevelDTO extends SysAclModule {
    private List<AclModuleLevelDTO> aclModuleList = Lists.newArrayList();

    private List<AclDTO> aclList = Lists.newArrayList();

    public static AclModuleLevelDTO adapt(SysAclModule aclModule) {
        AclModuleLevelDTO dto = new AclModuleLevelDTO();
        BeanUtils.copyProperties(aclModule, dto);
        return dto;
    }
}
