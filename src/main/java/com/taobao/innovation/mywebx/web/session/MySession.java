package com.taobao.innovation.mywebx.web.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ObjectUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * sesson有几个问题要考虑：
 * 1）分布式的应用，确保不同应用，能够取得同一个sessionId
 * 实现分布式应用获取到同一个sessionId的方式就是通过cookie来获取
 * 2) 再基于同一个sessionId从集中式的key,value中获取，从而实现分布式的session
 * <p/>
 * Ruby on rails提供了几种策略，一种session on cookie,session on file
 * <p/>
 * User: voff12
 * Date: 13-11-2
 * Time: 下午8:46
 */
public class MySession implements HttpSession {

    public static final String SESSIONID = "sessionId";
    public static final String CENTER_SESSION = "center";

    private volatile MySessionServletRequest request;
    private volatile MySessionServletResponse response;
    private String sessionId;
    private long creationTime;
    private volatile int maxInactiveInterval = 1800;
    private volatile ServletContext context;
    private Map<String, SessionStore> sessionStoreMap;
    private String domain;
    private Map<String, Object> attributes = new HashMap<String, Object>();


    public MySession(MySessionServletRequest request, MySessionServletResponse response, ServletContext context, Map<String, SessionStore> sessionStoreMap, String domain) {

        this.creationTime = System.currentTimeMillis();
        this.request = request;
        this.response = response;
        this.context = context;
        this.sessionStoreMap = sessionStoreMap;
        this.domain = domain;
    }


    public void init() {
        // 初始化一下sessionStore
        sessionId = (String) getAttribute(SESSIONID);
        if (sessionId == null) {
            // 同一个域名下才能简化操作
            sessionId = DigestUtils.md5Hex(UniqId.getInstance().getUniqId());
            setAttribute(SESSIONID, sessionId);
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
        if (SESSIONID.equals(s)) {
            SessionStore cookieStore = new CookieSessionImpl(this, domain);
            return cookieStore.getAttribute(s);
        }

        SessionStore centerStore = this.sessionStoreMap.get(CENTER_SESSION); // 集中式的session存储
        String json = (String) centerStore.getAttribute(sessionId);
        HashMap<String, Object> result = (HashMap<String, Object>) this.toObject(json);
        return result == null ? null : result.get(s);
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
        if (SESSIONID.equals(s)) {
            SessionStore cookieStore = new CookieSessionImpl(this, domain);
            cookieStore.setAttribute(s, o);
            return;
        }
        attributes.put(s, o);
        SessionStore centerStore = this.sessionStoreMap.get(CENTER_SESSION); // 集中式的session存储
        centerStore.setAttribute(sessionId, toJsonString(attributes));
    }


    public static void main(String[] args) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("nick", "voff12");
        System.out.println(toJsonString(attributes));
        System.out.println(toObject(toJsonString(attributes)));
    }

    private static String toJsonString(Map<String, Object> attributes) {
        return new GsonBuilder().create().toJson(attributes);
    }


    private static Map<String, Object> toObject(String json) {
        return new GsonBuilder().create().fromJson(json, Map.class);
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


    public HttpServletRequest getRequest() {
        return this.request;
    }


    public HttpServletResponse getResponse() {
        return this.response;
    }
}
