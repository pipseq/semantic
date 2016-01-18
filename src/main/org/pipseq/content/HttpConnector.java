/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.content;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpConnector.
 */
public class HttpConnector extends ConnectorBase implements IConnector {

	/** The Constant log. */
	static final Logger log = LoggerFactory.getLogger(HttpConnector.class);
	
	/** The method. */
	GetMethod method = null;
	
	/* (non-Javadoc)
	 * @see org.pipseq.content.IConnector#getInputStream(java.lang.String)
	 */
	public InputStream getInputStream(String url) throws Exception {

		String path = makeAbsolutePath(url);
		
		log.info("loading "+path);
		
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		method = new GetMethod(path);

		int statusCode = client.executeMethod(method);

		if (statusCode != HttpStatus.SC_OK) {
//			log.error("GetMethod failed: " + method.getStatusText() + ", code: "+statusCode);
			return null;
		}

		return method.getResponseBodyAsStream();

	}
	
	/* (non-Javadoc)
	 * @see org.pipseq.content.IConnector#close()
	 */
	public void close(){
		if (method != null) {
			method.releaseConnection();
			method = null;
		}
	}
	/*
	 * Tries to determine if the file passed in is relative or absolute
	 * If relative then prepend path, else use as it
	 * (non-Javadoc)
	 * @see org.pipseq.content.ConnectorBase#makeFilename(java.lang.String)
	 */
	@Override
	String makeAbsolutePath(String name) {
		String absName;
		if (isRelativeUrl(name)){
			absName = getPath() 
			+ (getPath().endsWith("/") ? "" : "/") 
			+ name;
		}
		else {
			absName = name;
		}

		return absName;
	}
	
	private boolean isRelativeUrl(String url){
		if (url.toLowerCase().startsWith("http:")) return false;
		if (url.toLowerCase().startsWith("file:")) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public IConnector clone(){
		HttpConnector p = new HttpConnector();
		p.setPath(this.getPath());
		return p;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.content.IConnector#getOutputStream(java.lang.String)
	 */
	public OutputStream getOutputStream(String url) throws Exception {
		log.error("getOutputStream() not implemented");
		return null;
	}
}
