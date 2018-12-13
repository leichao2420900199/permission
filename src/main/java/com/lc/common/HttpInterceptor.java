package com.lc.common;

import com.lc.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURL().toString();
        Map paramMap =  request.getParameterMap();
        log.info("request before ...url:{},params:{}",url, JsonMapper.obj2String(paramMap));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String url = request.getRequestURL().toString();
        Map paramMap =  request.getParameterMap();
        log.info("request after ...url:{},params:{}",url, JsonMapper.obj2String(paramMap));
        RequestHolder.remove();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url = request.getRequestURL().toString();
        Map paramMap =  request.getParameterMap();
        log.info("request after ...url:{},params:{}",url, JsonMapper.obj2String(paramMap));
        RequestHolder.remove();
    }
}
