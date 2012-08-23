/**
 * 
 */
package com.taobao.innovation.mywebx.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wuxiang Feb 21, 2009
 */
public abstract class MyFrameworkServlet extends HttpServlet{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3753812969513184639L;

	/**
	 * Delegate GET requests to processRequest/doService.
	 * <p>Will also be invoked by HttpServlet's default implementation of <code>doHead</code>,
	 * with a <code>NoBodyResponse</code> that just captures the content length.
	 * @see #doService
	 * @see #doHead
	 */
	protected final void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

		processRequest(request, response);
	}

	/**
	 * Delegate POST requests to {@link #processRequest}.
	 * @see #doService
	 */
	protected final void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

		processRequest(request, response);
	}

	/**
	 * Delegate PUT requests to {@link #processRequest}.
	 * @see #doService
	 */
	protected final void doPut(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

		processRequest(request, response);
	}

	/**
	 * Delegate DELETE requests to {@link #processRequest}.
	 * @see #doService
	 */
	protected final void doDelete(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
		processRequest(request, response);
	}
	
	protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		try {
			doService(request, response);
		} catch (Exception e) {
            throw new ServletException(e);
		}
	}
	
	protected abstract void doService(HttpServletRequest request, HttpServletResponse response)
    throws Exception;

}
