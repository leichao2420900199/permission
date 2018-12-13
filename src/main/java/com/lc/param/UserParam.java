package com.lc.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserParam {

    private Integer id;

    @NotBlank(message = "用户名不能为空")
    @Length(min = 1,max = 20,message = "用户名长度在1到20之间")
    private String username;

    @NotBlank(message = "联系电话不能为空")
    @Length(min = 1,max = 13,message = "电话长度在1到13之间")
    private String telephone;

    @NotBlank(message = "邮箱不能为空")
    @Length(min = 5,max = 50,message = "邮箱长度在5到50之间")
    private String mail;

    @NotNull(message = "用户所在部门不能为空")
    private Integer deptId;

    @NotNull(message = "用户状态不能为空")
    @Min(value = 0,message = "用户状态不合法")
    @Max(value = 2,message = "用户状态不合法")
    private Integer status;

    @Length(max = 200,message = "备注长度在0到200之间")
    private String remark = "";
}
