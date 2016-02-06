package org.pipseq.rdf.jena.filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase;
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
 * vote
 * For use as a filter function within a SPARQL query.
 * The vote function accepts a policy parameter followed
 * by any number of "vote" or "ballot" parameters.  The vote
 * parameters are compared and a result is returned depending
 * on agreement of the vote with the policy.
 * If the policy is a resource with local name
 * 'Unanimous', then all votes must agree.
 * If the policy is a resource with local name
 * 'Majority', then > 50% of all votes must agree.
 * If the policy is a resource with local name
 * 'SuperMajority', then > 66% of all votes must agree.
 * Returns the agreed-upon vote or false.
 */
public class vote  extends FunctionBase {

	private static final Logger log = LoggerFactory.getLogger(vote.class);
    
    /**
     * Instantiates a new select.
     */
    public vote() { super() ; }

    String policy;
    List<NodeValue> lnv= new ArrayList<NodeValue>();
	@Override
	public NodeValue exec(List<NodeValue> args) {

		int j=0;
		for (NodeValue arg : args){
			if (j++ == 0)
				policy = arg.asNode().getLocalName();
			else {
				lnv.add(arg);
			}
		}
    	if (policy.equals("UnanimousPolicy")){
	    	if (lnv.size() == 0) 
	    		return NodeValue.FALSE;
	    	for (int i=0;i<lnv.size();i++){
	    		if (!lnv.get(0).equals(lnv.get(i))) // all same as first one, first time compares to self
	        		return NodeValue.FALSE;
	    	}
	    	return lnv.get(0);
    	}
    	else if (policy.equals("MajorityPolicy")){
    		Map<NodeValue,Integer>map = new HashMap<NodeValue,Integer>();
	    	if (lnv.size() == 0) 
	    		return NodeValue.FALSE;
	    	for (int i=0;i<lnv.size();i++){
	    		if (!map.containsKey(lnv.get(i)))
	    			map.put(lnv.get(i),new Integer(0));
	    		Integer c = map.get(lnv.get(i))+ 1;
	    		map.put(lnv.get(i),c);
	    	}
	    	for (NodeValue nv : map.keySet()){
	    		int c = map.get(nv);
	    		float f1 = c;
	    		float f2 = lnv.size() / 2;
	    		if (f1 >= f2)
	    			return nv;
	    	}
    	}
    	else if (policy.equals("SuperMajorityPolicy")){
    		Map<NodeValue,Integer>map = new HashMap<NodeValue,Integer>();
	    	if (lnv.size() == 0) 
	    		return NodeValue.FALSE;
	    	for (int i=0;i<lnv.size();i++){
	    		if (!map.containsKey(lnv.get(i)))
	    			map.put(lnv.get(i),new Integer(0));
	    		Integer c = map.get(lnv.get(i))+ 1;
	    		map.put(lnv.get(i),c);
	    	}
	    	for (NodeValue nv : map.keySet()){
	    		int c = map.get(nv);
	    		float f1 = c;
	    		float f2 = ((float)lnv.size()) * 2 / 3;
	    		if (f1 >= f2)
	    			return nv;
	    	}
    	}
    	else throw new RuntimeException("voting policy "+policy+" is not implemented");
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
