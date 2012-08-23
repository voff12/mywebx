/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web.exception;

/**
 *
 * @author wuxiang
 */
public class NoTemplateFindException extends Exception{

    /**
	 * 
	 */
	private static final long serialVersionUID = 5545652057583708968L;

	public NoTemplateFindException() {
        super();
    }

    public NoTemplateFindException(String message) {
        super(message);
    }

    public NoTemplateFindException(String message, Throwable ex) {
        super(message,ex);
    }

     public NoTemplateFindException(Throwable ex) {
        super(ex);
    }

}
