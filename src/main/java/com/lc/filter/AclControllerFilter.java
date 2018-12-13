package com.lc.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.lc.common.ApplicationContextHelper;
import com.lc.common.JsonData;
import com.lc.common.RequestHolder;
import com.lc.model.SysUser;
import com.lc.service.SysCoreService;
import com.lc.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class AclControllerFilter implements Filter {

    private static Set<String> exclusionUrlSet = Sets.newConcurrentHashSet();
    private final static String NO_AUTH_URL = "/sys/user/noAuth.page";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //白名单
        String exclusionUrls = filterConfig.getInitParameter("exclusionUrls");
        List<String> exclusionUrlList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(exclusionUrls);
        exclusionUrlSet = Sets.newConcurrentHashSet(exclusionUrlList);
        exclusionUrlSet.add(NO_AUTH_URL);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Map requestMap = request.getParameterMap();
        String servletPath = request.getServletPath();
        if(exclusionUrlSet.contains(servletPath)){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        SysUser user = RequestHolder.getCurrentUser();
        if(user==null){
            log.info("someone visit {},but no login,paramter:{}",servletPath, JsonMapper.obj2String(requestMap));
            noAuth(request,response);
            return;
        }

        SysCoreService coreService = ApplicationContextHelper.popBean(SysCoreService.class);
        if(!coreService.hasUrlAcl(servletPath)){
            log.info("{} visit {},paramter:{}",JsonMapper.obj2String(user),servletPath, JsonMapper.obj2String(requestMap));
            noAuth(request,response);
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);
        return;

    }

    private void noAuth(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String servletPath = request.getServletPath();
        if(servletPath.endsWith(".json")){
            JsonData data = JsonData.fail("没有权限访问,请联系管理员");
            response.setHeader("Content-Type","application/json");
            response.getWriter().println(JsonMapper.obj2String(data));
        }else {
            clientRedirect(NO_AUTH_URL,response);
        }
    }

    private void clientRedirect(String url,HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type","text/html");
        response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                + "window.location.href='" + url + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
    }

    @Override
    public void destroy() {

    }
}
