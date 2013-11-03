package com.taobao.innovation.mywebx.web.session;

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
 * sesson�м�������Ҫ���ǣ�
 * 1���ֲ�ʽ��Ӧ�ã�ȷ����ͬӦ�ã��ܹ�ȡ��ͬһ��sessionId
 * ʵ�ֲַ�ʽӦ�û�ȡ��ͬһ��sessionId�ķ�ʽ����ͨ��cookie����ȡ
 * 2) �ٻ���ͬһ��sessionId�Ӽ���ʽ��key,value�л�ȡ���Ӷ�ʵ�ֲַ�ʽ��session
 * <p/>
 * Ruby on rails�ṩ�˼��ֲ��ԣ�һ��session on cookie,session on file
 * <p/>
 * User: voff12
 * Date: 13-11-2
 * Time: ����8:46
 */
public class MySession implements HttpSession {

    public static final String SESSIONID = "sessionId";
    public static final String COOKIE_SESSION = "cookie";
    public static final String CENTER_SESSION = "center";

    private volatile MySessionServletRequest request;
    private volatile MySessionServletResponse response;
    private String sessionId;
    private long creationTime;
    private volatile int maxInactiveInterval = 1800;
    private volatile ServletContext context;
    private Map<String, SessionStore> sessionStoreMap ;
    private Map<String, Object> attributes = new HashMap<String, Object>();


    public MySession(MySessionServletRequest request, MySessionServletResponse response, ServletContext context,Map<String, SessionStore> sessionStoreMap) {

        this.creationTime = System.currentTimeMillis();
        this.request = request;
        this.response = response;
        this.context = context;
        this.sessionStoreMap = sessionStoreMap;
    }


    public void init() {
        // ��ʼ��һ��sessionStore
        sessionId = (String) getAttribute(SESSIONID);
        if (sessionId == null) {
            // ͬһ�������²��ܼ򻯲���
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
            SessionStore cookieStore = new CookieSessionImpl(this);
            return cookieStore.getAttribute(s);
        }

        SessionStore centerStore = this.sessionStoreMap.get(CENTER_SESSION); // ����ʽ��session�洢
        HashMap<String, Object> result = (HashMap<String, Object>) centerStore.getAttribute(sessionId);
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
        if (SESSIONID.equals(s)) {
            SessionStore cookieStore = new CookieSessionImpl(this);
            cookieStore.setAttribute(s, o);
            return;
        }
        attributes.put(s, o);
        SessionStore centerStore = this.sessionStoreMap.get(CENTER_SESSION); // ����ʽ��session�洢
        centerStore.setAttribute(sessionId, attributes);
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
        // ������е�ֵ
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