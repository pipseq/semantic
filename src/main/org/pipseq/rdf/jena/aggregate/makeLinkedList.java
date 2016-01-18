/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.aggregate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.aggregate.Accumulator;
import com.hp.hpl.jena.sparql.expr.aggregate.AccumulatorFactory;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCustom;
import com.hp.hpl.jena.sparql.expr.aggregate.AggregateRegistry;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.hp.hpl.jena.sparql.graph.NodeConst;

// TODO: Auto-generated Javadoc
/**
 * The Class makeLinkedList.
 */
public class makeLinkedList implements Accumulator {
    
    // Execution of a custom aggregate is with accumulators. One accumulator is
    /** The my accumulator factory. */
    // created for the factory for each group in a query execution.
    static AccumulatorFactory myAccumulatorFactory = new AccumulatorFactory() {
        @Override
        public Accumulator createAccumulator(AggCustom agg) { 
        	return new makeLinkedList(agg) ; 
        	}
    } ;
    
    /** The agg uri. */
    /* Registration */
    public static String aggUri = "java:org.pipseq.rdf.jena.aggregate.makeLinkedList" ;
    
    /**
     * Register.
     */
    public static void register(){
    	AggregateRegistry.register(aggUri, myAccumulatorFactory, NodeConst.nodeFalse);
     }

    private AggCustom agg ;
    
    /**
     * Instantiates a new make linked list.
     *
     * @param agg the agg
     */
    makeLinkedList(AggCustom agg) { this.agg = agg ; }

    /** The model. */
    Model model;
	private static Property first;
	private static Property rest;
	private static Resource nil;
	
	/** The map. */
	Map<RDFNode, Object> map= new LinkedHashMap<RDFNode, Object>();
    
    /* (non-Javadoc)
     * @see com.hp.hpl.jena.sparql.expr.aggregate.Accumulator#accumulate(com.hp.hpl.jena.sparql.engine.binding.Binding, com.hp.hpl.jena.sparql.function.FunctionEnv)
     */
    @Override
    public void accumulate(Binding binding, FunctionEnv functionEnv) {
		if (model == null)
			model = ModelFactory
				.createModelForGraph(functionEnv.getActiveGraph());
        ExprList exprList = agg.getExprList() ;
        for(Expr expr: exprList) {
            try {
                NodeValue nv = expr.eval(binding, functionEnv) ;
                // Evaluation succeeded.

    			Resource res = model.createResource();
    			map.put(res,  nv);
            } catch (ExprEvalException ex) {
            	ex.printStackTrace();
            }
        }
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.sparql.expr.aggregate.Accumulator#getValue()
     */
    @Override
    public NodeValue getValue() {
		if (first == null)
			first = model.createProperty(
					"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "first");
		if (rest == null)
			rest = model.createProperty(
					"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rest");
		if (nil == null)
			nil = model
					.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil");
		
        Resource head = null ;
        Resource tail = null ;
		for (RDFNode node : map.keySet()){
			if (head == null)
				head = node.asResource();
			
			if (tail != null) {
				model.remove(tail, rest, nil);
				Resource current = node.asResource();
				model.add(tail, rest, current);
				model.add(current, first, map.get(current).toString());
				model.add(current, rest, nil);
    			tail = node.asResource();
			} else {
				model.add(head, first, map.get(head).toString());
				model.add(head, rest, nil);
    			tail = node.asResource();
			}
		}
        return NodeValue.makeNode(head.asNode()) ;
    }
//    private static Property first;
//    private static Property rest;
    /**
     * Recursive method to traverse linked list, gathering all
     * "first" resources, then retrieving the "rest" over which to recurse.
     */
    public static void traverseCollection(Model model, List<RDFNode> list, Resource res){

    	if (first == null) 
    		first = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#","first");
    	if (rest == null)
    		rest = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#","rest");
    	
        for (StmtIterator si2 = model.listStatements(res, first, (RDFNode)null);si2.hasNext();){
        	Statement st2 = si2.nextStatement();
        	RDFNode u2 = st2.getObject();
        	list.add(u2);
            for (StmtIterator si3 = model.listStatements(res, rest, (RDFNode)null);si3.hasNext();){
            	Statement st3 = si3.nextStatement();
            	Resource u3 = st3.getResource();
            	traverseCollection(model, list,u3);
            }
        }
    }
}
