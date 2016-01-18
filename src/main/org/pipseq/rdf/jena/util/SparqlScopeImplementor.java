/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.util;

// TODO: Auto-generated Javadoc
/**
 * The Interface SparqlScopeImplementor.
 */
public interface SparqlScopeImplementor {
	
	/**
	 * Submit commands.
	 *
	 * @param lines the lines
	 * @return the string
	 */
	public String submitCommands(String lines);
	
	/**
	 * Register listener.
	 *
	 * @param nil the nil
	 */
	public void registerListener(SparqlScopeListener nil);
	
	/**
	 * Sets the use short names.
	 *
	 * @param useShortNames the new use short names
	 */
	void setUseShortNames(boolean useShortNames);
}
