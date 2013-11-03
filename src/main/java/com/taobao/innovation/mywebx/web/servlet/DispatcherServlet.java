/**
 * 
 */
package com.taobao.innovation.mywebx.web.servlet;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.web.context.WebApplicationContext;

import com.taobao.innovation.mywebx.web.HandlerInterceptor;
import com.taobao.innovation.mywebx.web.annotation.Controller;
import com.taobao.innovation.mywebx.web.annotation.MultiController;
import com.taobao.innovation.mywebx.web.control.IdeaControl;
import com.taobao.innovation.mywebx.web.exception.MyHttpServiceException;
import com.taobao.innovation.mywebx.web.exception.NoControllerFindException;
import com.taobao.innovation.mywebx.web.exception.NoTemplateFindException;
import com.taobao.innovation.mywebx.web.multipart.DefaultMyMultipartResolver;
import com.taobao.innovation.mywebx.web.multipart.MyMultipartResolver;
import com.taobao.innovation.mywebx.web.pull.PullFactory;
import com.taobao.innovation.mywebx.web.util.ClassUtil;
import com.taobao.innovation.mywebx.web.util.StringUtil;
import com.taobao.innovation.mywebx.web.view.DefaultRunData;
import com.taobao.innovation.mywebx.web.view.DefaultTemplate;
import com.taobao.innovation.mywebx.web.view.RunData;
import com.taobao.innovation.mywebx.web.view.Template;

/**
 * 
 * @author chenxiong Feb 12, 2009
 */
public class DispatcherServlet extends MyFrameworkServlet {

	private static final long serialVersionUID = -2872619873662204387L;
	private VelocityEngine velocityEngine = null;
	private static final Log logger = LogFactory.getLog("myframework");
	public static final String TEMPLATE_LOCATION_PARAM = "moduleLocation";
	public static final String EXCEPTION_HANDLED_STATUS = "exceptionHandle";
	public static final String DEFAULT_CONTROL = "com.taobao.innovation.mywebx.web.control.DefaultControl";
	private ConcurrentHashMap<String, Object> currentHashMap = null;
	private ConcurrentHashMap<String, Object> controlHashMap = new ConcurrentHashMap<String, Object>();
	private List<HandlerInterceptor> handlerInterceptors;

	/**
	 * ��ʼ��ģ����ֿ spring�ĳ�ʼ��ͳһ��
	 * org.springframework.web.context.ContextLoaderListener����ʼ��
	 */
	public final void init() throws ServletException {
		synchronized(this) { // ����ͬ���� 
			initVelocityEngine();
			try {
				pullIntoHashMap();
			} catch (MyHttpServiceException ex) {
				throw new ServletException(ex);
			}
			WebApplicationContext context = getSpringContext();
			Map matchingBeans = context.getBeansOfType(HandlerInterceptor.class);
			if (!matchingBeans.isEmpty()) {
				handlerInterceptors = new ArrayList(matchingBeans.values());
			}
		}
	}

