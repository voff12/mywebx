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
	 * 初始化模板引挚 spring的初始化统一由
	 * org.springframework.web.context.ContextLoaderListener来初始化
	 */
	public final void init() throws ServletException {
		synchronized(this) { // 增加同步锁 
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
	 * 得到spring容器 启动模板引挚 包装rundata session机制产生 产生sessioId与得到cookie 注入pull 页面工具
	 * 至context 得到AOP controller拦截器 处理controller
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
	 * 最终处理
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	protected void doDispatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 这是用filter过率器过率
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
			logger.info("模版式controller");
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
			logger.info("判断是mutipart上传...");
			return checkPart.resolveMultipart(request);
		}
		logger.info("不是mutipart上传...");
		return request;
	}

	/**
	 * pull utils至页面中
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
				Object pullUtilObj = Class.forName(value).newInstance();
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
		// 防止每次实例化与反射操作
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
				// 注入
				WebApplicationContext context = getSpringContext();
			
				Class<?> clazz = objectInstance.getClass(); // 得到实现类的名称
				ClassUtil.getBeanName(clazz, objectInstance, context);
				controlHashMap.put(controlClass, objectInstance);
			} catch (ClassNotFoundException ex) {
				// 这里判断一下，如果配有默认的错误处理机制，则可以处一下2010-05-08
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
	 * 取到实现的类名
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
	 * <pre>2011-12-26 wuxiang增加一种默认的映射方式
	 * 
	 * <pre>
	 * 渲染模板
	 * 
	 * @param rundata
	 * @param context
	 */
	private void render(RunData rundata, Template context)
			throws NoTemplateFindException {
		String servletPath = rundata.getHttpServletRequest().getServletPath();
		String view = StringUtil.getTemplateView(servletPath);
		// 模板名
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

		try {
			org.apache.velocity.Template template = velocityEngine
					.getTemplate(view);
			VelocityContext context1 = new VelocityContext(map);
			// 这里要layout布局 rails on ruby样子
			template.merge(context1, rundata.getHttpServletResponse()
					.getWriter());
		} catch (ResourceNotFoundException e) {
			// 不同的抱错，我都能找到不同的确页面上
			throw new NoTemplateFindException(e);
		} catch (ParseErrorException e) {
			throw new NoTemplateFindException(e);
		} catch (Exception e) {
			throw new NoTemplateFindException(e);
		}
	}

	/**
	 * 初始化
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
