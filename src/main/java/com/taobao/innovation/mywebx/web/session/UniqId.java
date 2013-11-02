package com.taobao.innovation.mywebx.web.session;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Date: 13-11-2
 * Time: ÏÂÎç9:21
 */
public class UniqId {

    private static String hostaddr;
    private static Random random = new SecureRandom();
    private static  UniqId me = new UniqId();

    private  UniqId() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostaddr =  address.getHostAddress();
        } catch (Throwable e) {

        }
    }


    public static UniqId getInstance() {
        return me;
    }

    public String getUniqId() {
        StringBuffer sb = new StringBuffer();
        long t = System.currentTimeMillis();
        sb.append(t);
        sb.append("-");
        sb.append(random.nextInt(89999) + 10000);
        sb.append("-");
        sb.append(hostaddr);
        sb.append("-");
        sb.append(Thread.currentThread().hashCode());
        return sb.toString();
    }


    public static void main(String[] args) {

        System.out.println(UniqId.getInstance().getUniqId());
    }
}
