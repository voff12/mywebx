package com.taobao.innovation.mywebx.web.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * 主要是获取sessionId
 * 第一次请求web A服务器，会把sessionId写到本地浏览器
 * 第二次请求web B服务器，浏览器会把session作为request请求给B服务器
 * 通过sessionId就可以获取集中式的key,value值，从而实现web层无状态
 *
 * User: voff12
 * Date: 13-11-3
 * Time: 下午12:15
 */
public class CookieSessionImpl implements SessionStore{

    private MySession mySession;

    public CookieSessionImpl(MySession session) {
        this.mySession = session;
    }


    @Override
    public void setAttribute(String key, Object value) {
        HttpServletResponse response = mySession.getResponse();
        Cookie cookie = new Cookie(key,null);
        cookie.setMaxAge(-1);  // 浏览器里进程有效
        cookie.setValue(value.toString());   // 先最简单的处理
        response.addCookie(cookie);

    }

    @Override
    public Object getAttribute(String key) {
         Cookie[] cookies =  mySession.getRequest().getCookies();
        if (cookies != null) {
            for(Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }

        return null;

    }
}
