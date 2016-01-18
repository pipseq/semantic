/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.aggregate;

import java.util.ArrayList;
import java.util.List;

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
 * The Class allSameList.
 */
public class allSameList implements Accumulator {
    
    // Execution of a custom aggregate is with accumulators. One accumulator is
    /** The my accumulator factory. */
    // created for the factory for each group in a query execution.
    static AccumulatorFactory myAccumulatorFactory = new AccumulatorFactory() {
        @Override
        public Accumulator createAccumulator(AggCustom agg) { return new allSameList(agg) ; }
    } ;
    
    /** The agg uri. */
    /* Registration */
    public static String aggUri = "java:org.pipseq.rdf.jena.aggregate.allSameList" ;
    
    /**
     * Register.
     */
    public static void register(){
    	AggregateRegistry.register(aggUri, myAccumulatorFactory, NodeConst.nodeFalse);
    	}

    private AggCustom agg ;
    
    /**
     * Instantiates a new all same list.
     *
     * @param agg the agg
     */
    allSameList(AggCustom agg) { this.agg = agg ; }

    /** The lnv. */
    List<NodeValue> lnv = new ArrayList<NodeValue>();
    
    /* (non-Javadoc)
     * @see com.hp.hpl.jena.sparql.expr.aggregate.Accumulator#accumulate(com.hp.hpl.jena.sparql.engine.binding.Binding, com.hp.hpl.jena.sparql.function.FunctionEnv)
     */
    @Override
    public void accumulate(Binding binding, FunctionEnv functionEnv) {
        ExprList exprList = agg.getExprList() ;
        for(Expr expr: exprList) {
            try {
                NodeValue nv = expr.eval(binding, functionEnv) ;
                // Evaluation succeeded.
                lnv.add(nv);
            } catch (ExprEvalException ex) {}
        }
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.sparql.expr.aggregate.Accumulator#getValue()
     */
    @Override
    public NodeValue getValue() {
    	
    	if (lnv.size() < 0) 
    		return NodeValue.FALSE;
    	for (int i=1;i<lnv.size();i++){
    		if (!lnv.get(0).equals(lnv.get(i))) 
        		return NodeValue.FALSE;
    	}
        return NodeValue.TRUE; //lnv.get(0) ;
    }
}
