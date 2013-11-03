package com.taobao.innovation.mywebx.web.session;

import redis.clients.jedis.Jedis;

/**
 * ֧�ַ���ʽsession
 * ����Ҫ��һ�����е�key,value�洢�ṹ
 * Ҫ����ʽ��ϵͳ�ǳ��ȶ�
 * <p/>
 * User: voff12
 * Date: 13-11-2
 * Time: ����10:49
 */
public class RedisSessionImpl implements SessionStore {

    private Jedis jedis;
    private String ip;

    public RedisSessionImpl(String ip) {
        this.ip = ip;
        init();
    }

    public void init() {
        jedis = new Jedis(ip);
    }

    /**
     * ����Ĭ��value��string
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
