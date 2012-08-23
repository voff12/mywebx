/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web;

import com.taobao.innovation.mywebx.web.view.RunData;
import com.taobao.innovation.mywebx.web.view.Template;

/**
 *
 * @author wuxiang
 */
public interface HandlerInterceptor {

    boolean preHandle(RunData rundata, Template context, Object handler);

    void postHandle(RunData rundata, Template context, Object handler);

}
