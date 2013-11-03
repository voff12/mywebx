package com.taobao.innovation.mywebx.web.session;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: voff12
 * Date: 13-11-2
 * Time: 8:42
 */
public class MySessionFilter implements Filter {

    private FilterConfig filterConfig;
    private String ip;
    private String domain;
    private Map<String, SessionStore> sessionStoreMap;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        // 这里只会初始化一次
        sessionStoreMap = new HashMap<String, SessionStore>();
        ip = filterConfig.getInitParameter("ip");
        // 初始化一下redis
        domain = filterConfig.getInitParameter("domain");
        SessionStore store = new RedisSessionImpl(ip,0); // FIXME later
        sessionStoreMap.put(MySession.CENTER_SESSION, store);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MySession session = null;

        if (!(servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse)
         || servletRequest.getAttribute(getClass().getName()) != null) {
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        servletRequest.setAttribute(getClass().getName(), true);
        MySessionServletRequest myRequest = new MySessionServletRequest((HttpServletRequest) servletRequest);
        MySessionServletResponse myResponse = new MySessionServletResponse((HttpServletResponse) servletResponse);
        session = new MySession(myRequest, myResponse, filterConfig.getServletContext(), sessionStoreMap);
        session.init();
        myRequest.setSession(session);
        myResponse.setSession(session);

        filterChain.doFilter(myRequest,myResponse); // continue?
    }

    @Override
    public void destroy() {

    }
}
