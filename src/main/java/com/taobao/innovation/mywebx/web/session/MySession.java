package com.taobao.innovation.mywebx.web.session;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ObjectUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * sesson有几个问题要考虑：
 * 1）分布式的应用，确保不同应用，能够取得同一个sessionId
 * 2) 再基于同一个sessionId从集中式的key,value中获取，从而实现分布式的session
 *
 * Ruby on rails提供了几种策略，一种session on cookie,session on file
 *
 * User: voff12
 * Date: 13-11-2
 * Time: 下午8:46
 */
public class MySession implements HttpSession{

    private static final String SESSIONID= "sessionId";

    private volatile MySessionServletRequest request;
    private volatile  MySessionServletResponse response;
    private String sessionId;
    private long creationTime;
    private volatile int maxInactiveInterval = 1800;
    private volatile ServletContext context;
    private SessionStore sessionStore;
    private Map<String, Object> attributes = new HashMap<String, Object>();


    public MySession(MySessionServletRequest request, MySessionServletResponse response, ServletContext context) {

        this.creationTime = System.currentTimeMillis();
        this.request = request;
        this.response = response;
        this.context = context;
    }


    public void init() {
        // 初始化一下sessionStore
        sessionId = (String) getAttribute(SESSIONID);
        if (sessionId == null) {
            // 同一个域名下才能简化操作
            sessionId = DigestUtils.md5Hex(UniqId.getInstance().getUniqId());
            setAttribute(SESSIONID,sessionId);
        }
    }


    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public long getLastAccessedTime() {
        return creationTime;
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public void setMaxInactiveInterval(int i) {
        maxInactiveInterval = i;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        HashMap<String, Object> result = (HashMap<String, Object>) sessionStore.getAttribute(sessionId);
        return result.get(s);
    }

    @Override
    public Object getValue(String s) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributes.put(s,o);
        sessionStore.setAttribute(sessionId, attributes);
    }

    @Override
    public void putValue(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {
    }

    @Override
    public void removeValue(String s) {
    }

    @Override
    public void invalidate() {
        // 清除所有的值
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
