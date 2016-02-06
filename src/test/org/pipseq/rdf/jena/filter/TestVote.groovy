package org.pipseq.rdf.jena.filter;

import static org.junit.Assert.*;

import org.junit.*;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.*;
import com.hp.hpl.jena.reasoner.rulesys.*;
import com.hp.hpl.jena.sparql.expr.aggregate.AggregateRegistry;
import com.hp.hpl.jena.sparql.graph.NodeConst;
import com.hp.hpl.jena.util.*;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import org.apache.jena.sparql.function.*;

class TestVote {

	def buyResult = """---------\r\n| posn  |\r\n=========\r\n| "buy" |\r\n---------\r\n"""
	def noResult = """--------\r\n| posn |\r\n========\r\n--------\r\n"""

	@Test
	public void test1() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .
:c1 :hasPosition "buy" .
:c2 :hasPosition "buy" .
:c3 :hasPosition "buy" .
:c4 :hasPosition "sell" .
"""

		def query = """

PREFIX f: <java:org.pipseq.rdf.jena.filter.>
prefix : <http://example.org/stuff/1.0/>
select ?posn
 {
    bind (:UnanimousPolicy as ?policy ) .
    bind (:c1 as ?a)
	
    bind (:c2 as ?b)
    bind (:c3 as ?c)
	filter(?posn)
	?a :hasPosition ?a9 .
	?b :hasPosition ?b9 .
	?c :hasPosition ?c9 .
bind (f:vote(?policy,?a9,?b9,?c9) as ?posn)
}

"""
		Model model = ModelFactory.createDefaultModel();
		InputStream is = new ByteArrayInputStream(ttl.getBytes());
		model.read(is,null,"TTL");
		//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		def rs = ResultSetFormatter.asText(results);
		println rs

		assert buyResult == rs, "wrong results"
	}

	@Test
	public void test1a() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .
:c1 :hasPosition "buy" .
:c2 :hasPosition "sell" .
:c3 :hasPosition "buy" .
:c4 :hasPosition "sell" .
"""

		def query = """

PREFIX f: <java:org.pipseq.rdf.jena.filter.>
prefix : <http://example.org/stuff/1.0/>
select ?posn
 {
    bind (:UnanimousPolicy as ?policy ) .
    bind (:c1 as ?a)
	
    bind (:c2 as ?b)
    bind (:c3 as ?c)
	filter(?posn)
	?a :hasPosition ?a9 .
	?b :hasPosition ?b9 .
	?c :hasPosition ?c9 .
bind (f:vote(?policy,?a9,?b9,?c9) as ?posn)
}

"""
		Model model = ModelFactory.createDefaultModel();
		InputStream is = new ByteArrayInputStream(ttl.getBytes());
		model.read(is,null,"TTL");
		//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		def rs = ResultSetFormatter.asText(results);
		println rs

		assert noResult == rs, "wrong results"
	}

	@Test
	public void test2() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .
:c1 :hasPosition "buy" .
:c2 :hasPosition "buy" .
:c3 :hasPosition "buy" .
:c4 :hasPosition "sell" .
"""

		def query = """

PREFIX f: <java:org.pipseq.rdf.jena.filter.>
prefix : <http://example.org/stuff/1.0/>
select ?posn
 {
    bind (:MajorityPolicy as ?policy ) .
    bind (:c1 as ?a)
	
    bind (:c2 as ?b)
    bind (:c3 as ?c)
	filter(?posn)
	?a :hasPosition ?a9 .
	?b :hasPosition ?b9 .
	?c :hasPosition ?c9 .
bind (f:vote(?policy,?a9,?b9,?c9) as ?posn)
}

