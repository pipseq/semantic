package org.pipseq.spin;

import java.util.List;
import java.util.Map;

import org.pipseq.common.DateTime;
import org.pipseq.common.IClock
import org.pipseq.rdf.jena.model.RowMapper;import org.pipseq.rdf.jena.model.Sparql;
import org.pipseq.rdf.jena.model.SparqlResultRowObject;

import com.google.gson.Gson
import com.hp.hpl.jena.rdf.model.Model;





import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.pipseq.rdf.jena.cfg.ModelWrapper
import org.pipseq.rdf.jena.cfg.WrapperRegistry;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
public class TradeTesterTime {

	private static final Logger log = LoggerFactory.getLogger(TradeTesterTime.class);
	static String prolog = """
@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
@prefix pip:     <http://pipseq.org#> .
@prefix fxs:     <http://pipseq.org/strategy#> .
@prefix owl:     <http://www.w3.org/2002/07/owl#> .
@prefix xsd:     <http://www.w3.org/2001/XMLSchema#> .
@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix fn:		<http://www.w3.org/2005/xpath-functions#> .

"""


	public TradeTesterTime(){
		//WrapperRegistry.getInstance().setDefaultLoggingLevel(0);
		RuleEngineFactory.getInstance().setModelFiles(
			[
			"C:/users/rspates/Google Drive/work/fx/tbc/forex/pipseq.ttl",
			"C:/Users/rspates/Google Drive/work/fx/tbc//forexStrategy/pipseqStrategy_\$.ttl"
			]);
		ruleEngine = RuleEngineFactory.getInstance().getRuleEngine("test"+(rec++));
		ruleEngine.setFeedbackQuery("""
		describe ?s {
			?s a <http://pipseq.org#TradeRecommendation> .
		}
		""");
		ruleEngine.setOutcomeQuery("""
		describe ?s {
			?s a <http://pipseq.org#Trade> .
		}
		""");
		ruleEngine.setDiagnostics(true);
		

	}
	
	RuleEngine ruleEngine;
	
	@After
	public void shutdown(){
		RuleEngineFactory.reset();
	}

	int rec = 1;
	@Before
	public void setup() {

		WrapperRegistry.getInstance().setDefaultLoggingLevel(6);
		RuleEngineFactory.getInstance().setModelFiles(
			[
			"C:/users/rspates/Google Drive/work/fx/tbc/forex/pipseq.ttl",
			"C:/Users/rspates/Google Drive/work/fx/tbc//forexStrategy/pipseqStrategy_\$.ttl"
			]);
		ruleEngine = RuleEngineFactory.getInstance().getRuleEngine("test"+(rec++));
		ruleEngine.setFeedbackQuery("""
		describe ?s {
			?s a <http://pipseq.org#TradeRecommendation> .
		}
		""");
		ruleEngine.setOutcomeQuery("""
		describe ?s {
			?s a <http://pipseq.org#Trade> .
		}
		""");
		ruleEngine.setDiagnostics(true);
		


	}
	def setTime = {
		DateTime.setClock(new IClock(){
			DateTime now(){
				return DateTime.parse(it);
			}
			DateTime now(TimeZone timeZone){
				return null;
			}
		});
	}

