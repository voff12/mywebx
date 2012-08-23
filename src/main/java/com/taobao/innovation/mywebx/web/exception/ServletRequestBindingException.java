/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web.exception;

/**
 *
 * @author Lee Ye
 */
public class ServletRequestBindingException extends Exception{

    /**
	 * 
	 */
	private static final long serialVersionUID = 96603860048855761L;

	public ServletRequestBindingException() {
        super();
    }

    public ServletRequestBindingException(String message) {
        super(message);
    }
    
   public ServletRequestBindingException(String message, Throwable cause) {
       super(message,cause);
   }

}
