/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.cfg;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class NewBoxWrapper.
 */
public class NewBoxWrapper extends BoxWrapper {

	/**
	 * Instantiates a new rdf new box resource.
	 *
	 * @param abox the abox
	 * @param tbox the tbox
	 */
	public NewBoxWrapper(String name, ModelWrapper abox, ModelWrapper tbox) {
		super(name, abox, tbox);
	}
	
	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.BoxWrapper#getModel()
	 */
	// returns a new union model based on a new abox model each call
	protected Model getModel() {
		abox.setModel(null);	// nothing is read into abox so reinit is quick
		Model union = ModelFactory.createUnion(abox.get(), tbox.get());
		configure(union);
		return union;
	}
	
	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.BoxWrapper#accept(org.pipseq.rdf.jena.cfg.WrapperVisitor)
	 */
	@Override
	public void accept(WrapperVisitor visitor) {
		visitor.visit(this);
		tbox.accept(visitor);
		abox.accept(visitor);
	}
}
