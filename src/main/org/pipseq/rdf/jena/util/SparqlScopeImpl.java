/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.util;

import java.util.ArrayList;
import java.util.List;

import org.pipseq.rdf.jena.model.Sparql;
import org.pipseq.rdf.jena.model.TripleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;

// TODO: Auto-generated Javadoc
/**
 * The Class SparqlScopeImpl.
 */
public class SparqlScopeImpl implements SparqlScopeImplementor {

	private static final Logger log = LoggerFactory.getLogger(SparqlScopeImpl.class);
	private Model model;
	private boolean useShortNames=true;
	
	/**
	 * Instantiates a new jena interpreter impl.
	 */
	public SparqlScopeImpl() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new jena interpreter impl.
	 *
	 * @param model the model
	 */
	public SparqlScopeImpl(Model model) {
		this.model = model;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.common.JenaInterpreterImplementor#registerListener(org.pipseq.common.JenaInterpreterListener)
	 */
	public void registerListener(SparqlScopeListener jil) {
		// TODO Auto-generated method stub

	}
	
	/** The query cnt. */
	int queryCnt=0;
	
	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.util.SparqlScopeImplementor#submitCommands(java.lang.String)
	 */
	public String submitCommands(String query){
		String result="";
		try {
			List<List<Object>> rl = submitCommands(query, null);
			result = TripleUtil.printQueryResults(rl);			
		} catch (Exception e) {
			log.error(query + "\n" + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Submit commands.
	 *
	 * @param query the query
	 * @param columnHeaders the column headers
	 * @return the list
	 * @throws Exception the exception
	 */
	/* (non-Javadoc)
	 * @see org.pipseq.common.JenaInterpreterImplementor#submitCommands(java.lang.String)
	 */
	public List<List<Object>> submitCommands(String query,List<String> columnHeaders) throws Exception {
		Model m = model;
		if (m == null){
			throw new RuntimeException("No declared model");
		}
		List<List<Object>> rl = new ArrayList<List<Object>>();
		int cnt=0;
		try {
			
			if (isUseShortNames())
				rl = Sparql.queryShortNames(m, query, columnHeaders);
			else {
				rl = Sparql.queryVerbose(m, query, columnHeaders);
			}
			cnt = rl.size();
		} catch (Exception e) {
//			RuntimeException re = new RuntimeException(e.getMessage());
//			re.initCause(e);
//			throw re;
			throw e;
		} finally {
		}
		log.info("rows="+cnt);
		return rl;
	}

	/**
	 * Sets the model.
	 *
	 * @param model the new model
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.util.SparqlScopeImplementor#setUseShortNames(boolean)
	 */
	public void setUseShortNames(boolean useShortNames) {
		this.useShortNames = useShortNames;
	}

	/**
	 * Checks if is use short names.
	 *
	 * @return true, if is use short names
	 */
	public boolean isUseShortNames() {
		return useShortNames;
	}

}
