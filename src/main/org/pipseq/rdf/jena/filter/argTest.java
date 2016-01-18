package org.pipseq.rdf.jena.filter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase;
//@Grapes([
//@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7');
//@Grab('oauth.signpost:signpost-core:1.2.1.2');
//@Grab('oauth.signpost:signpost-commonshttp4:1.2.1.2');
//	@GrabConfig( systemClassLoader=true )
//])

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
/**
 * start up node app.js listener
 * # use this query to test this function
PREFIX f: <java:org.pipseq.rdf.jena.filter.>
select *
 {
    let (?a := "one")
	
    let (?b := "two")
	
let (?c := f:argTest(?a,?b) )
}

see NodeRestTest for more.
 * @author rspates
 *
 */
public class argTest  extends FunctionBase {

	private static final Logger log = LoggerFactory.getLogger(argTest.class);
    
    /**
     * Instantiates a new select.
     */
    public argTest() { super() ; }


	@Override
	public NodeValue exec(List<NodeValue> args) {

//		for (NodeValue arg : args)
//			if (arg.equals(NodeValue.TRUE)) return NodeValue.TRUE;
		try {
			int c = nodeRest(args);
			return NodeValue.makeInteger(c);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return NodeValue.FALSE;
	}

	@Override
	public void checkBuild(String uri, ExprList args) {
	}
	// use app.js
	public int nodeRest(List<NodeValue> args) {
		int c=0;
		for (;c<args.size();c++)
			;
		return c;
	}
	
}
