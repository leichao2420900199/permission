package com.lc.controller;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lc.beans.PageQuery;
import com.lc.common.JsonData;
import com.lc.model.SysUser;
import com.lc.param.RoleParam;
import com.lc.service.*;
import com.lc.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    private SysRoleService roleService;
    @Resource
    private SysTreeService treeService;
    @Resource
    private SysRoleAclService roleAclService;
    @Resource
    private SysRoleUserService roleUserService;
    @Resource
    private SysUserService userService;

    @RequestMapping("role.page")
    public ModelAndView page(){
        return new ModelAndView("role");
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData all(){
        return JsonData.success(roleService.findAll());
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveRole(RoleParam param){
        roleService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateRole(RoleParam param){
        roleService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JsonData getRoleTree(@RequestParam("roleId") int roleId){
        return JsonData.success(treeService.roleTree(roleId));
    }


    @RequestMapping("/changeAcls.json")
    @ResponseBody
    public JsonData changeAcl(@RequestParam("roleId") int roleId,@RequestParam(value = "aclIds",required = false,defaultValue = "") String aclIds){
        List<Integer> aclIdList = StringUtil.splitToListInt(aclIds);
        roleAclService.changeRoleAcls(roleId,aclIdList);
        return JsonData.success();
    }

    @RequestMapping("/users.json")
    @ResponseBody
    public JsonData getRoleUsers(@RequestParam("roleId") int roleId){
        List<SysUser> selectedUsers = roleUserService.findUsersByRoleId(roleId);
        List<SysUser> users = userService.findAllUsers();
        Set<Integer> selectedUsersSet = selectedUsers.stream().map(user -> user.getId()).collect(Collectors.toSet());
        List<SysUser> unselectedUsers = Lists.newArrayList();
        for(SysUser user:users){
            if(user.getStatus()==1 && !selectedUsersSet.contains(user.getId())){
                unselectedUsers.add(user);
            }
        }
       // selectedUsers = selectedUsers.stream().filter(user -> user.getStatus()!=1).collect(Collectors.toList());
        Map<String,List<SysUser>> result = Maps.newLinkedHashMap();
        result.put("selected",selectedUsers);
        result.put("unselected",unselectedUsers);
        return JsonData.success(result);
    }


    @RequestMapping("/changeUsers.json")
    @ResponseBody
    public JsonData changeUser(@RequestParam("roleId") int roleId,@RequestParam(value = "userIds",required = false,defaultValue = "") String userIds){
        List<Integer> userIdList = StringUtil.splitToListInt(userIds);
        roleUserService.changeRoleUsers(roleId,userIdList);
        return JsonData.success();
    }

}
