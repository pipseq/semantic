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
import java.util.Map;
import java.util.regex.*;

import org.pipseq.rdf.jena.cfg.NSPrefixManager;
import org.pipseq.rdf.jena.util.StatementWriter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.PrintUtil;

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
public class LoggingListener implements ModelChangedListener {
	private static final Logger log = LoggerFactory
			.getLogger(LoggingListener.class);

	// TODO-need setting for long name display
	public static final int NONE = 0; // log nothing
	public static final int RESOURCE = 1; // log only the statement subject
	public static final int STATEMENT = 2; // log all statements as localnames
	public static final int STATEMENTQNAME = 5; // log all statements using
												// QNames
	public static final int STATEMENTURI = 6; // log all statements with FQN
												// URIs
	public static final int RDFTYPEONLY = 3; // log all properties (stmts)
												// except std namespace
	public static final int DATATYPEVALUEONLY = 4; // log statements where
													// property is datatype
													// value
	private int level = 0;
	private String name = "n/a";
	private boolean excludeFromLogging = false;

	public boolean isExcludeFromLogging() {
		return excludeFromLogging;
	}

	public void setExcludeFromLogging(boolean excludeFromLogging) {
		this.excludeFromLogging = excludeFromLogging;
	}

	public LoggingListener(String name, int level) {
		this.level = level;
		this.name = name;
		log.debug("" + name + ", log level=" + level);
	}

	public void setLoggingLevel(int level) {
		this.level = level;
		log.debug("" + name + ", log level=" + level);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.rdf.model.ModelChangedListener#addedStatement(com.hp.
	 * hpl.jena.rdf.model.Statement)
	 */
	public void addedStatement(Statement s) {
		if (!this.isExcludeFromLogging())
			try {
				String entry = "";

				switch (level) {
				case RESOURCE:
					if (s.getSubject().isAnon()) {
						entry = String.format("[%s] %s", name, s.getSubject());
					} else {
						entry = String.format("[%s] %s", name, s.getSubject()
								.getLocalName());
					}
					log.debug(entry);
					break;

				case STATEMENT:
					if (s.getObject().isURIResource()) {
						entry = String.format("[%s] %s %s %s", name, s
								.getSubject().isAnon() ? s.getSubject() : s
								.getSubject().getLocalName(), s.getPredicate()
								.getLocalName(), s.getObject().asResource()
								.getLocalName());
					} else {
						entry = String.format("[%s] %s %s %s", name, s
								.getSubject().isAnon() ? s.getSubject() : s
								.getSubject().getLocalName(), s.getPredicate()
								.getLocalName(), s.getObject());
					}
					log.debug(entry);
					break;

				case STATEMENTQNAME:
					String subj = "";
					if (s.getSubject().isAnon()) {
						String spre = "_:b";
						String sname = s.getSubject().toString().replaceAll(":", "");
						subj = spre + sname;
					} else {
						String spre = s.getModel().getNsURIPrefix(
								s.getSubject().getNameSpace());
						String sname = s.getSubject().getLocalName();
						subj = spre + ":" + sname;
					}
					String ppre = s.getModel().getNsURIPrefix(
							s.getPredicate().getNameSpace());
					String pname = s.getPredicate().getLocalName();
					String os = "";
					if (s.getObject().isLiteral()){
						String uri = StatementWriter.writeNode(s.getObject());
						// "89.2"^^<http://www.w3.org/2001/XMLSchema#float>
						String[] sa = uri.split("[\\^<>#]+");
						os = String.format("%s^^xsd:%s", sa[0], sa[2]);
					} else if (s.getObject().isAnon()){
						os = StatementWriter.writeNode(s.getObject());
					} else if (s.getObject().isResource()){
						String spre = s.getModel().getNsURIPrefix(
								s.getObject().asResource().getNameSpace());
						String sname = s.getObject().asResource().getLocalName();
						os = spre + ":" + sname;
					}
					entry = String.format("[%s] %s  %s:%s %s", name, subj,
					ppre, pname, os);

					log.debug(entry);

					break;

				case STATEMENTURI:
					if (s.getSubject().isAnon()) {
						entry = String.format("[%s] _:b%s <%s> %s", name,
								s.getSubject().toString().replaceAll(":", ""), s.getPredicate(), 
								StatementWriter.writeNode(s.getObject()));
					} else {
						entry = String.format("[%s] <%s> <%s> %s", name,
								s.getSubject(), s.getPredicate(), 
								StatementWriter.writeNode(s.getObject()));
						
					}
					log.debug(entry);
					break;

				case RDFTYPEONLY:
					if (!"type".equals(s.getPredicate().getLocalName()))
						break;
					entry = String.format("[%s] %s %s %s", name, s.getSubject()
							.isAnon() ? s.getSubject() : s.getSubject()
							.getLocalName(), s.getPredicate().getLocalName(), s
							.getObject().asResource().getLocalName());
					log.debug(entry);
					break;

				case DATATYPEVALUEONLY:
					if (!s.getObject().isLiteral())
						break;
					if (s.getSubject().isAnon()) {
						entry = String.format("[%s] %s %s %s", name,
								s.getSubject(),
								s.getPredicate().getLocalName(), s.getObject());
					} else {
						entry = String.format("[%s] %s %s %s", name, s
								.getSubject().getLocalName(), s.getPredicate()
								.getLocalName(), s.getObject());
					}
					log.debug(entry);
					break;

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
