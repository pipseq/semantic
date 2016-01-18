/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.cfg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;

// TODO: Auto-generated Javadoc
/**
 * The Class FileBackedModelWrapper.
 */
public class FileBackedModelWrapper extends ModelWrapper {
	private static final Logger log = LoggerFactory.getLogger(FileBackedModelWrapper.class);
	private String root = System.getProperty("java.io.tmpdir", ".");
	private ModelMaker maker;

	/**
	 * Instantiates a new file backed model resource.
	 */
	public FileBackedModelWrapper() {
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#getModel()
	 */
	protected Model getModel() {
		if (maker == null){
			maker = ModelFactory.createFileModelMaker(getRoot());
			Model model = maker.openModel(getModelName(), false); // false = create it if not exist
			configure(model);
			setModel(model);
		}
		return super.getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#close()
	 */
	public void close(){
		maker.close();
	}

	// File-backed models emits exception for transaction already in progress
	// so track nested transaction state here
	// TODO -- consider: if 3 classes need this code, create new class for TransactionHandlerResource
	private int xactNestCnt;
	
	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#begin()
	 */
	public void begin(){
		if (xactNestCnt++ == 0){
			super.begin();
		}
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#commit()
	 */
	public void commit(){
		if (--xactNestCnt == 0){
			super.commit();
		}
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#abort()
	 */
	public void abort(){
		if (--xactNestCnt == 0){
			super.abort();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.ModelWrapper#accept(org.pipseq.rdf.jena.cfg.WrapperVisitor)
	 */
	@Override
	public void accept(WrapperVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Sets the root.
	 *
	 * @param root the new root
	 */
	public void setRoot(String root) {
		this.root = root;
	}

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public String getRoot() {
		return root;
	}

}
