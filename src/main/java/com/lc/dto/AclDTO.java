package com.lc.dto;

import com.lc.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@ToString
public class AclDTO extends SysAcl {

    //权限点是否默认选中
    private boolean checked = false;
    //权限点是否可操作
    private boolean hasAcl = false;

    public static AclDTO adapt(SysAcl acl){
        AclDTO dto = new AclDTO();
        BeanUtils.copyProperties(acl,dto);
        return dto;
    }

}