	def mapComponents = [ 
		"ema m1 pattern": {
			
			Triple.remove(ruleEngine.getModel(), "pip:RuleContext"); // Change the context!!
					String s2 = """
		pip:RuleContext
		rdf:type pip:RuleContextSingleton ;
		pip:hasInstrument pip:AUDJPY ;
		pip:hasTimeFrame pip:m1 ;
.
pip:EMAResult_f102_m1
  rdf:type pip:EMAResult ;
  pip:Value "89.1"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:08:17.5Z"^^xsd:dateTime ;
.
pip:EMAResult_s102_m1
  rdf:type pip:EMAResult ;
  pip:Value "89.0"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:08:17.639Z"^^xsd:dateTime ;
.
fxs:EMA_8_1 pip:hasIndicatorResult pip:EMAResult_f102_m1
.
fxs:EMA_34_1 pip:hasIndicatorResult pip:EMAResult_s102_m1
.
pip:EMAResult_f103_m1
  rdf:type pip:EMAResult ;
  pip:Value "89.2"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:09:17.5Z"^^xsd:dateTime ;
.
pip:EMAResult_s103_m1
  rdf:type pip:EMAResult ;
  pip:Value "89.1"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:09:17.639Z"^^xsd:dateTime ;
.
fxs:EMA_8_1 pip:hasIndicatorResult pip:EMAResult_f103_m1
.
fxs:EMA_34_1 pip:hasIndicatorResult pip:EMAResult_s103_m1
.
fxs:Ask_1
  rdf:type pip:Ask ;
  pip:Value "89.3"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:08:17.5Z"^^xsd:dateTime ;
.
fxs:Bid_1
  rdf:type pip:Bid ;
  pip:Value "89.1"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:08:17.5Z"^^xsd:dateTime ;
.
fxs:PriceFastSlowPattern_1
		pip:hasAsk	fxs:Ask_1 ;
		pip:hasBid	fxs:Bid_1 ;
		.
fxs:Ask_2
  rdf:type pip:Ask ;
  pip:Value "89.4"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:09:17.5Z"^^xsd:dateTime ;
.
fxs:Bid_2
  rdf:type pip:Bid ;
  pip:Value "89.2"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:09:17.5Z"^^xsd:dateTime ;
.
fxs:PriceFastSlowPattern_1
		pip:hasAsk	fxs:Ask_2 ;
		pip:hasBid	fxs:Bid_2 ;
		.

"""
							ruleEngine.insertModel(prolog + s2);
							
							
					ModelWrapper mw = ruleEngine.run();
					assert ruleEngine.hasOutcomeResults() == it
			
		},
		"ema m5 cross": {
			Triple.remove(ruleEngine.getModel(), "pip:RuleContext"); // Change the context!!
			String s = """

		pip:RuleContext
		rdf:type pip:RuleContextSingleton ;
		pip:hasInstrument pip:AUDJPY ;
		pip:hasTimeFrame pip:m5 ;
.

pip:EMAResult_f102
  rdf:type pip:EMAResult ;
  pip:Value "89.2"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:08:17.5Z"^^xsd:dateTime ;
.
pip:EMAResult_s102
  rdf:type pip:EMAResult ;
  pip:Value "89.3"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:08:17.639Z"^^xsd:dateTime ;
.
fxs:EMA_8 pip:hasIndicatorResult pip:EMAResult_f102
.
fxs:EMA_34 pip:hasIndicatorResult pip:EMAResult_s102
.
pip:EMAResult_f103
  rdf:type pip:EMAResult ;
  pip:Value "89.4"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:09:17.5Z"^^xsd:dateTime ;
.
pip:EMAResult_s103
  rdf:type pip:EMAResult ;
  pip:Value "89.2"^^xsd:float ;
  pip:TimeStamp "2015-10-19T14:09:17.639Z"^^xsd:dateTime ;
.
fxs:EMA_8 pip:hasIndicatorResult pip:EMAResult_f103
.
fxs:EMA_34 pip:hasIndicatorResult pip:EMAResult_s103
.


"""
					ruleEngine.insertModel(prolog + s);
					ModelWrapper mw = ruleEngine.run();
					assert ruleEngine.hasOutcomeResults() == it
			
		},
		"rsi m5 threshold": {
			Triple.remove(ruleEngine.getModel(), "pip:RuleContext"); // Change the context!!
			
			String s3 = """

		pip:RuleContext
		rdf:type pip:RuleContextSingleton ;
		pip:hasInstrument pip:AUDJPY ;
		pip:hasTimeFrame pip:m5 ;
.



pip:RSIResult_100b
  rdf:type pip:RSIResult ;
  pip:Value "69.2"^^xsd:float ;
  pip:TimeStamp "2015-10-19T15:09:17.639Z"^^xsd:dateTime ;
.
fxs:RSI_9 pip:hasIndicatorResult pip:RSIResult_100b ;
.

pip:RSIResult_101b
  rdf:type pip:RSIResult ;
  pip:Value "79.2"^^xsd:float ;
  pip:TimeStamp "2015-10-19T15:09:18.639Z"^^xsd:dateTime ;
.
fxs:RSI_9 pip:hasIndicatorResult pip:RSIResult_101b ;
.

"""
			ruleEngine.insertModel(prolog + s3);
			ModelWrapper mw = ruleEngine.run();
			assert ruleEngine.hasOutcomeResults() == it
	
		},
	"dump":{
	/*
	 http://www.w3.org/1999/02/22-rdf-syntax-ns#type:http://pipseq.org#Trade, 
	 http://pipseq.org#TimeStamp:2015-12-02T20:36:39.866Z, 
	 http://pipseq.org#hasPosition:http://pipseq.org#PositionLong
	 */
	def map = Sparql.queryMap(ruleEngine.getLastInferences().get(), """
		select ?p ?o {
			?s a pip:Trade .
			?s ?p ?o .
			}
	""");
		println map
		println "outcome = "+ruleEngine.hasOutcomeResults();
		},
	"scope":{
		ruleEngine.scope();
		}
		]
	// TODO - prefix pipff: pipfa: (?)
	@Test
	public void test1(){
		setTime("10/19/2014 14:20:55")
		
		mapComponents["ema m5 cross"](false)
		mapComponents["ema m1 pattern"](false)
		
		setTime("10/19/2014 14:30:55")
		
		mapComponents["rsi m5 threshold"](true)
	}
	@Test
	public void test2(){
		
		setTime("10/19/2014 14:20:55")
		mapComponents["rsi m5 threshold"](false)
		mapComponents["ema m1 pattern"](false)
		
		setTime("10/19/2014 14:30:55")
		mapComponents["ema m5 cross"](true)
	}
	
	@Test
	public void test3(){
		
		setTime("10/19/2014 14:20:55")
		mapComponents["rsi m5 threshold"](false)
		mapComponents["ema m5 cross"](false)
		
		setTime("10/19/2014 14:30:55")
		mapComponents["ema m1 pattern"](true)
	}
	
	@Test
	public void test4(){
		
		setTime("10/19/2014 14:20:55")
		mapComponents["ema m1 pattern"](false)
		mapComponents["ema m5 cross"](false)
		
		setTime("10/19/2014 14:30:55")
		mapComponents["rsi m5 threshold"](true)
	}

	@Test
	public void test5(){
		
		setTime("10/19/2014 14:20:55")
		mapComponents["ema m1 pattern"](false)
		mapComponents["rsi m5 threshold"](false)
		
		setTime("10/19/2014 14:30:55")
		mapComponents["ema m5 cross"](true)
	}
	
}
