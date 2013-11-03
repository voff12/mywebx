package com.taobao.innovation.mywebx.web.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * ��Ҫ�ǻ�ȡsessionId
 * ��һ������web A�����������sessionIdд�����������
 * �ڶ�������web B����������������session��Ϊrequest�����B������
 * ͨ��sessionId�Ϳ��Ի�ȡ����ʽ��key,valueֵ���Ӷ�ʵ��web����״̬
 *
 * User: voff12
 * Date: 13-11-3
 * Time: ����12:15
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
        cookie.setMaxAge(-1);  // ������������Ч
        cookie.setValue(value.toString());   // ����򵥵Ĵ���
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
