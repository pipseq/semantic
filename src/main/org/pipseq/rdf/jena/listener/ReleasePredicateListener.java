/*
 * Copyright 2009 McKesson Corporation and/or one of its subsidiaries. 
 * All Rights Reserved.
 *
 * Use of this material is governed by a license agreement. This material 
 * contains confidential, proprietary and trade secret information of 
 * McKesson Corporation and/or one of its subsidiaries and is protected 
 * under United States and international copyright and other intellectual
 * property laws. Use, disclosure, reproduction, modification, distribution,
 * or storage in a retrieval system in any form or by any means is prohibited
 * without the prior express written permission of McKesson Corporation.
 */
package org.pipseq.rdf.jena.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving test events. The class that is
 * interested in processing a test event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addTestListener<code> method. When
 * the test event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see TestEvent
 */
public class ReleasePredicateListener implements ModelChangedListener {
	private static final Logger log = LoggerFactory
			.getLogger(ReleasePredicateListener.class);
	private int limit = 30;
	Pattern pattern = Pattern.compile("n/a");

	List<Statement> listStatement = new ArrayList<Statement>();

	public ReleasePredicateListener(String regex, int limit) {
		pattern = Pattern.compile(regex);
		this.limit = limit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#addedStatement(com.hp.
	 * hpl.jena.rdf.model.Statement)
	 */
	public void addedStatement(Statement s) {
		Resource sub = s.getSubject();
		Property prop = s.getPredicate();
		String pname = prop.getLocalName();
		Matcher m = pattern.matcher(pname);
		if (m.matches()) {
			listStatement.add(s);
			if (listStatement.size() > limit) {
				Statement first = listStatement.remove(0);
				Model model = first.getModel();
				model.remove(first);
				log.debug("removing all stmts for "
						+ getName(first.getSubject()) + " "+ getName(first.getPredicate()));
			}
		}
	}
	
	private String getName(Resource sub){
		String name = sub.getLocalName()==null?sub.toString():sub.getLocalName();
		return name;
	}
	private String getName(Statement s){
		return getName(s.getSubject());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#addedStatements(com.hp
	 * .hpl.jena.rdf.model.Statement[])
	 */
	public void addedStatements(Statement[] statements) {
		addedStatements(Arrays.asList(statements));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#addedStatements(java.util
	 * .List)
	 */
	public void addedStatements(List statements) {
		for (Statement stmt : (List<Statement>) statements) {
			addedStatement(stmt);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#addedStatements(com.hp
	 * .hpl.jena.rdf.model.StmtIterator)
	 */
	public void addedStatements(StmtIterator statements) {
		List<Statement> list = new ArrayList<Statement>();
		for (; statements.hasNext();) {
			list.add(statements.next());
		}
		addedStatements(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#addedStatements(com.hp
	 * .hpl.jena.rdf.model.Model)
	 */
	public void addedStatements(Model m) {
		StmtIterator si = m.listStatements();
		addedStatements(si);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#removedStatement(com.hp
	 * .hpl.jena.rdf.model.Statement)
	 */
	public void removedStatement(Statement s) {
		; // not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#removedStatements(com.
	 * hp.hpl.jena.rdf.model.Statement[])
	 */
	public void removedStatements(Statement[] statements) {
		; // not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#removedStatements(java
	 * .util.List)
	 */
	public void removedStatements(List statements) {
		; // not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#removedStatements(com.
	 * hp.hpl.jena.rdf.model.StmtIterator)
	 */
	public void removedStatements(StmtIterator statements) {
		; // not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#removedStatements(com.
	 * hp.hpl.jena.rdf.model.Model)
	 */
	public void removedStatements(Model m) {
		; // not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#notifyEvent(com.hp.hpl
	 * .jena.rdf.model.Model, java.lang.Object)
	 */
	public void notifyEvent(Model arg0, Object arg1) {
		; // not used
	}
}
