package com.lc.controller;

import com.lc.common.ApplicationContextHelper;
import com.lc.common.JsonData;
import com.lc.dao.SysAclModuleMapper;
import com.lc.exception.PermissionException;
import com.lc.model.SysAclModule;
import com.lc.param.TestVo;
import com.lc.util.BeanValidator;
import com.lc.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello(){
        log.info("hello");
        //测试异常
        throw new PermissionException("test exception");
       // return JsonData.success("hello permission");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo){
        log.info("validate");
        try {
            Map<String,String> map = BeanValidator.validateObject(vo);
            if(map!=null && map.entrySet().size()>0){
                for (Map.Entry<String,String> entry:map.entrySet()) {
                    log.info("{}---{}",entry.getKey(),entry.getValue());
                }
            }
        } catch (Exception e){

        }

        return JsonData.success("hello permission");
    }

    @RequestMapping("/applicationContextHelper.json")
    @ResponseBody
    public JsonData applicationContextHelper(TestVo vo){
        log.info("applicationContextHelper");
        SysAclModuleMapper moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule module = moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(module));
        return JsonData.success("hello permission");
    }
}
