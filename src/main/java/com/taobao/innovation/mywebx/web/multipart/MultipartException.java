/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web.multipart;

import org.springframework.core.NestedRuntimeException;

/**
 *
 * @author wuxiang
 */
public class MultipartException extends NestedRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 777676509560434141L;

	/**
	 * Constructor for MultipartException.
	 * @param msg the detail message
	 */
	public MultipartException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for MultipartException.
	 * @param msg the detail message
	 * @param cause the root cause from the multipart parsing API in use
	 */
	public MultipartException(String msg, Throwable cause) {
		super(msg, cause);
	}


}
