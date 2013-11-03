package com.taobao.innovation.mywebx.web.session;

import redis.clients.jedis.Jedis;

/**
 * 支持分享式session
 * 必须要有一个集中的key,value存储结构
 * 要求集中式的系统非常稳定
 * <p/>
 * User: voff12
 * Date: 13-11-2
 * Time: 下午10:49
 */
public class RedisSessionImpl implements SessionStore {

    private Jedis jedis;
    private String ip;
    private int port;


    public RedisSessionImpl(String ip, int port) {
        this.ip = ip;
        this.port = port;
        init();
    }

    public void init() {
        jedis = new Jedis(ip,6379);
    }

    /**
     * 现在默认value是string
     *
     * @param key
     * @param value
     */
    @Override
    public void setAttribute(String key, Object value) {
        jedis.set(key, value.toString());
    }

    @Override
    public Object getAttribute(String key) {
        return jedis.get(key);
    }
}