	/**
	 * <p>
	 * �õ�spring���� ����ģ����ֿ ��װrundata session���Ʋ��� ����sessioId��õ�cookie ע��pull ҳ�湤��
	 * ��context �õ�AOP controller������ ����controller
	 * </p>
	 */
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			doDispatch(request, response);
		} catch (Exception e) {
			logger.error("doService", e);
			throw new MyHttpServiceException(e);
		}
	}

	/**
	 * ���մ���
	 * 
	 * @param request  ����
	 * @param response   ��Ӧ
	 * @throws Exception
	 */
	protected void doDispatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// ������filter����������
		request.setCharacterEncoding("GBK");
		response.setContentType("text/html;charset=GBK");
		/** session based on cookie */
		HttpServletRequest processedRequest = request;
		processedRequest = checkMultipart(request);
		RunData rundata = new DefaultRunData(processedRequest, response);
		Template context = new DefaultTemplate();
		pullUtil(context);
		Object obj = getImplementObjectClass(rundata);
		if (handlerInterceptors != null && !handlerInterceptors.isEmpty()) {
			HandlerInterceptor[] interceptors = handlerInterceptors
					.toArray(new HandlerInterceptor[handlerInterceptors.size()]);
			for (int i = 0; i < interceptors.length; i++) {
				HandlerInterceptor interceptor = interceptors[i];
				interceptor.preHandle(rundata, context, obj);
				logger.info("preHandle");
			}
		}

		// -------------------------------------------------/
		if (obj instanceof IdeaControl) {
			logger.info("ģ��ʽcontroller");
			IdeaControl ideaControl = (IdeaControl) obj;
			ideaControl.handle(rundata, context);
		} else {
			if (obj.getClass().isAnnotationPresent(Controller.class)) {
				Method method = obj.getClass().getMethod("execute",
						new Class[] { RunData.class, Template.class });
				method.invoke(obj, new Object[] { rundata, context });
			}

			if (obj.getClass().isAnnotationPresent(MultiController.class)) {
				String methodParm = rundata.getParameter("method");
				if (methodParm == null) {
					Method method = obj.getClass().getMethod("execute",
							new Class[] { RunData.class, Template.class });
					method.invoke(obj, new Object[] { rundata, context });
				} else {
					Method method = obj.getClass().getMethod(methodParm,
							new Class[] { RunData.class, Template.class });
					method.invoke(obj, new Object[] { rundata, context });
					logger.info("MultiController is execute");
				}
			}
		}

		// postHandle
		if (handlerInterceptors != null && !handlerInterceptors.isEmpty()) {
			HandlerInterceptor[] interceptors = handlerInterceptors
					.toArray(new HandlerInterceptor[handlerInterceptors.size()]);
			for (int i = 0; i < interceptors.length; i++) {
				HandlerInterceptor interceptor = interceptors[i];
				interceptor.postHandle(rundata, context, obj);
				logger.info("postHandle");
			}
		}

		if (rundata.isNeedTemplate()) {
			render(rundata, context);
		}

	}

	/**
	 * 
	 * 
	 * @param request
	 * @return
	 */
	private HttpServletRequest checkMultipart(HttpServletRequest request) {
		logger.info("request object... " + request);
		logger.info("request getContentType... " + request.getContentType());
		MyMultipartResolver checkPart = new DefaultMyMultipartResolver();
		if (checkPart.isMultipart(request)) {
			logger.info("�ж���mutipart�ϴ�...");
			return checkPart.resolveMultipart(request);
		}
    		return request;
	}

	/**
	 * pull utils��ҳ����
	 * 
	 * @param context
	 */
	private void pullUtil(Template context) {
		if (currentHashMap == null || currentHashMap.size() < 1) {
			return;
		}
		for (Iterator iter = currentHashMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			Object value = (Object) currentHashMap.get(key);
			context.put(key, value);
		}
	}

	private void pullIntoHashMap() throws MyHttpServiceException {
		if (this.currentHashMap == null) {
			currentHashMap = new ConcurrentHashMap<String, Object>();
		}
		PullFactory pull = PullFactory.getInstance();
		Hashtable hashTable = pull.getHashtable();
		for (Iterator iter = hashTable.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			String value = (String) hashTable.get(key);
			try {
				Object pullUtilObj = Class.forName(value).   newInstance();
				currentHashMap.put(key, pullUtilObj);
				logger.info("pullUtilObj chen is " + pullUtilObj);
			} catch (Exception e) {
				throw new MyHttpServiceException(e);
			} 
		}

	}

	private Object getImplementObjectClass(RunData rundata)
			throws NoControllerFindException {
		String controlClass = getControlName(rundata);
		// ��ֹÿ��ʵ�����뷴�����
		Object objectInstance = controlHashMap.get(controlClass);
		if (objectInstance == null) {
			try {
				try {
					objectInstance = Class.forName(controlClass).newInstance();
				} catch (InstantiationException ex) {
					throw new NoControllerFindException(ex);
				} catch (IllegalAccessException ex) {
					throw new NoControllerFindException(ex);
				}
				// ע��
				WebApplicationContext context = getSpringContext();
			
				Class<?> clazz = objectInstance.getClass(); // �õ�ʵ���������
				ClassUtil.getBeanName(clazz, objectInstance, context);
				controlHashMap.put(controlClass, objectInstance);
			} catch (ClassNotFoundException ex) {
				// �����ж�һ�£��������Ĭ�ϵĴ�������ƣ�����Դ�һ��2010-05-08
				String controlException = this.getServletContext()
						.getInitParameter(EXCEPTION_HANDLED_STATUS);
				if ("true".equals(controlException)) {
					try {
						objectInstance = Class.forName(DEFAULT_CONTROL)
								.newInstance();
					} catch (Exception e) {
						throw new NoControllerFindException(ex);
					}
				} else {
					throw new NoControllerFindException(ex);
				}

			}
		}
		return objectInstance;
	}

	private WebApplicationContext getSpringContext() {
		WebApplicationContext context = (WebApplicationContext) this
				.getServletContext()
				.getAttribute(
						WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		return context;
	}

	/**
	 * ȡ��ʵ�ֵ�����
	 * 
	 * @param rundata
	 * @return
	 */
	private String getControlName(RunData rundata) {
		String servletPath = rundata.getHttpServletRequest().getServletPath();
		String proClassName = StringUtil.getRealBean(servletPath);
		String module = this.getServletContext().getInitParameter(
				TEMPLATE_LOCATION_PARAM);
		String controlClass = module + "." + proClassName;
		return controlClass;
	}

	/**
	 * <pre>2011-12-26 wuxiang����һ��Ĭ�ϵ�ӳ�䷽ʽ
	 * 
	 * <pre>
	 * ��Ⱦģ��
	 * 
	 * @param rundata
	 * @param context
	 */
	private void render(RunData rundata, Template context)
			throws NoTemplateFindException {
		String servletPath = rundata.getHttpServletRequest().getServletPath();
		String view = StringUtil.getTemplateView(servletPath);
		// ģ����
		view = StringUtil.downFristChar(view) + ".vm";
		String methodParm = rundata.getParameter("method");
		if (methodParm != null) {
			view = StringUtil.getTemplateView(servletPath) + File.separator
					+ methodParm + ".vm";
		}

		if (rundata.getTemplateView() != null) {
			view = rundata.getTemplateView();
			logger.info("getTemplateView = " + view);
		}
		Map<Object, Object> map = context.getMap();
        org.apache.velocity.Template template = null;
		try {
			template = velocityEngine.getTemplate(view);
        } catch (ResourceNotFoundException e) {
            // ��ͬ�ı����Ҷ����ҵ���ͬ��ȷҳ����
            try {
                // ���vmû���ҵ��������ȥ���Լ���һ��.html�Ĵ���, ��Ҫ��Ϊ��֧��html��̬ҳ�� // FIXME later
                view = StringUtil.getTemplateView(servletPath) + ".html";
                template = velocityEngine.getTemplate(view);
            }  catch (Exception e1) {
                throw new NoTemplateFindException(e);
            }

        } catch (Exception e) {
            throw new NoTemplateFindException(e);
        }

        try {
                VelocityContext context1 = new VelocityContext(map);
                // ����Ҫlayout���� rails on ruby����
                template.merge(context1, rundata.getHttpServletResponse()
                .getWriter());
		} catch (ParseErrorException e) {
			throw new NoTemplateFindException(e);
		} catch (Exception e) {
			throw new NoTemplateFindException(e);
		}
	}

	/**
	 * ��ʼ��
	 */
	private void initVelocityEngine() {
		String path = this.getServletContext().getRealPath("/");
		Properties p = new Properties();
		p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, path
				+ "/WEB-INF/template/");
		p.setProperty("input.encoding", "GBK");
		p.setProperty("output.encoding", "GBK");
		if (velocityEngine == null) {
			velocityEngine = new VelocityEngine();
			try {
				velocityEngine.init(p);
			} catch (Exception e) {
				logger.error("error is ...", e);
			}
		}

	}
}
