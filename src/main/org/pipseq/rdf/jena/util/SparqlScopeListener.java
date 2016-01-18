/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.util;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving jenaInterpreter events.
 * The class that is interested in processing a jenaInterpreter
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addJenaInterpreterListener<code> method. When
 * the jenaInterpreter event occurs, that object's appropriate
 * method is invoked.
 *
 * @see JenaInterpreterEvent
 */
public interface SparqlScopeListener {
	
	/**
	 * Result event.
	 *
	 * @param s the s
	 */
	void resultEvent(String s);
}
