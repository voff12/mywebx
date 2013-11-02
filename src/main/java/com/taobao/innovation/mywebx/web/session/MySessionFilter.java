package com.taobao.innovation.mywebx.web.session;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: voff12
 * Date: 13-11-2
 * Time: 8:42
 */
public class MySessionFilter implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MySession session = null;

        if (servletRequest.getAttribute(getClass().getName()) != null) {
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        servletRequest.setAttribute(getClass().getName(), true);
        MySessionServletRequest myRequest = new MySessionServletRequest((HttpServletRequest) servletRequest);
        MySessionServletResponse myResponse = new MySessionServletResponse((HttpServletResponse) servletResponse);
        session = new MySession(myRequest, myResponse, filterConfig.getServletContext());
        session.init();
        myRequest.setSession(session);
        myResponse.setSession(session);
    }

    @Override
    public void destroy() {

    }
}
