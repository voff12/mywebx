package com.taobao.innovation.mywebx.web.session;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: voff12
 * Date: 13-11-2
 * Time: обнГ8:52
 */
public class MySessionServletResponse extends HttpServletResponseWrapper{

    private MySession mySession;

    public void setSession(MySession session) {
        mySession = session;
    }

    public MySession getSession(){
          return mySession;
    }

    public MySessionServletResponse(HttpServletResponse response) {
        super(response);
    }
}
