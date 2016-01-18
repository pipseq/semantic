package org.pipseq.rdf.jena.filter
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase;
import com.hp.hpl.jena.sparql.function.FunctionBase1;
import com.hp.hpl.jena.sparql.function.FunctionBase2;
import com.hp.hpl.jena.sparql.function.FunctionBase3;
import com.hp.hpl.jena.sparql.function.FunctionEnv;

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient;
import groovyx.net.http.ContentType;
import groovyx.net.http.Method;

/**
 * start up node app.js listener
 * use this query to test this function
 * This approach produces an arbitrarily deep nested graph of objects
 * The graph is captured in a map cumulatively (across rows) by getListObjs
 * which returns a blanknode location in the graph for a java Map
 * The aggregate restClientMap then accesses the Map via blanknode
 * and sends the rest call to app.js server.
  PREFIX f: <java:org.pipseq.rdf.jena.filter.>
  SELECT ((<java:org.pipseq.rdf.jena.aggregate.restClientMap>(?bn)) AS  ?res) {
  	?a a pip:Trigger .
	?a ?b ?c .
	optional {
	?c ?d ?e
		optional {
		?e ?f ?g
		}
	}
  let (?bn := f:jexec("getListObjs",?a,?b,?c, ?d, ?e, ?f, ?g))
  }

see NodeRestTest for more.
 * @author rspates
 *
 */
public class CallRest {

	private static final Logger log = LoggerFactory.getLogger(CallRest.class);
    
    /**
     * Instantiates a new select.
     */
    public CallRest() { 
	}


	public void call(def map) {
		def client = new RESTClient( 'http://localhost:3001/' )//3001 for TCP/IP monitor
		
		def resp = client.post(
			path : '/func',
			contentType : 'application/json',
			requestContentType : 'application/json',
			body :  map
			)

		assert resp.status == 200
		def map2 = resp.getData()
		println map2
		
	}
	

}
