package com.taobao.innovation.mywebx.web.session;

/**
 * 支持分享式session
 * 必须要有一个集中的key,value存储结构
 * 要求集中式的系统非常稳定
 *
 * User: voff12
 * Date: 13-11-2
 * Time: 下午10:49
 */
public class RedisSessionImpl implements SessionStore{

    public void init() {

    }

    @Override
    public void setAttribute(String key, Object value) {

    }

    @Override
    public Object getAttribute(String key) {

        return null;
    }
}
