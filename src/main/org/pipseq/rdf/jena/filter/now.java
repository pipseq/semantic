package org.pipseq.rdf.jena.filter;

import org.pipseq.common.DateTime;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase0;

/**
 * now
 * For use as a filter function within a SPARQL query.
 * Returns the DateTime now.
 */
public class now extends FunctionBase0
{
	public now() {
		super();
	}

	@Override
	public NodeValue exec() {

		DateTime now =DateTime.now();
		if (now == null) throw new RuntimeException("DateTime now failed");
        String lex = now.toISOString() ;
        return NodeValue.makeDateTime(lex);
	}
}
