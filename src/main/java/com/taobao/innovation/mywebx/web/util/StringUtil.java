/**
 * 
 */
package com.taobao.innovation.mywebx.web.util;

/**
 * @author wuxiang
 *
 * 2009-2-28--StringUtil.java
 * 2009-3-25 增加多级目录的映射
 * 2009-12-12 bug fixed
 */
public class StringUtil {

    /**
     * 首字母变大写
     *
     * @param str
     * @return
     */
    public static String upFristChar(String str) {
        if (str == null) {
            return str;
        }

        StringBuffer sb = new StringBuffer();
        char a = Character.toUpperCase(str.charAt(0));
        sb.append(a);
        sb.append(str.substring(1));
        return sb.toString();

    }

    private static String urlMapping(String str) {
        String afterStr = null;
        if (str == null) {
            return afterStr;
        // 建议要throw
        }
        if (str.indexOf("/") > 0) {
            String[] split = str.split("/");
            // 全部变成小写
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < split.length; i++) {
                //sb.append(split[i].toLowerCase());
            	sb.append(downFristChar(split[i]));
                sb.append(".");
            }
            afterStr = sb.toString();
            // 去掉刚加上这个点
            int m = afterStr.lastIndexOf(".");
            afterStr = afterStr.substring(0, m);
            // 大写最后
            int j = afterStr.lastIndexOf(".");
            String needTop = afterStr.substring(0, j);
            String needUpStr = upFristChar(afterStr.substring(j + 1));

            afterStr = needTop + "." + needUpStr;

        } else {
            // 直接规范
            afterStr = upFristChar(str);
        }
        return afterStr;
    }

    /**
     * 首字母变小写
     *
     * @param str
     * @return
     */
    public static String downFristChar(String str) {
        if (str == null) {
            return str;
        }
        StringBuffer sb = new StringBuffer();
        char a = Character.toLowerCase(str.charAt(0));
        sb.append(a);
        sb.append(str.substring(1));
        return sb.toString();
    }

    /**
     * 这个方法要改进，要不然，映射规则就很不灵活
     *
     * @param requestURI
     * @return
     */
    public static String paser(String requestURI) {

        if (requestURI == null) {
            return null;
        }

        int i = requestURI.indexOf(".");
        String paserStr = requestURI.substring(0, i);

        return paserStr;
    }

    /**
     * 此种方法目前只支持
     * 得到请求的类名
     *
     * @param paserStr
     * @return
     */
    public static String getWant(String paserStr) {
        String result = null;
        if (paserStr == null) {
            return result;
        }
        // 可能会是/myTest
        // 也可能是/project/myTest
        int j = paserStr.indexOf("/");
        result = paserStr.substring(j + 1);
        return result;
    }

    public static String getRealBean(String requestURI) {
        // 截掉.my的后缀
        String leftStr = paser(requestURI);
        // 取得想要字段
        String middleStr = getWant(leftStr);
        // 大写，转成驼峰规范
        String finalStr = urlMapping(middleStr);
        return finalStr;
    }

    public static String getTemplateView(String requestURI) {
        // 截掉.my的后缀
        String leftStr = paser(requestURI);
        // 取得想要字段
        String middleStr = getWant(leftStr);
        // 大写，转成驼峰规范
        String finalStr = TemplateViewMapping(middleStr);
        return finalStr;
    }

    /**
     * 得到模板view
     *
     * @param obj
     * @return
     */
    public static String getControllerName(Object obj) {
        String name = obj.getClass().getSimpleName();
        return convert2Small(name) + ".vm";
    }

    /**
     * 首字母变成小写
     *
     * @param str
     * @return
     */
    public static String convert2Small(String str) {
        /*StringBuffer sb = new StringBuffer();
        for (int i=0; i<str.length(); i++) {
        char n = Character.toLowerCase(str.charAt(i));
        sb.append(n);
        }
        return sb.toString();*/
        return downFristChar(str);
    }


    private static String TemplateViewMapping(String str) {
        String afterStr = null;
        if (str == null) {
            return afterStr;
        // 建议要throw
        }
        if (str.indexOf("/") > 0) {
            String[] split = str.split("/");
            // 全部变成小写
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < split.length; i++) {
            	sb.append(downFristChar(split[i]));
                sb.append("/");
            }
            afterStr = sb.toString();
            // 去掉刚加上这个/号
            int m = afterStr.lastIndexOf("/");
            afterStr = afterStr.substring(0, m);
            // 大写最后
            int j = afterStr.lastIndexOf("/");
            String needTop = afterStr.substring(0, j);
            String needdownStr = downFristChar(afterStr.substring(j + 1));

            afterStr = needTop + "/" + needdownStr;

        } else {
            // 直接规范
            afterStr = downFristChar(str);
        }
        return afterStr;
    }

    public static void main(String[] args) {
        String a = "/test/test/test/bcD.my";
        // System.out.println(paser(a));
        // System.out.println(getWant(paser(a)));
        //System.out.println(getTemplateView(a));
       // System.out.println(downFristChar("A abc"));
        
        System.out.println(urlMapping(a));
        
    }
}
