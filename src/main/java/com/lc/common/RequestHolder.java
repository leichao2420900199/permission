package com.lc.common;

import com.lc.model.SysUser;

import javax.servlet.http.HttpServletRequest;

public class RequestHolder {


    //ThreadLocal 相当于map，key为当前线程
    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();


    public static void add(SysUser user){
        userHolder.set(user);
    }

    public static void add(HttpServletRequest request){
        requestHolder.set(request);
    }

    public static SysUser getCurrentUser(){
        return userHolder.get();
    }

    public static HttpServletRequest getCurrentRequest(){
        return requestHolder.get();
    }

    public static void remove(){
        userHolder.remove();
        requestHolder.remove();
    }
}
