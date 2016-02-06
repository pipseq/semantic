package org.pipseq.spin;

import org.junit.*;
import org.topbraid.spin.inference.DefaultSPINRuleComparator;
import org.topbraid.spin.inference.SPINExplanations;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.inference.SPINRuleComparator;
import org.topbraid.spin.progress.ProgressMonitor;
import org.topbraid.spin.statistics.SPINStatistics;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.CommandWrapper;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.util.SPINQueryFinder;
import org.topbraid.spin.vocabulary.SPIN;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.*;
import com.hp.hpl.jena.reasoner.rulesys.*;
import com.hp.hpl.jena.util.*;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import org.pipseq.spin.compare.RestrictionSPINRuleComparator;
import org.pipseq.rdf.jena.aggregate.AggMethodRegistry;
import org.pipseq.rdf.jena.model.*;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

/**
 * Takes the pipseq.ttl model
 * Takes an fxs: strategy model
 * Test simulates three time period occurences
 * an m5 period
 * an m1 period
 * an m5 period
 * each represents incremental addition of indicator results
 * and then inferencing with SPIN
 * @author rspates
 *
 */
public class RestTester {

	String prolog = """
@prefix fxs: <http://pipseq.org/2016/01/fx/strategy#> .
@prefix pip: <http://pipseq.org/2016/01/forex#> .
@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
@prefix owl:     <http://www.w3.org/2002/07/owl#> .
@prefix xsd:     <http://www.w3.org/2001/XMLSchema#> .
@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix fn:		<http://www.w3.org/2005/xpath-functions#> .

"""
	
	@Before
	public void setup() {
		AggMethodRegistry.init();	// TODO--make this occur transparently
		
	}
	
	int ontModelSize = 0;
	
	// this tests the combo of jexec-getListObjs and
	// restClientMap calling node.js
	// the jexec call constructs a graph of objects in a map
	// the restClientMap pulls the map from the model
	// sends it via json to app.js running in node
	// where it is processed and returned.
	// This is a demonstration test
	@Test
	public void test0() {
		Model model = ModelFactory.createDefaultModel();
		model.read(new FileInputStream("test.ttl"),null,"TTL");
		//scope(model);
		
		def query="""
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

"""
		List results = Sparql.query(model,query)
		String sout = TripleUtil.printQueryResults(results);
		println(sout);
//		println(model.size());
		
		// shows means of retrieving the blanknode anchor for the map
		Object bn = Triple.getRes(model, "pip:hasMap", null);
		//model.listObjectsOfProperty(new Property("pip:hasMap"))
		Object map = Triple.get(model, bn, "pip:hasMap");
		//Object map = mapLit.getValue();
//		println(map);
		
//		StringWriter sw = new StringWriter();
//		JSONValue.writeJSONString(map, sw);
//		println sw.toString();
	}

}
