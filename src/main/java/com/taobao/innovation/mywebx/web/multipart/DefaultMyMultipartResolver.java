/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taobao.innovation.mywebx.web.multipart;

import com.taobao.innovation.mywebx.web.multipart.commons.CommonsFileUploadSupport;
import com.taobao.innovation.mywebx.web.multipart.support.DefaultMultipartHttpServletRequest;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


/**
 *
 * @author Chen Xiong
 */
public class DefaultMyMultipartResolver extends CommonsFileUploadSupport implements MyMultipartResolver{

    public boolean isMultipart(HttpServletRequest request) {
         if (request == null) {
             return false;
         }
         return ServletFileUpload.isMultipartContent(request);

    }

    public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) {
        return new DefaultMultipartHttpServletRequest(request) {
				protected void initializeMultipart() {
					MultipartParsingResult parsingResult = parseRequest(request);
					setMultipartFiles(parsingResult.getMultipartFiles());
					setMultipartParameters(parsingResult.getMultipartParameters());
				}
			};
    }

    protected MultipartParsingResult parseRequest(HttpServletRequest request) {
		String encoding = determineEncoding(request);
		FileUpload fileUpload = prepareFileUpload(encoding);
		try {
			List fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
			return parseFileItems(fileItems, encoding);
		}
		catch (FileUploadBase.SizeLimitExceededException ex) {
			throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
		}
		catch (FileUploadException ex) {
			throw new MultipartException("Could not parse multipart servlet request", ex);
		}
	}

   protected String determineEncoding(HttpServletRequest request) {
		String encoding = request.getCharacterEncoding();
		if (encoding == null) {
			encoding = getDefaultEncoding();
		}
		return encoding;
	}

   	
    @Override
    protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
        return new ServletFileUpload(fileItemFactory);
    }


}
