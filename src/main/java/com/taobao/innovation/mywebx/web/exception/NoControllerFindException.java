/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web.exception;

/**
 *
 * @author wuxiang
 */
public class NoControllerFindException extends Exception{

    /**
	 * 
	 */
	private static final long serialVersionUID = 7993954797162729140L;

	public NoControllerFindException() {
        super();
    }

    public NoControllerFindException(String message) {
        super(message);
    }

    public NoControllerFindException(String message, Throwable ex) {
        super(message,ex);
    }

   public NoControllerFindException(Throwable ex) {
        super(ex);
    }
}
