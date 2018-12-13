package com.lc.controller;

import com.lc.common.JsonData;
import com.lc.dto.AclModuleLevelDTO;
import com.lc.param.AclModuleParam;
import com.lc.service.SysAclModuleService;
import com.lc.service.SysTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/aclModule")
@Slf4j
public class SysAclModuleController {

    @Resource
    private SysAclModuleService aclModuleService;
    @Resource
    private SysTreeService treeService;

    @RequestMapping("/acl.page")
    public ModelAndView page(){
        return new ModelAndView("acl");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveAclModule(AclModuleParam param){
        aclModuleService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAclModule(AclModuleParam param){
        aclModuleService.update(param);
        return JsonData.success();
    }


    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData deleteDept(@RequestParam("id") int id){
        aclModuleService.delete(id);
        return JsonData.success();
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData getAclModuleTree(){
        List<AclModuleLevelDTO> aclModuleTree = treeService.aclModuleTree();
        return JsonData.success(aclModuleTree);
    }

}
