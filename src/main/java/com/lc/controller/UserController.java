package com.lc.controller;

import com.lc.model.SysUser;
import com.lc.service.SysUserService;
import com.lc.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    @Resource
    private SysUserService userService;

    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = request.getParameter("username");
        String password = request.getParameter("password");


        SysUser user = userService.findByKeyword(username);
        String errorMsg = "";
        String ret = request.getParameter("ret");

        if(StringUtils.isBlank(username)){
            errorMsg = "用户名不能为空";
        }else if(StringUtils.isBlank(password)){
            errorMsg = "密码不能为空";
        }else if(user == null){
            errorMsg = "用户不存在，请先注册";
        }else if (!user.getPassword().equals(MD5Util.encrypt(password))){
            errorMsg = "用户名或密码不匹配";
        }else if(user.getStatus()!=1){
            errorMsg = "此用户已被冻结，请联系管理员";
        }else {
            //login
            request.getSession().setAttribute("user",user);
            if(StringUtils.isNotBlank(ret)){
                request.setAttribute("ret",ret);
                response.sendRedirect(ret);
            }else {
                response.sendRedirect("/admin/index.page");
            }
        }

        request.setAttribute("error",errorMsg);
        request.setAttribute("username",username);

        String path = "signin.jsp";
        request.getRequestDispatcher(path).forward(request,response);
    }


    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().invalidate();
        String path = "signin.jsp";
        response.sendRedirect(path);
    }
}
