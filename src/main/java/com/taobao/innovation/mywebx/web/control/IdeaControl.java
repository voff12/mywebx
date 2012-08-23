/**
 * 
 */
package com.taobao.innovation.mywebx.web.control;

import com.taobao.innovation.mywebx.web.view.RunData;
import com.taobao.innovation.mywebx.web.view.Template;

/**
 *
 * @author chenxiong Feb 12, 2009
 */
public interface IdeaControl {
	
	// URLBroker注入
	
       // 注解式多
	public void handle(RunData rundata, Template context);
         // 得到请求数据
	//处理请求得到数据
	// 放入context
}
