/**
 * 
 */
package com.taobao.innovation.mywebx.web.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.context.ApplicationContext;

/**
 * @author chenxiong
 *
 * 2009-2-28--ClassUtil.java
 */
public class ClassUtil {
	
	/**
	 * 找到这个类
	 * 
	 * @param clazz
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getClass(String clazz) throws ClassNotFoundException {
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	
    	if (classLoader != null) {
    		try {
    			return Class.forName(clazz, true, classLoader);
    		} catch (Exception e) {
    			throw new ClassNotFoundException();
    		}
    	}  
    	
    	return Class.forName(clazz);
    }
    
    /**
     * 实例化
     * 
     * @param clazz
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object getNewInstance(String clazz)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException {
    	
    	return getClass(clazz).newInstance();
	}
    
    /**
	 * 
	 * @param clazz
	 * @param obj 是clazz的实例
	 * @return
	 */
	public static Object getBeanName(Class<?> clazz, Object obj, ApplicationContext context){
		 
		PropertyDescriptor[] p = PropertyUtils.getPropertyDescriptors(clazz);
		for (int i = 0; i < p.length; i++) {
			Method method = PropertyUtils.getWriteMethod(p[i]);
			if (method != null && method.getName().length() > 3
					&& method.getName().startsWith("set")) {
				String name = method.getName().substring(3);
				name = StringUtil.downFristChar(name);
				Object springObj = context.getBean(name);
				try {
					method.invoke(obj, springObj);
				} catch (IllegalArgumentException e) {
					System.out.println(e.getStackTrace());
				} catch (IllegalAccessException e) {
					System.out.println(e.getStackTrace());
				} catch (InvocationTargetException e) {
					System.out.println(e.getStackTrace());
				}
			}
		}
		return obj;
	  
	}
	

}
