package org.pipseq.rdf.jena.filter;

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

class TestFilter {

	static Model baseModel = FileManager.get().loadModel("file:///C:/users/rspates/Google%20Drive/work//fx/tbc/forex/pipseq.ttl");
	//static Model baseModel = FileManager.get().loadModel("file:data/pipseq.ttl");
	// Create OntModel with imports
	OntModel ontModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_MEM,baseModel);
	String prolog = """
@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
@prefix pip:     <http://pipseq.org#> .
@prefix owl:     <http://www.w3.org/2002/07/owl#> .
@prefix xsd:     <http://www.w3.org/2001/XMLSchema#> .
@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

"""
	public PipsTester(){
		String uri = "http://pipseq.org#";
		PrintUtil.registerPrefix("pip", uri);
		// Initialize system functions and templates
		SPINModuleRegistry.get().init();
		// Register locally defined functions
		SPINModuleRegistry.get().registerAll(ontModel, null);
	}

	void insertModel(ttl){
		InputStream is = new ByteArrayInputStream((prolog + ttl).getBytes());
		baseModel.read(is,null,"TTL");

	}
	@Test
	public void test() {
		String s = """
pip:EMAResult_f100
  rdf:type pip:EMAResult ;
  pip:Value "89.4" ;
  pip:TimeStamp "2015-10-19T14:06:17.5Z" ;
.
pip:EMAResult_s100
  rdf:type pip:EMAResult ;
  pip:Value "89.5" ;
  pip:TimeStamp "2015-10-19T14:06:17.639Z" ;
.
pip:EMA_8 pip:hasIndicatorResult pip:EMAResult_f100
.
pip:EMA_34 pip:hasIndicatorResult pip:EMAResult_s100
.
"""
		insertModel(s);
		 s = """
pip:EMAResult_f101
  rdf:type pip:EMAResult ;
  pip:Value "89.5" ;
  pip:TimeStamp "2015-10-19T14:06:18.5Z" ;
.
pip:EMAResult_s101
  rdf:type pip:EMAResult ;
  pip:Value "89.6" ;
  pip:TimeStamp "2015-10-19T14:06:18.639Z" ;
.
pip:EMA_8 pip:hasIndicatorResult pip:EMAResult_f101
.
pip:EMA_34 pip:hasIndicatorResult pip:EMAResult_s101
.
"""
		insertModel(s);
		org.pipseq.rdf.jena.util.SparqlScope.scope(ontModel);
		println("done")
	}

}
