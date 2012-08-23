/**
 * 
 */
package com.taobao.innovation.mywebx.web.view;

import com.taobao.innovation.mywebx.web.util.ServletRequestUtils;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author chenxiong Feb 21, 2009
 * @author chenxiong jule 02, 2009
 */
public class DefaultRunData implements RunData{

    private static final Log logger = LogFactory.getLog(DefaultRunData.class);
	
	public DefaultRunData(HttpServletRequest request,  HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
    private HttpServletRequest request;
	private HttpServletResponse response;

    // 增加
    private String templateView;
    // 是否需要渲染模板 
    private Boolean isNeedTemplate = true;

	public HttpServletRequest getHttpServletRequest() {
		return request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public HttpServletResponse getHttpServletResponse() {
		return response;
	}

	public String getParameter(String str) {
		return this.request.getParameter(str);
	}

    public void setIsNeedTemplate(Boolean isNeedTemplate) {
        this.isNeedTemplate = isNeedTemplate;
    }

    public void redirect(String URL) {
        try {
            this.isNeedTemplate = false;
            logger.info("重定向至" + URL);
            this.response.sendRedirect(URL);
            logger.info("重定向后是否回来..." + URL);
        } catch (IOException ex) {
            logger.error(DefaultRunData.class.getName(), ex);
        }
    }

    public void setTemplateView(String templateView) {
        this.templateView = templateView;
    }

    public String getTemplateView() {
        return templateView;
    }

    public void reTemplateDirect(String reTemplateView) {
        this.templateView = reTemplateView;
    }

    public boolean isNeedTemplate() {
        return isNeedTemplate;
    }

    public String getStringParameter(String str, String defaultValue) {
        return ServletRequestUtils.getStringParameter(request, str, defaultValue);
    }

    public int getIntParameter(String str, int defaultValue) {
       return ServletRequestUtils.getIntParameter(request, str, defaultValue);
    }

    public int getIntParameter(String str) {
        return ServletRequestUtils.getIntParameter(request, str, 0);
    }

    public long getLongParameter(String para, long defaultValue) {
        return ServletRequestUtils.getLongParameter(request, para, defaultValue);
    }

    public long getLongParameter(String para) {
        return ServletRequestUtils.getLongParameter(request, para, 0L);
    }

    public int[] getIntParameters(String str) {
        return ServletRequestUtils.getIntParameters(request, str);
    }

    public long[] getLongParameters(String para) {
        return ServletRequestUtils.getLongParameters(request, para);
    }

}
