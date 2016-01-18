/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.cfg;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;

// TODO: Auto-generated Javadoc
/**
 * The Class InferModelWrapper.
 */
public class InferModelWrapper extends ModelWrapper {
	private static final Logger log = LoggerFactory.getLogger(InferModelWrapper.class);
	private Reasoner reasoner = ReasonerRegistry.getRDFSSimpleReasoner();
	private String reasonerName;

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#read()
	 */
	public void read() {
		super.read();
		Model dfltModel = getModel();
		Model infModel = ModelFactory.createInfModel(reasoner, dfltModel);
		
		// this code avoids the runtime inference model and deadlock issues
		// with poorly coded LPTopGoalIterator
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefixes(infModel.getNsPrefixMap());
		model.add(infModel);
		setModel(model);
	}

//	public InfModel get() {
//		return (InfModel) getModel();
//	}

	/**
 * Sets the reasoner name.
 *
 * @param reasonerName the new reasoner name
 */
	public void setReasonerName(String reasonerName) {
		this.reasonerName = reasonerName;
	}

	/**
	 * Gets the reasoner.
	 *
	 * @return the reasoner
	 */
	public Reasoner getReasoner() {
		return reasoner;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#init()
	 */
	public void init(){
		super.init();
		if (reasonerName != null){
			try {
				Method method = ReasonerRegistry.class.getDeclaredMethod(reasonerName,new Class[]{});
				if (method != null && method.getReturnType().equals(Reasoner.class)) {
					this.reasoner = (Reasoner) method.invoke(null, new Object[]{});
				}
			} catch (Exception e) {
				throw new RuntimeException(reasonerName ,e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#accept(org.pipseq.rdf.jena.cfg.WrapperVisitor)
	 */
	@Override
	public void accept(WrapperVisitor visitor) {
		visitor.visit(this);
	}
}
