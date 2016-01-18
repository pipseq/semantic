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
//@Grapes([
//@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7');
//@Grab('oauth.signpost:signpost-core:1.2.1.2');
//@Grab('oauth.signpost:signpost-commonshttp4:1.2.1.2');
//	@GrabConfig( systemClassLoader=true )
//])

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient;
import groovyx.net.http.ContentType;
import groovyx.net.http.Method;

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
	
let (?c := f:TestRest(?a,?b) )
}

see NodeRestTest for more.
 * @author rspates
 *
 */
public class TestRest  extends FunctionBase {

	private static final Logger log = LoggerFactory.getLogger(TestRest.class);
    
    /**
     * Instantiates a new select.
     */
    public TestRest() { super() ; }


	@Override
	public NodeValue exec(List<NodeValue> args) {

//		for (NodeValue arg : args)
//			if (arg.equals(NodeValue.TRUE)) return NodeValue.TRUE;
		try {
			nodeRest(args);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return NodeValue.FALSE;
	}

	@Override
	public void checkBuild(String uri, ExprList args) {
		
	}
	// use app.js
	public void nodeRest(List<NodeValue> args) {
		def client = new RESTClient( 'http://localhost:3000/' )
		
		def resp = client.post(
			path : '/func',
			 contentType : 'application/json',
			requestContentType : 'application/json',
			body : [ firstName:'John', lastName:'Doe' ]
			)

		assert resp.status == 200
		def map = resp.getData()
		println map
		
	}
	
	// prior
	void nodeRest0() {
		def client = new RESTClient( 'http://localhost:3000/' )
		
		def resp = client.get(path : '/test' )

		assert resp.status == 200
		println resp.getData()
	}
	
	// use app0.js
	void nodeRest01() {
		def client = new RESTClient( 'http://localhost:3000/' )
		
		def resp = client.post(
			path : '/func',
			 contentType : 'application/json',
			requestContentType : 'application/json',
			body : [ firstName:'John', lastName:'Doe' ]
			)

		assert resp.status == 200
		println resp.getData()
	}
	

}
