/**
 * 
 */
package com.taobao.innovation.mywebx.web.util;

import java.util.Date;

/**
 * @author chenxiong
 *
 * Feb 28, 2009--DateUtil.java
 */
public class DateUtil {
	
	public DateUtil() {}

    private static final long MINUTE_MSEL = 60 * 1000L;//一分钟的毫秒值
    private static final long HOUR_MSEL = 60 * MINUTE_MSEL;//一小时的毫秒值
    private static final long DATE_MSEL = 24 * HOUR_MSEL;//一天的毫秒值
	
    /**
     * 格式化创建日期.
     * XX天前
     * XX小时前
     * XX分钟前
     * 
     * @param date
     * @return
     */
	public static String format(Date date) {
    	long c = date.getTime();//createdDate
        long n = System.currentTimeMillis();//now
        long interval = n - c;
        
        int d = (int)(interval / DATE_MSEL);
        if (d > 0) {
        	return "" + d + "天前";
        } 
        
        int h = (int)(interval / HOUR_MSEL);
        if (h > 0) {
        	return "" + h + "小时前";
        }
        
        int m = (int)(interval / MINUTE_MSEL);
        if (m > 0) {
        	return "" + m + "分钟前";
        }
        return "刚刚";
	}

}
