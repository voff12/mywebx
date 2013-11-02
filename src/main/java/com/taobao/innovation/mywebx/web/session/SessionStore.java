package com.taobao.innovation.mywebx.web.session;

/**
 * User: voff12
 * Date: 13-11-2
 * Time: обнГ10:39
 */
public interface SessionStore {

   void setAttribute(String key, Object value);

   Object getAttribute(String key);
}
