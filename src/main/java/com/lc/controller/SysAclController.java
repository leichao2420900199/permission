package com.lc.controller;

import com.google.common.collect.Maps;
import com.lc.beans.PageQuery;
import com.lc.common.JsonData;
import com.lc.model.SysRole;
import com.lc.param.AclParam;
import com.lc.service.SysAclService;
import com.lc.service.SysRoleService;
import com.lc.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sys/acl")
public class SysAclController {

    @Resource
    private SysAclService aclService;
    @Resource
    private SysRoleService roleService;
    @Resource
    private SysUserService userService;


    @RequestMapping("/acl.json")
    @ResponseBody
    public JsonData page(@RequestParam("aclModuleId") int aclModuleId, PageQuery pageQuery){
        return JsonData.success(aclService.findAclPageByAclModuleId(aclModuleId,pageQuery));
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveAcl(AclParam param){
        aclService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAcl(AclParam param){
        aclService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/acls.json")
    @ResponseBody
    public JsonData getAcls(@RequestParam("aclId") int aclId){
        Map<String,Object> map = Maps.newHashMap();
        List<SysRole> roleList = roleService.findRoleListByAcId(aclId);
        map.put("roles",roleList);
        List<Integer> roleIdList = roleList.stream().map(sysRole -> sysRole.getId()).collect(Collectors.toList());
        map.put("users", userService.findUsersByRoleIdList(roleIdList));
        return JsonData.success(map);
    }
}
