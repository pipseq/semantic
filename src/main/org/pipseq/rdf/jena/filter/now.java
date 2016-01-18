package org.pipseq.rdf.jena.filter;

import org.pipseq.common.DateTime;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase0;

public class now extends FunctionBase0
{
	public now() {
		super();
	}

	@Override
	public NodeValue exec() {

        String lex = DateTime.now().toISOString() ;
        return NodeValue.makeDateTime(lex);
	}
}
