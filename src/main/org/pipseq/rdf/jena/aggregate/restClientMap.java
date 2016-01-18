/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pipseq.rdf.jena.model.Triple;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
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
 * The Class restClientMap.
 */
public class restClientMap implements Accumulator {
    
    // Execution of a custom aggregate is with accumulators. One accumulator is
    /** The my accumulator factory. */
    // created for the factory for each group in a query execution.
    static AccumulatorFactory myAccumulatorFactory = new AccumulatorFactory() {
        @Override
        public Accumulator createAccumulator(AggCustom agg) { return new restClientMap(agg) ; }
    } ;
    
    /** The agg uri. */
    /* Registration */
    public static String aggUri = "java:org.pipseq.rdf.jena.aggregate.restClientMap" ;
//    static {
//		AggMethodRegistry.init();
//    }
    /**
     * Register.
     */
    public static void register(){
    	AggregateRegistry.register(aggUri, myAccumulatorFactory, NodeConst.nodeFalse);
    	}

    private AggCustom agg ;
    /** The model. */
    private Model model;
    private NodeValue blanknode;
    private CallRest callRest = new CallRest();
    /**
     * Instantiates a new all same list.
     *
     * @param agg the agg
     */
    restClientMap(AggCustom agg) { this.agg = agg ; }

    /** The lnv. */
    List<NodeValue> lnv = new ArrayList<NodeValue>();
    
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
                blanknode = nv;//.getNode().getBlankNodeLabel();
            } catch (ExprEvalException ex) {}
        }
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.sparql.expr.aggregate.Accumulator#getValue()
     */
    @SuppressWarnings("unchecked")
	@Override
    public NodeValue getValue() {
    	
    	Map<String,Object> map = null;
		try {
			
			// unable to use blanknode directly, so do a lookup
			Object bn0 = null;
			List<Resource> bnl = Triple.getResList(model, "pip:hasMap", null);
			for (Resource bn : bnl){
				if (bn.asNode().getBlankNodeId().equals(blanknode.asNode().getBlankNodeId()))
					bn0 = bn;
			}
			map = (Map<String,Object>)Triple.get(model,bn0, "pip:hasMap");

			// perform rest call
//			StringWriter sw = new StringWriter();
//			JSONValue.writeJSONString(map, sw);
//			//System.out.println("MAP="+sw.toString());
			callRest.call(map);
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return NodeValue.TRUE; //lnv.get(0) ;
    }
}
