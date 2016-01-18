/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.cfg;


// TODO: Auto-generated Javadoc
/**
 * The Interface WrapperVisitor.
 */
public interface WrapperVisitor {
	
	/**
	 * Visit.
	 *
	 * @param res the res
	 */
	void visit(ModelWrapper res);
	
	/**
	 * Visit.
	 *
	 * @param res the res
	 */
	void visit(BoxWrapper res);
	
	/**
	 * Visit.
	 *
	 * @param res the res
	 */
	void visit(InferModelWrapper res);
	
	/**
	 * Visit.
	 *
	 * @param res the res
	 */
	void visit(NewBoxWrapper res);
	
	/**
	 * Visit.
	 *
	 * @param res the res
	 */
	void visit(OntoBoxWrapper res);

}
