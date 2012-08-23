/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web.multipart;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Chen Xiong
 */
public interface MyMultipartResolver {

    public boolean isMultipart(HttpServletRequest request) ;

    public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request);

}
