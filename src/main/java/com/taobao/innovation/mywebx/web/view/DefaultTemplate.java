/**
 * 
 */
package com.taobao.innovation.mywebx.web.view;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author chenxiong Feb 21, 2009
 */
public class DefaultTemplate implements Template{
	
	private Map<Object, Object> hashMap = new HashMap<Object, Object>();

	public Object get(Object key) {
		return hashMap.get(key);
	}

	public void put(Object key, Object value) {
		hashMap.put(key, value);
	}

	public Map<Object, Object> getHashMap() {
		return hashMap;
	}

	public void setHashMap(Map<Object, Object> hashMap) {
		this.hashMap = hashMap;
	}

	public Map<Object, Object> getMap() {
		return hashMap;
	}

 

}