"""
		Model model = ModelFactory.createDefaultModel();
		InputStream is = new ByteArrayInputStream(ttl.getBytes());
		model.read(is,null,"TTL");
		//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		def rs = ResultSetFormatter.asText(results);
		println rs
		assert buyResult == rs, "wrong results"
	}

	@Test
	public void test2a() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .
:c1 :hasPosition "buy" .
:c2 :hasPosition "sell" .
:c3 :hasPosition "buy" .
:c4 :hasPosition "sell" .
:c5 :hasPosition "buy" .
:c6 :hasPosition "sell" .
"""

		def query = """

PREFIX f: <java:org.pipseq.rdf.jena.filter.>
prefix : <http://example.org/stuff/1.0/>
select ?posn
 {
    bind (:MajorityPolicy as ?policy ) .
    bind (:c1 as ?a)
    bind (:c2 as ?b)
    bind (:c3 as ?c)
    bind (:c4 as ?d)
    bind (:c5 as ?e)
	filter(?posn)
	?a :hasPosition ?a9 .
	?b :hasPosition ?b9 .
	?c :hasPosition ?c9 .
	?d :hasPosition ?d9 .
	?e :hasPosition ?e9 .
bind (f:vote(?policy,?a9,?b9,?c9,?d9,?e9) as ?posn)
}

"""
		Model model = ModelFactory.createDefaultModel();
		InputStream is = new ByteArrayInputStream(ttl.getBytes());
		model.read(is,null,"TTL");
		//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		def rs = ResultSetFormatter.asText(results);
		println rs
		assert buyResult == rs, "wrong results"
	}

	@Test
	public void test2b() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .
:c1 :hasPosition "buy" .
:c2 :hasPosition "sell" .
:c3 :hasPosition "buy" .
:c4 :hasPosition "sell" .
:c5 :hasPosition "buy" .
:c6 :hasPosition "sell" .
"""

		def query = """

PREFIX f: <java:org.pipseq.rdf.jena.filter.>
prefix : <http://example.org/stuff/1.0/>
select ?posn
 {
    bind (:MajorityPolicy as ?policy ) .
    bind (:c1 as ?a)
    bind (:c2 as ?b)
    bind (:c3 as ?c)
    bind (:c4 as ?d)
    bind (:c5 as ?e)
    bind (:c6 as ?f)
	filter(?posn)
	?a :hasPosition ?a9 .
	?b :hasPosition ?b9 .
	?c :hasPosition ?c9 .
	?d :hasPosition ?d9 .
	?e :hasPosition ?e9 .
	?f :hasPosition ?f9 .
bind (f:vote(?policy,?a9,?b9,?c9,?d9,?e9,?f9) as ?posn)
}

"""
		Model model = ModelFactory.createDefaultModel();
		InputStream is = new ByteArrayInputStream(ttl.getBytes());
		model.read(is,null,"TTL");
		//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		def rs = ResultSetFormatter.asText(results);
		println rs
		assert buyResult == rs, "wrong results"
	}

	@Test
	public void test3() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .
:c1 :hasPosition "buy" .
:c2 :hasPosition "buy" .
:c3 :hasPosition "buy" .
:c4 :hasPosition "sell" .
"""

		def query = """

PREFIX f: <java:org.pipseq.rdf.jena.filter.>
prefix : <http://example.org/stuff/1.0/>
select ?posn
 {
    bind (:SuperMajorityPolicy as ?policy ) .
    bind (:c1 as ?a)

    bind (:c2 as ?b)
    bind (:c3 as ?c)
	filter(?posn)
	?a :hasPosition ?a9 .
	?b :hasPosition ?b9 .
	?c :hasPosition ?c9 .
bind (f:vote(?policy,?a9,?b9,?c9) as ?posn)
}

"""
		Model model = ModelFactory.createDefaultModel();
		InputStream is = new ByteArrayInputStream(ttl.getBytes());
		model.read(is,null,"TTL");
		//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		def rs = ResultSetFormatter.asText(results);
		println rs
		assert buyResult == rs, "wrong results"
	}

	@Test
	public void test3c() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .
:c1 :hasPosition "buy" .
:c2 :hasPosition "sell" .
:c3 :hasPosition "buy" .
:c4 :hasPosition "sell" .
"""

		def query = """

PREFIX f: <java:org.pipseq.rdf.jena.filter.>
prefix : <http://example.org/stuff/1.0/>
select ?posn
 {
    bind (:SuperMajorityPolicy as ?policy ) .
    bind (:c1 as ?a)

    bind (:c2 as ?b)
    bind (:c3 as ?c)
	filter(?posn)
	?a :hasPosition ?a9 .
	?b :hasPosition ?b9 .
	?c :hasPosition ?c9 .
bind (f:vote(?policy,?a9,?b9,?c9) as ?posn)
}

