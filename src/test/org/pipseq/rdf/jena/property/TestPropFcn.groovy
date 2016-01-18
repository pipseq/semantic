package org.pipseq.rdf.jena.property;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.*;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.*;
import com.hp.hpl.jena.reasoner.rulesys.*;
import com.hp.hpl.jena.util.*;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;
import org.apache.jena.sparql.function.*;
import java.io.*;

class TestPropFcn {

	@Test
	public void test() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .

:a :b ( "apple" "banana" ) .

:c1 :d "buy" .
:c2 :d "sell" .
:c3 :d "buy" .
"""
		
	def queries = """

prefix extp: <java:org.pipseq.rdf.jena.property.>
prefix list:  <http://jena.hpl.hp.com/ARQ/list#>
prefix : <http://example.org/stuff/1.0/>
prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT ?l {
    ?c :d ?b .
 ?l extp:collectList ?b .
}

prefix extp: <java:org.pipseq.rdf.jena.property.>
prefix rdfs: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
select * {
?a rdfs:first ?b .
?a rdfs:rest ?r .
}

prefix extp: <java:org.pipseq.rdf.jena.property.>
prefix rdfs: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
select * {
?a rdfs:first "buy" .
?a extp:countList ?s .
} order by desc(?s) limit 1

prefix extp: <java:org.pipseq.rdf.jena.property.>
prefix rdfs: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
select * {
?a rdfs:first "buy" .
?a extp:allSameList ?s .
} order by desc(?s) limit 1


"""
	Model model = ModelFactory.createDefaultModel();
	InputStream is = new ByteArrayInputStream(ttl.getBytes());
	model.read(is,null,"TTL");
	org.pipseq.rdf.jena.util.SparqlScope.scope(model);
	model.write(System.out, "TTL");
	}
	
	//@Test
	public void test1() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .

:a :b ( "apple" "banana" ) .

:c :d "first" .
:c :d "second" .
:c :d "third" .
"""
		
	def queries = """


prefix extp: <java:org.pipseq.rdf.jena.property.>
prefix list:  <http://jena.hpl.hp.com/ARQ/list#>
prefix : <http://example.org/stuff/1.0/>
prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
SELECT *
WHERE {
    :c :d ?a .
 ?l extp:listTest ?a .
}

"""
	Model model = ModelFactory.createDefaultModel();
	InputStream is = new ByteArrayInputStream(ttl.getBytes());
	model.read(is,null,"TTL");
	org.pipseq.rdf.jena.util.SparqlScope.scope(model);
	model.write(System.out, "TTL");
	}
	
	//@Test
	public void test0() {
		def ttl = """
@prefix : <http://example.org/stuff/1.0/> .

:a :b ( "apple" "banana" ) .
"""
		
	def queries = """
prefix list:  <http://jena.hpl.hp.com/ARQ/list#>
prefix : <http://example.org/stuff/1.0/>
prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
SELECT ?ll
WHERE {
    :a :b ?p .
	?p list:member "apple" .
	:a :b/rdf:rest/rdf:first ?ll
}


prefix extp: <java:org.pipseq.rdf.jena.property.>
prefix list:  <http://jena.hpl.hp.com/ARQ/list#>
prefix : <http://example.org/stuff/1.0/>
prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
SELECT *
WHERE {
    :a :b ?p .
 ?p extp:traverseLinkedList ?list .
}

prefix ext: <java:org.pipseq.rdf.jena.filter.>
prefix extp: <java:org.pipseq.rdf.jena.property.>
prefix list:  <http://jena.hpl.hp.com/ARQ/list#>
prefix : <http://example.org/stuff/1.0/>
prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
SELECT *
WHERE {
    :a :b ?p .
	let (?x := "apple")
    filter ext:isCollectionMember( ?p, ?x)
}

"""
	Model model = ModelFactory.createDefaultModel();
	InputStream is = new ByteArrayInputStream(ttl.getBytes());
	model.read(is,null,"TTL");
	org.pipseq.rdf.jena.util.SparqlScope.scope(model);
	model.write(System.out, "TTL");
	}
	

}
