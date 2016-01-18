/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.cfg;

import org.pipseq.rdf.jena.listener.LoggingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class BoxWrapper.
 */
public class BoxWrapper extends ModelWrapper {
	private static final Logger log = LoggerFactory.getLogger(BoxWrapper.class);
	
	/** The abox. */
	protected ModelWrapper abox;
	
	/**
	 * Gets the abox.
	 *
	 * @return the abox
	 */
	public ModelWrapper getAbox() {
		return abox;
	}

	/**
	 * Sets the abox.
	 *
	 * @param abox the new abox
	 */
	public void setAbox(ModelWrapper abox) {
		this.abox = abox;
		//this.abox.read();
	}

	/** The tbox. */
	protected ModelWrapper tbox;
	
	/**
	 * Gets the tbox.
	 *
	 * @return the tbox
	 */
	public ModelWrapper getTbox() {
		return tbox;
	}

	/**
	 * Sets the tbox.
	 *
	 * @param tbox the new tbox
	 */
	public void setTbox(ModelWrapper tbox) {
		this.tbox = tbox;
		//this.tbox.read();
	}

	private Model union;

	/**
	 * Instantiates a new rdf box resource.
	 */
	public BoxWrapper(String name) {
		super(name);
	}

	/**
	 * Instantiates a new rdf box resource.
	 *
	 * @param abox the abox
	 * @param tbox the tbox
	 */
	public BoxWrapper(String name, ModelWrapper abox, ModelWrapper tbox) {
		super(name);
		this.abox = abox;
		this.tbox = tbox;
		load();
	}

	public void init(){
		tbox.init();
		abox.init();
	}
	/**
	 * Load.
	 */
	protected void load() {
		tbox.read();
		abox.read();
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#getModel()
	 */
	protected Model getModel() {
		if (union == null){
			union = ModelFactory.createUnion(abox.get(), tbox.get());
			configure(union);
		}
			// no abox logging policy
		read(union);
		abox.getLoggingListener().setExcludeFromLogging(true);
		tbox.getLoggingListener().setExcludeFromLogging(true);	// probably doesn't matter here
		return union;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#setModel(com.hp.hpl.jena.rdf.model.Model)
	 */
	public void setModel(Model model) {
		this.union = model;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#read()
	 */
	public void read() {
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#write()
	 */
	public void write() {
		abox.write();
	}
	
	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#accept(org.pipseq.rdf.jena.cfg.WrapperVisitor)
	 */
	@Override
	public void accept(WrapperVisitor visitor) {
		visitor.visit(this);
		tbox.accept(visitor);
		abox.accept(visitor);
	}

}
