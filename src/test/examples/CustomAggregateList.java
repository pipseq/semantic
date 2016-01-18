package examples;


/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.jena.atlas.logging.LogCtl;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
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
import com.hp.hpl.jena.sparql.sse.SSE;

/**
 * Custom aggregate example.
 * <p>
 * Custom aggregates must be registered before parsing the query; custom
 * aggregates and custom functions have the same syntax so the to tell the
 * differenc, the parser needs to know which IRIs are custom aggregates.
 * <p>
 * The aggregate is registered as a URI, AccumulatorFactory and default value
 * for the "no groups" case.
 */
public class CustomAggregateList {
    static { LogCtl.setCmdLogging(); }
    /**
     * Execution of a custom aggregate is with accumulators. One accumulator is
     * created for the factory for each group in a query execution.
     */
    static AccumulatorFactory myAccumulatorFactory = new AccumulatorFactory() {
        @Override
        public Accumulator createAccumulator(AggCustom agg) { return new MyAccumulator(agg) ; }
    } ;
    
    /**
     * Example accumulators - counts the number of valid literals
     * of an expression over a group. 
     */
    static class MyAccumulator implements Accumulator {
        private AggCustom agg ;
        MyAccumulator(AggCustom agg) { this.agg = agg ; }
        Model model;
    	private static Property first;
    	private static Property rest;
    	private static Resource nil;
    	Map<RDFNode, Object> map= new LinkedHashMap<RDFNode, Object>();
        /** Function called on each row in a group */
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

        /** Function called to retrieve the value for a single group */
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
    }
    
    public static void main(String[] args) {
        
        // Example aggregate that counts literals.
        // Returns unbound for no rows. 
        String aggUri = "http://example/countLiterals" ;
        
        
        /* Registration */
        AggregateRegistry.register(aggUri, myAccumulatorFactory, NodeConst.nodeMinusOne);
        
        
        // Some data.
        Graph g = SSE.parseGraph("(graph (:s :p :o) (:s :p 'buy') (:s :p 'sell'))") ;
        String qs = "SELECT (<http://example/countLiterals>(?o) AS ?x) {?s ?p ?o}" ;
        
        // Execution as normal.
        Query q = QueryFactory.create(qs) ;
        Model model = ModelFactory.createModelForGraph(g);
        try ( QueryExecution qexec = QueryExecutionFactory.create(q, model) ) {
            ResultSet rs = qexec.execSelect() ;
        	//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
            ResultSetFormatter.out(rs);
        	model.write(System.out, "TTL");
        }
    }
    
}