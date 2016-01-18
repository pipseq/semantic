/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.cfg;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class OntoBoxWrapper.
 */
public class OntoBoxWrapper extends BoxWrapper {
	private static final Logger log = LoggerFactory.getLogger(OntoBoxWrapper.class);
	private OntModelSpec ontModelSpec;
	private String ontModelSpecName;

//	/**
//	 * Instantiates a new rdf onto box resource.
//	 *
//	 * @param abox the abox
//	 * @param tbox the tbox
//	 */
//	public OntoBoxWrapper(ModelWrapper abox, ModelWrapper tbox) {
//		this(abox,tbox,null);
//	}
//
//	/**
//	 * Instantiates a new rdf onto box resource.
//	 *
//	 * @param abox the abox
//	 * @param tbox the tbox
//	 * @param ontModelSpecName the ont model spec name
//	 */
//	public OntoBoxWrapper(ModelWrapper abox, ModelWrapper tbox, String ontModelSpecName) {
//		this.abox = abox;
//		this.tbox = tbox;
//		setOntModelSpec( ontModelSpecName);
//	}
//
	/**
	 * Instantiates a new rdf onto box resource.
	 *
	 * @param abox the abox
	 * @param tbox the tbox
	 */
	public OntoBoxWrapper(String name,ModelWrapper abox, ModelWrapper tbox) {
		this(name, abox,tbox,null);
	}

	/**
	 * Instantiates a new rdf onto box resource.
	 *
	 * @param abox the abox
	 * @param tbox the tbox
	 * @param ontModelSpecName the ont model spec name
	 */
	public OntoBoxWrapper(String name, ModelWrapper abox, ModelWrapper tbox, String ontModelSpecName) {
		super(name);
		this.abox = abox;
		this.tbox = tbox;
		setOntModelSpec( ontModelSpecName);
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.BoxWrapper#load()
	 */
	protected void load() {
		super.load();
		Model combModel = getModel();
		setModel( ModelFactory.createOntologyModel(ontModelSpec,
				combModel));
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#get()
	 */
	public OntModel get() {
		return (OntModel) getModel();
	}

	/**
	 * Sets the ont model spec.
	 *
	 * @param ontModelSpecName the new ont model spec
	 */
	public void setOntModelSpec(String ontModelSpecName) {
		this.ontModelSpecName = ontModelSpecName;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#init()
	 */
	public void init(){
		super.init();
		
		if (ontModelSpecName != null){
			try {
				Field field = OntModelSpec.class.getDeclaredField(ontModelSpecName);
				if (field != null && field.getType().equals(OntModelSpec.class)) {
					this.ontModelSpec = (OntModelSpec) field.get(OntModelSpec.class);
					this.ontModelSpec.getDocumentManager().setProcessImports(false);
				}
			} catch (Exception e) {
				throw new RuntimeException(ontModelSpecName , e);
			}
		}
		else {
			ontModelSpec = OntModelSpec.OWL_MEM;
		}
		
		load();
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
