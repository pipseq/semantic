/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.aggregate;

// TODO: Auto-generated Javadoc
/**
 * The Class AggMethodRegistry.
 */
public class AggMethodRegistry {
	
	/**
	 * Inits the.
	 */
	public static void init(){
		makeLinkedList.register();
		allSameList.register();
		restClientMap.register();
	}
}
