package com.lc.common;

import com.lc.exception.ParamException;
import com.lc.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        String url = httpServletRequest.getRequestURI().toString();
        ModelAndView mv ;
        String defaultMsg = "system error";
        //通过判断后缀 .json  .page来判断是json请求还是页面请求
        if(url.endsWith(".json")){
            if(e instanceof PermissionException || e instanceof ParamException){
                JsonData jsonData = JsonData.fail(e.getMessage());
                mv = new ModelAndView("jsonView",jsonData.toMap());
            }else {
                log.error("unknown json exception,url:"+url,e);
                JsonData jsonData = JsonData.fail(defaultMsg);
                mv = new ModelAndView("jsonView",jsonData.toMap());
            }
        } else if(url.endsWith(".page")){
            log.error("unknown page exception,url:"+url,e);
            JsonData jsonData = JsonData.fail(defaultMsg);
            mv = new ModelAndView("exception",jsonData.toMap());
        } else {
            log.error("unknown json exception,url:"+url,e);
            JsonData jsonData = JsonData.fail(defaultMsg);
            mv = new ModelAndView("jsonView",jsonData.toMap());
        }
        return mv;
    }
}
