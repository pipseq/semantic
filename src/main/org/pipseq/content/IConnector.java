/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.content;

import java.io.InputStream;
import java.io.OutputStream;

// TODO: Auto-generated Javadoc
/**
 * The Interface IConnector.
 */
public interface IConnector {

	/**
	 * Gets the input stream.
	 *
	 * @param url the url
	 * @return the input stream
	 * @throws Exception the exception
	 */
	public abstract InputStream getInputStream(String url) throws Exception;
	
	/**
	 * Gets the output stream.
	 *
	 * @param url the url
	 * @return the output stream
	 * @throws Exception the exception
	 */
	public abstract OutputStream getOutputStream(String url) throws Exception;

	/**
	 * Gets the string.
	 *
	 * @param url the url
	 * @return the string
	 * @throws Exception the exception
	 */
	public abstract String getString(String url) throws Exception;

	/**
	 * Close.
	 */
	public abstract void close();

	/**
	 * Clone.
	 *
	 * @return the i provider
	 */
	public IConnector clone();
}