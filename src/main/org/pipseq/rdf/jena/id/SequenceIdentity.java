/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.id;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceIdentity.
 */
public class SequenceIdentity implements IIdentity, IUid {

	public SequenceIdentity(long increment) {
		super();
		this.increment = increment;
	}

	public SequenceIdentity(int increment) {
		super();
		this.increment = increment;
	}

	public SequenceIdentity() {
		super();
	}

	private long increment=1;

	/**
	 * Gets the increment.
	 *
	 * @return the increment
	 */
	public long getIncrement() {
		return increment;
	}

	/**
	 * Sets the increment.
	 *
	 * @param increment the new increment
	 */
	public void setIncrement(long increment) {
		this.increment = increment;
	}

	private synchronized long identifier(){
		return increment++;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.id.IIdentity#getId()
	 */
	@Override
	public String getId() {
		return "" + identifier();
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.id.IUid#getGuid()
	 */
	public String getGuid() {
		return getId();
	}

}
