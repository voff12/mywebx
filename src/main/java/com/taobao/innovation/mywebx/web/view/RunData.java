/**
 * 
 */
package com.taobao.innovation.mywebx.web.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 *  2009-03-27 增加重定向
 *  是否需要渲染模板
 *
 * @author chenxiong Feb 12, 2009
 */
public interface RunData {
	
    public HttpServletRequest getHttpServletRequest();
    
    public HttpServletResponse getHttpServletResponse();
    
    public String getParameter(String str);

    public String getStringParameter(String str, String defaultValue);

    public int getIntParameter(String str, int defaultValue);

    public int getIntParameter(String str);

    public int[] getIntParameters(String str);

    public long getLongParameter(String para, long defaultValue);

    public long getLongParameter(String para);

    public long[] getLongParameters(String para);


    /**
     * 重定向至新的URL
     *
     * @param URL
     * @return
     */
    public void redirect(String URL);

    /**
     * 
     * @param templateView
     */
    public void reTemplateDirect(String reTemplateView);

    public boolean isNeedTemplate();

    public void setIsNeedTemplate(Boolean isNeedTemplate);

    /**
     * 得到渲染的模板名
     *
     * @return
     */
    public String getTemplateView();
    
}
