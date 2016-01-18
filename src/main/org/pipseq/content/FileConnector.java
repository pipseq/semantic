/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class FileConnector.
 */
public class FileConnector extends ConnectorBase implements IConnector {

	/** The Constant log. */
	static final Logger log = LoggerFactory.getLogger(FileConnector.class);
	private FileInputStream fis;
	private String filename;

	/* (non-Javadoc)
	 * @see org.pipseq.content.IConnector#getInputStream(java.lang.String)
	 */
	public InputStream getInputStream(String relFilename) throws Exception {
		
		String filename = makeAbsolutePath(relFilename);
		
		this.filename = filename;
		
		File file = new File(filename);
		
		if (!file.exists()){
//			String msg = "" + filename + " does not exist";
//			log.debug(msg);
			return null; //This can go to the next provider 
		}
		
		try {
			fis = new FileInputStream(file);
			if (fis != null)
				log.debug("loading "+filename);
		} catch (Exception e) {
			throw new RuntimeException("Error reading "+filename,e);
		}
		return fis;
	}
	
	/* (non-Javadoc)
	 * @see org.pipseq.content.IConnector#close()
	 */
	public void close(){
		if (fis != null) {
			try {
				fis.close();
			} catch (Exception ex){
				throw new RuntimeException("Error closing "+filename,ex);
			}
			fis = null;
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
		if (name.startsWith("file:")) {
			name = name.substring("file:".length());
		}
		if (isRelativeFilename(name)){
			String path = getPath();
			if (path != null){
				if (!path.endsWith(File.separator)) {
					path += File.separator;
				}
			}
			else path = "";
			absName = path + name;
		}
		else {
			absName = name;
		}
		return absName;
	}
	
	private boolean isRelativeFilename(String fn){
		if (fn.startsWith(File.separator)) return false;
		if (fn.charAt(1) == ':') return false;		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public IConnector clone(){
		FileConnector p = new FileConnector();
		p.setPath(this.getPath());
		return p;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.content.IConnector#getOutputStream(java.lang.String)
	 */
	public OutputStream getOutputStream(String url) throws Exception {
		FileOutputStream fos = new FileOutputStream(url);
		return fos;
	}
}
