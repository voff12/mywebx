/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web.multipart;

/**
 *
 * @author Lee Ye
 */
public class MaxUploadSizeExceededException extends MultipartException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2809975088255279752L;
	private final long maxUploadSize;


	/**
	 * Constructor for MaxUploadSizeExceededException.
	 * @param maxUploadSize the maximum upload size allowed
	 */
	public MaxUploadSizeExceededException(long maxUploadSize) {
		this(maxUploadSize, null);
	}

	/**
	 * Constructor for MaxUploadSizeExceededException.
	 * @param maxUploadSize the maximum upload size allowed
	 * @param ex root cause from multipart parsing API in use
	 */
	public MaxUploadSizeExceededException(long maxUploadSize, Throwable ex) {
		super("Maximum upload size of " + maxUploadSize + " bytes exceeded", ex);
		this.maxUploadSize = maxUploadSize;
	}


	/**
	 * Return the maximum upload size allowed.
	 */
	public long getMaxUploadSize() {
		return maxUploadSize;
	}

}


