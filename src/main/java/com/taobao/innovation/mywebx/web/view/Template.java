/**
 * 
 */
package com.taobao.innovation.mywebx.web.view;

import java.util.Map;

/**
 *
 * @author chenxiong Feb 12, 2009
 */
public interface Template {
	
	public Object get(Object key) ;

	public void put(Object key, Object value);
	
	public Map<Object, Object> getMap();

}
