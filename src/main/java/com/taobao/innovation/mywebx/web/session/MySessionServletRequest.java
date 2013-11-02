package com.taobao.innovation.mywebx.web.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * User: voff12
 * Date: 13-11-2
 * Time: обнГ8:45
 */
public class MySessionServletRequest extends HttpServletRequestWrapper{

    private MySession mySession;


    public void setSession(MySession mySession) {
        this.mySession = mySession;
    }

    public MySessionServletRequest(HttpServletRequest request) {

        super(request);
    }

    @Override
    public HttpSession getSession() {
        return mySession;

    }
}
