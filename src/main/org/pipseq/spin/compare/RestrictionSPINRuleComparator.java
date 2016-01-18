/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.spin.compare;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pipseq.rdf.jena.model.Sparql;
import org.topbraid.spin.inference.SPINRuleComparator;
import org.topbraid.spin.util.CommandWrapper;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

// TODO: Auto-generated Javadoc
/**
 * The Class RestrictionSPINRuleComparator.
 */

public class RestrictionSPINRuleComparator  implements SPINRuleComparator {
	
	private Map<String,String> map = new HashMap<String,String>();
	private static String query = ""
			+ "prefix pip:     <http://pipseq.org#> \n"
			+ "prefix owl:     <http://www.w3.org/2002/07/owl#> \n"
			+ ""
			+ "SELECT *{"
			+ "?cls owl:equivalentClass"
			+ "[owl:hasValue ?rc ;"
			+ "owl:onProperty pip:hasRuleCategory ]"
			+ "}"
			+ "";
	
	/**
	 * Instantiates a new restriction spin rule comparator.
	 *
	 * @param model the model
	 */
	public RestrictionSPINRuleComparator(Model model) {
		// Pre-build properties list
		//org.pipseq.spin.BaseTstr.scope(model);
		
		List<List<Object>> list = Sparql.query(model,query);
		for (List<Object> lo : list) {
			String key = (String)lo.get(0);
			int n = key.lastIndexOf('#')+1;
			key = key.substring(n);
			map.put(key, (String)lo.get(1));
		}
	}
	

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(CommandWrapper w1, CommandWrapper w2) {
		
		Statement s1 = w1.getStatement();
		Resource r1 = s1.getSubject();
		String name1 = r1.getLocalName();
		String cmp1 = map.get(name1);
		Statement s2 = w2.getStatement();
		Resource r2 = s2.getSubject();
		String name2 = r2.getLocalName();
		String cmp2 = map.get(name2);
		return cmp1.compareTo(cmp2);
	}
}
