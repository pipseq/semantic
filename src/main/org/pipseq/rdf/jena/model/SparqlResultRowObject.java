/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

// TODO: Auto-generated Javadoc
/**
 * The Class SparqlResultRowObject.
 */
public class SparqlResultRowObject implements RowMapper {

	/** The model. */
	Model model;
	
	/** The use literals. */
	boolean useLiterals;

	/**
	 * Checks if is use literals.
	 *
	 * @return true, if is use literals
	 */
	public boolean isUseLiterals() {
		return useLiterals;
	}

	/** The use short names. */
	boolean useShortNames;

	/**
	 * Checks if is use short names.
	 *
	 * @return true, if is use short names
	 */
	public boolean isUseShortNames() {
		return useShortNames;
	}

	/**
	 * Instantiates a new sparql result row object.
	 *
	 * @param model the model
	 * @param useLiterals the use literals
	 * @param useShortNames the use short names
	 */
	public SparqlResultRowObject(Model model, boolean useLiterals,
			boolean useShortNames) {
		this.model = model;
		this.useLiterals = useLiterals;
		this.useShortNames = useShortNames;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.model.RowMapper#mapRow(com.hp.hpl.jena.query.ResultSet, int)
	 */
	public List<Object> mapRow(ResultSet rs, int rowNum) {

		// handle each solution
		QuerySolution rb = rs.nextSolution();

		List<Object> list = new ArrayList<Object>();

		int i = 0;
		for (String var : rs.getResultVars()) {
			RDFNode x = rb.get(var);
			if (x == null) {
				list.add("");
			} else if (x.isLiteral() && isUseLiterals()) {
				list.add(x.asNode().toString(true));
			} else if (x.isLiteral()) {
				Literal lit = (Literal) x;
				Object o;
				try {
					o = lit.getValue();
				} catch (DatatypeFormatException e) {
					o = lit.getString();
				}
				list.add(o);
			} else if (isUseShortNames() && x.isURIResource()) {
				list.add(TripleData.getShortName(model, x));
			} else {
				list.add(x.toString());
			}
			i++;
		}
		return list;
	}
}
