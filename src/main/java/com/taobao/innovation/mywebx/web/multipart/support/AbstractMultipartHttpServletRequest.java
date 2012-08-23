/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web.multipart.support;

import com.taobao.innovation.mywebx.web.multipart.MultipartFile;
import com.taobao.innovation.mywebx.web.multipart.MultipartHttpServletRequest;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author Lee Ye
 */
public abstract class AbstractMultipartHttpServletRequest extends HttpServletRequestWrapper
    implements MultipartHttpServletRequest {

    private Map multipartFiles;


	/**
	 * Wrap the given HttpServletRequest in a MultipartHttpServletRequest.
	 * @param request the request to wrap
	 */
	protected AbstractMultipartHttpServletRequest(HttpServletRequest request) {
		super(request);
	}


	public Iterator getFileNames() {
		return getMultipartFiles().keySet().iterator();
	}

	public MultipartFile getFile(String name) {
		return (MultipartFile) getMultipartFiles().get(name);
	}

	public Map getFileMap() {
		return getMultipartFiles();
	}


	/**
	 * Set a Map with parameter names as keys and MultipartFile objects as values.
	 * To be invoked by subclasses on initialization.
	 */
	protected final void setMultipartFiles(Map multipartFiles) {
		this.multipartFiles = Collections.unmodifiableMap(multipartFiles);
	}

	/**
	 * Obtain the MultipartFile Map for retrieval,
	 * lazily initializing it if necessary.
	 * @see #initializeMultipart()
	 */
	protected Map getMultipartFiles() {
		if (this.multipartFiles == null) {
			initializeMultipart();
		}
		return this.multipartFiles;
	}

	/**
	 * Lazily initialize the multipart request, if possible.
	 * Only called if not already eagerly initialized.
	 */
	protected void initializeMultipart() {
		throw new IllegalStateException("Multipart request not initialized");
	}

}
