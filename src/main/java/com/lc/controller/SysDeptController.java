package com.lc.controller;

import com.lc.common.JsonData;
import com.lc.dto.DeptLevelDTO;
import com.lc.param.DeptParam;
import com.lc.service.SysDeptService;
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
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {

    @Resource
    private SysDeptService deptService;
    @Resource
    private SysTreeService treeService;

    @RequestMapping("/dept.page")
    public ModelAndView page(){
        return new ModelAndView("dept");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveDept(DeptParam param){
        deptService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateDept(DeptParam param){
        deptService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData deleteDept(@RequestParam("id") int id){
        deptService.delete(id);
        return JsonData.success();
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData getDeptTree(){
        List<DeptLevelDTO> deptTree = treeService.deptTree();
        return JsonData.success(deptTree);
    }
}
