package org.pipseq.rdf.jena.aggregate;

import static org.junit.Assert.*;

import org.junit.*;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.*;
import com.hp.hpl.jena.reasoner.rulesys.*;
import com.hp.hpl.jena.sparql.expr.aggregate.AggregateRegistry;
import com.hp.hpl.jena.sparql.graph.NodeConst;
import com.hp.hpl.jena.util.*;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import org.apache.jena.sparql.function.*;

class TestAggregate {

	
	// test of makeLinkedList
	@Test
	public void test() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .

:a :b ( "apple" "banana" ) .

:c1 :d "buy" .
:c2 :d "buy" .
:c3 :d "buy" .
"""
		
	def queries = """

prefix exta: <http://org.pipseq.rdf.jena.aggregate/>

prefix exta: <java:org.pipseq.rdf.jena.aggregate.>
prefix list:  <http://jena.hpl.hp.com/ARQ/list#>
prefix : <http://example.org/stuff/1.0/>
prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT (exta:makeLinkedList(?b) AS ?x) {
    ?c :d ?b .
}

"""
	AggMethodRegistry.init();
	Model model = ModelFactory.createDefaultModel();
	InputStream is = new ByteArrayInputStream(ttl.getBytes());
	model.read(is,null,"TTL");
	org.pipseq.rdf.jena.util.SparqlScope.scope(model);
	model.write(System.out, "TTL");
	}

	
	// test of allSameList
	//@Test
	public void test0() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .

:a :b ( "apple" "banana" ) .

:c1 :d "buy" .
:c2 :d "buy" .
:c3 :d "buy" .
"""
		
	def queries = """

prefix exta: <http://org.pipseq.rdf.jena.aggregate/>

prefix exta: <java:org.pipseq.rdf.jena.aggregate.>
prefix list:  <http://jena.hpl.hp.com/ARQ/list#>
prefix : <http://example.org/stuff/1.0/>
prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT (exta:allSameList(?b) AS ?x) {
    ?c :d ?b .
}

"""
	AggMethodRegistry.init();
	Model model = ModelFactory.createDefaultModel();
	InputStream is = new ByteArrayInputStream(ttl.getBytes());
	model.read(is,null,"TTL");
	org.pipseq.rdf.jena.util.SparqlScope.scope(model);
	model.write(System.out, "TTL");
	}
	


}
