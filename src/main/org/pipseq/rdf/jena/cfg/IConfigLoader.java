/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.cfg;

import com.hp.hpl.jena.rdf.model.Model;

// TODO: Auto-generated Javadoc
/**
 * The Interface IConfigLoader.
 */
public interface IConfigLoader {

	/**
	 * Load.
	 *
	 * @param src the src
	 * @param key the key
	 * @return the model
	 * @throws Exception the exception
	 */
	Model load(Model src, Object key) throws Exception;
}
