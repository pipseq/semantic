/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.id;

// TODO: Auto-generated Javadoc
/**
 * The Class IdentityUid.
 */
public class IdentityUid implements IUid {

	private IIdentity identity;
	/* (non-Javadoc)
	 * @see org.pipseq.db.util.IGuid#getGuid()
	 */
	public String getGuid() {
		return ""+getIdentifier().getId();
	}
	
	/**
	 * Sets the identity.
	 *
	 * @param identity the new identity
	 */
	public void setIdentifier(IIdentity identity) {
		this.identity = identity;
	}
	
	/**
	 * Gets the identity.
	 *
	 * @return the identity
	 */
	public IIdentity getIdentifier() {
		return identity;
	}

}
