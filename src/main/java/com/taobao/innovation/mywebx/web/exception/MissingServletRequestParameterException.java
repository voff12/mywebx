/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web.exception;

/**
 *
 * @author wuxiang
 */
public class MissingServletRequestParameterException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1159465676880308468L;
	

	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    private String type;

    public MissingServletRequestParameterException() {
        super();
    }

    public MissingServletRequestParameterException(String name, String type) {
        super(name);
        this.type = type;
    }

}
