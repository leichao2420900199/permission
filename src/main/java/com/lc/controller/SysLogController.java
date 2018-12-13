package com.lc.controller;

import com.lc.beans.PageQuery;
import com.lc.common.JsonData;
import com.lc.param.SearchLogParam;
import com.lc.service.SysLogService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/log")
public class SysLogController {

    @Resource
    private SysLogService logService;

    @RequestMapping("/log.page")
    public ModelAndView page(){
        return new ModelAndView("log");
    }

    @RequestMapping("/log.json")
    @ResponseBody
    public JsonData log(SearchLogParam param, PageQuery query){
        return JsonData.success(logService.searchPageList(param,query));
    }

    @RequestMapping("/recover.json")
    @ResponseBody
    public JsonData recover(@Param("id") int id){
        logService.recover(id);
        return JsonData.success();
    }
}
