/**
 * 
 */
package com.taobao.innovation.mywebx.web.pull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author chenxiong
 *
 * Feb 28, 2009--PullFactory.java
 */
public class PullFactory {
	private static final Log logger = LogFactory.getLog("myframework");

	private static PullFactory instance = null;
	private static Hashtable<Object, Object> hashtable = null;

	public PullFactory() {
		read();
	}
	
	public static PullFactory getInstance(){
		if (instance == null) {
			instance = new PullFactory();
		}
		return instance;
	}

	public void read() {

		Properties p = new Properties();
		try {
			InputStream in = this.getClass().getClassLoader()
					.getResourceAsStream("pullUtil.properties");
			logger.info("╪стьнд╪Ч");
			if (in != null) {
				p.load(in);
			} else {
				p.put("dateUtil", "com.voff.myspring.framework.web.util.DateUtil");
				p.put("stringUtil", "com.voff.myspring.framework.web.util.StringUtil");
			}
			this.hashtable = p;
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

	}

	public Hashtable<Object, Object> getHashtable() {
		return hashtable;
	}

	public  void setHashtable(Hashtable<Object, Object> hashtable) {
		PullFactory.hashtable = hashtable;
	}
	
	public static void main(String[] args){
		PullFactory pull = PullFactory.getInstance();
		Hashtable<Object, Object> hashTable = pull.getHashtable();
		logger.info("" + hashTable);
		for (Iterator<Object> iter = hashTable.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			String value= (String) hashTable.get(key);
			logger.info("key is" + key);
			logger.info("value is" + value);
			try {
				final Object pullUtilObj = Class.forName(value).newInstance();
				logger.info("pullUtilObj chen is " + pullUtilObj);
			} catch (InstantiationException e) {
				logger.error("error", e);
			} catch (IllegalAccessException e) {
				logger.error("error", e);
			} catch (ClassNotFoundException e) {
				logger.error("error", e);
			}
		}
	}

}
