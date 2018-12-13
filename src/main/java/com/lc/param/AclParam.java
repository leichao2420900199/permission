package com.lc.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class AclParam {

    private Integer id;

    @NotBlank(message = "权限点名称不能为空")
    @Length(min = 2,max = 20,message = "权限点名称长度在2到20之间")
    private String name;

    @NotNull(message = "必须指定权限模块")
    private Integer aclModuleId;

    @Length(min = 6,max = 100,message = "权限点URL长度必须在6到100之间")
    private String url;

    @NotNull(message = "必须指定权限点的类型")
    @Min(value = 1,message = "权限点类型不合法")
    @Max(value = 3,message = "权限点类型不合法")
    private Integer type;

    @NotNull(message = "必须指定权限点的状态")
    @Min(value = 0,message = "权限点状态不合法")
    @Max(value = 2,message = "权限点状态不合法")
    private Integer status;

    @NotNull(message = "必须指定权限点的显示顺序")
    private Integer seq;

    @Length(max = 256,message = "备注长度不能超过256个字")
    private String remark="";
}
