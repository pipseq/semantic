package org.pipseq.rdf.jena.filter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase;

// TODO: Auto-generated Javadoc
/**
 * Example filter function that returns an indicative type string.
 * <ul>
 * <li>"Number", if it's a number of some kind</li>
 * <li>"String", if it's string</li>
 * <li>"DateTime", if it's a date time</li>
 * <li>"unknown" otherwise</li>
 * </ul>
 * 
 * Usage:
 * <pre>
 * PREFIX ext: <java:arq.examples.ext.>
 * </pre>
 * <pre>
 * FILTER ext:classify(3+?x)
 * <pre>
 * 
 * @author Andy Seaborne
 */ 

public class or extends FunctionBase
{
	private static final Logger log = LoggerFactory.getLogger(or.class);
    
    /**
     * Instantiates a new select.
     */
    public or() { super() ; }


	@Override
	public NodeValue exec(List<NodeValue> args) {

		for (NodeValue arg : args)
			if (arg.equals(NodeValue.TRUE)) return NodeValue.TRUE;
		return NodeValue.FALSE;
	}

	@Override
	public void checkBuild(String uri, ExprList args) {
		
	}
	
}
