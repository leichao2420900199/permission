package com.lc.controller;

import com.google.common.collect.Maps;
import com.lc.beans.PageQuery;
import com.lc.common.JsonData;
import com.lc.param.UserParam;
import com.lc.service.SysRoleService;
import com.lc.service.SysTreeService;
import com.lc.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService userService;
    @Resource
    private SysTreeService treeService;
    @Resource
    private SysRoleService roleService;


    @RequestMapping("/noAuth.page")
    public ModelAndView noAuth(){
        return new ModelAndView("noAuth");
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(@RequestParam("deptId") int deptId, PageQuery pageQuery){
        return JsonData.success(userService.findUserPageByDeptId(deptId,pageQuery));
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveDept(UserParam param){
        userService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateDept(UserParam param){
        userService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/acls.json")
    @ResponseBody
    public JsonData getUserAcls(@RequestParam("userId") int userId){
        Map<String,Object> map = Maps.newHashMap();
        map.put("acls",treeService.userAclTree(userId));
        map.put("roles",roleService.findRoleListByUserId(userId));
        return JsonData.success(map);
    }
}
