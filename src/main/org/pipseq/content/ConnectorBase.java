/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.content;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * The Class ConnectorBase.
 */
// TODO: Auto-generated Javadoc
abstract class ConnectorBase {

	private String path;
	
	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Instantiates a new provider base.
	 */
	public ConnectorBase() {
		super();
	}

	/**
	 * Make absolute path.
	 *
	 * @param name the name
	 * @return the string
	 */
	abstract String makeAbsolutePath(String name);
	
	/**
	 * Gets the input stream.
	 *
	 * @param url the url
	 * @return the input stream
	 * @throws Exception the exception
	 */
	abstract InputStream getInputStream(String url) throws Exception;
	
	/**
	 * Close.
	 */
	abstract void close();
	
	/* (non-Javadoc)
	 * @see org.pipseq.content.IConnector#getString(java.lang.String)
	 */
	/**
	 * Gets the string.
	 *
	 * @param url the url
	 * @return the string
	 * @throws Exception the exception
	 */
	public String getString(String url) throws Exception {
		String resource = null;
		try {
	
			// Read the response body.
			// UTF-8 is good for most text and xml
			resource = IOUtils.toString(
					getInputStream(url), "UTF-8");
	
		} finally {
			close();
		}
		return resource;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "["+this.getClass().getName()+", path="+path+"]";
	}
}