"""
		Model model = ModelFactory.createDefaultModel();
		InputStream is = new ByteArrayInputStream(ttl.getBytes());
		model.read(is,null,"TTL");
		//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		def rs = ResultSetFormatter.asText(results);
		println rs
		assert buyResult == rs, "wrong results"
	}

	@Test
	public void test3a() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .
:c1 :hasPosition "buy" .
:c2 :hasPosition "sell" .
:c3 :hasPosition "buy" .
:c4 :hasPosition "sell" .
:c5 :hasPosition "buy" .
:c6 :hasPosition "sell" .
"""

		def query = """

PREFIX f: <java:org.pipseq.rdf.jena.filter.>
prefix : <http://example.org/stuff/1.0/>
select ?posn
 {
    bind (:SuperMajorityPolicy as ?policy ) .
    bind (:c1 as ?a)
    bind (:c2 as ?b)
    bind (:c3 as ?c)
    bind (:c4 as ?d)
    bind (:c5 as ?e)
	filter(?posn)
	?a :hasPosition ?a9 .
	?b :hasPosition ?b9 .
	?c :hasPosition ?c9 .
	?d :hasPosition ?d9 .
	?e :hasPosition ?e9 .
bind (f:vote(?policy,?a9,?b9,?c9,?d9,?e9) as ?posn)
}

"""
		Model model = ModelFactory.createDefaultModel();
		InputStream is = new ByteArrayInputStream(ttl.getBytes());
		model.read(is,null,"TTL");
		//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		def rs = ResultSetFormatter.asText(results);
		println rs
		assert noResult == rs, "wrong results"
	}

	@Test
	public void test3b() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .
:c1 :hasPosition "buy" .
:c2 :hasPosition "sell" .
:c3 :hasPosition "buy" .
:c4 :hasPosition "sell" .
:c5 :hasPosition "buy" .
:c6 :hasPosition "sell" .
"""

		def query = """

PREFIX f: <java:org.pipseq.rdf.jena.filter.>
prefix : <http://example.org/stuff/1.0/>
select ?posn
 {
    bind (:SuperMajorityPolicy as ?policy ) .
    bind (:c1 as ?a)
    bind (:c2 as ?b)
    bind (:c3 as ?c)
    bind (:c4 as ?d)
    bind (:c5 as ?e)
    bind (:c6 as ?f)
	filter(?posn)
	?a :hasPosition ?a9 .
	?b :hasPosition ?b9 .
	?c :hasPosition ?c9 .
	?d :hasPosition ?d9 .
	?e :hasPosition ?e9 .
	?f :hasPosition ?f9 .
bind (f:vote(?policy,?a9,?b9,?c9,?d9,?e9,?f9) as ?posn)
}

"""
		Model model = ModelFactory.createDefaultModel();
		InputStream is = new ByteArrayInputStream(ttl.getBytes());
		model.read(is,null,"TTL");
		//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		def rs = ResultSetFormatter.asText(results);
		println rs
		assert noResult == rs, "wrong results"
	}

	@Test
	public void test4() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .
:c1 :hasPosition "buy" .
:c2 :hasPosition "sell" .
:c3 :hasPosition "buy" .
:c4 :hasPosition "sell" .
:c5 :hasPosition "buy" .
:c6 :hasPosition "sell" .
"""

		def query = """

PREFIX f: <java:org.pipseq.rdf.jena.filter.>
prefix : <http://example.org/stuff/1.0/>
select ?posn
 {
    bind (:UnknownPolicy as ?policy ) .
    bind (:c1 as ?a)
    bind (:c2 as ?b)
    bind (:c3 as ?c)
    bind (:c4 as ?d)
    bind (:c5 as ?e)
    bind (:c6 as ?f)
	filter(?posn)
	?a :hasPosition ?a9 .
	?b :hasPosition ?b9 .
	?c :hasPosition ?c9 .
	?d :hasPosition ?d9 .
	?e :hasPosition ?e9 .
	?f :hasPosition ?f9 .
bind (f:vote(?policy,?a9,?b9,?c9,?d9,?e9,?f9) as ?posn)
}

"""
		Model model = ModelFactory.createDefaultModel();
		InputStream is = new ByteArrayInputStream(ttl.getBytes());
		model.read(is,null,"TTL");
		//org.pipseq.rdf.jena.util.SparqlScope.scope(model);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		try {
					ResultSet results =  qe.execSelect();

		} catch (Exception e) {
			assert e instanceof RuntimeException
		}
	}
}
