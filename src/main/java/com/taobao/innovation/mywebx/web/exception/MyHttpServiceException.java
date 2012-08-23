/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web.exception;

/**
 *
 * @author wuxiang
 */
public class MyHttpServiceException extends Exception{

     /**
	 * 
	 */
	private static final long serialVersionUID = 7002419873856977294L;

	public MyHttpServiceException() {
        super();
    }

    public MyHttpServiceException(String message) {
        super(message);
    }

    public MyHttpServiceException(String message, Throwable ex) {
        super(message,ex);
    }

   public MyHttpServiceException(Throwable ex) {
        super(ex);
    }

}
