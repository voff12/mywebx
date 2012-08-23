/**
 * 
 */
package com.taobao.innovation.mywebx.web.control;

import com.taobao.innovation.mywebx.web.view.RunData;
import com.taobao.innovation.mywebx.web.view.Template;

/**
 *
 * @author chenxiong Feb 21, 2009
 */
public abstract class MyControl implements IdeaControl{
	
	public void handle(RunData rundata, Template context){
		execute(rundata, context);
	}
	
	protected abstract void execute(RunData rundata, Template context);

}
