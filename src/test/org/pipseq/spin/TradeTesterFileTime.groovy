package org.pipseq.spin;

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
import org.pipseq.rdf.jena.cfg.ModelWrapper
import org.pipseq.rdf.jena.cfg.WrapperRegistry
import org.pipseq.rdf.jena.listener.ReleaseListener
import org.pipseq.rdf.jena.model.*;
import org.pipseq.common.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
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
public class TradeTesterFileTime {

	private static final Logger log = LoggerFactory.getLogger(TradeTesterFileTime.class);
	static String prolog = """
@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
@prefix pip:     <http://pipseq.org#> .
@prefix fxs:     <http://pipseq.org/strategy#> .
@prefix owl:     <http://www.w3.org/2002/07/owl#> .
@prefix xsd:     <http://www.w3.org/2001/XMLSchema#> .
@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix fn:		<http://www.w3.org/2005/xpath-functions#> .

"""
	RuleEngine ruleEngine;
	int trades = 0;
	
	def setTime = {
		DateTime.setClock(new IClock(){
			DateTime now(){
				return it;
			}
			DateTime now(TimeZone timeZone){
				return null;
			}
		});
	}

	@After
	public void shutdown(){
		ruleEngine.close();
	}

	@Before
	public void setup() {
		WrapperRegistry.getInstance().setDefaultLoggingLevel(2);
		RuleEngineFactory.getInstance().setModelFiles(
			[
			"C:/users/rspates/Google Drive/work/fx/tbc/forex/pipseq.ttl",
			"C:/Users/rspates/Google Drive/work/fx/tbc//forexStrategy/pipseqStrategy_\$.ttl"
			]);
		ruleEngine = RuleEngineFactory.getInstance().getRuleEngine("test");
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

	// This is a test of RuleEngine.
	// Three sets of data are processed with selected
	// inferences syphoned off to a cross-timeframe
	// model.  A final execution produces inferences
	// for a trade signal
	@Test
	public void test() {
		for (int i=0;i<1;i++) {
			testRun();
			Map<String,ModelWrapper> map = WrapperRegistry.getInstance().getMap();
			map.each { println it }
			//ruleEngine.init();
		}
	}

//	private static Map <String,Integer> coalesceParmsMap = new HashMap<String,Integer>();
//	static {
//		coalesceParmsMap.put("m1", 5);
//		coalesceParmsMap.put("m5", 2);
//	}
	/**
	 *
	 EMA	AUD/JPY	m1	84.043	84.036	84.0643628282454	84.0777729078807
	 EMA	AUD/JPY	m5	84.045	84.036	84.1000182951217	84.1944703963245
	 RSI	AUD/JPY	m5	38.1406206220808
	 *
	 */
	private void testRun(){
		//def file = "streamSegment.txt"
		def file = "C:/work/semFxModel/fxModelDemo/bin/Debug/stream-7.txt"
		int i=100;
		new File(file).eachLine { line ->
			i++;
			def fields = line.trim().split('\t')
			if (fields.size() < 5) return;
			if (fields[0] == 'EMA' && fields[2] =='m1'){
				m1EMA(i,fields);
			}
			if (fields[0] == 'RSI' && fields[2] =='m5'){
				m5RSI(i,fields);
			}
			if (fields[0] == 'EMA' && fields[2] =='m5'){
				m5EMA(i,fields);
			}
		}
		assert trades == 107
	}
	void m1EMA(int i,def fields){
		def ask = fields[3].toDouble()
		def bid = fields[4].toDouble()
		def fast = fields[5].toDouble()
		def slow = fields[6].toDouble()
		def dt = DateTime.parse( fields[7]);
		setTime(dt)
		
		Triple.remove(ruleEngine.getModel(), "pip:RuleContext"); // Change the context!!
		String s = """

		pip:RuleContext
		rdf:type pip:RuleContextSingleton ;
		pip:hasInstrument pip:AUDJPY ;
		pip:hasTimeFrame pip:m1 ;
.

pip:EMAResult_f_${i}
  rdf:type pip:EMAResult ;
  pip:Value "${fast}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}"^^xsd:dateTime ;
.
pip:EMAResult_s_${i}
  rdf:type pip:EMAResult ;
  pip:Value "${slow}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}"^^xsd:dateTime ;
.
fxs:EMA_8_1 pip:hasIndicatorResult pip:EMAResult_f_${i}
.
fxs:EMA_34_1 pip:hasIndicatorResult pip:EMAResult_s_${i}
.
fxs:Ask_${i}
  rdf:type pip:Ask ;
  pip:Value "${ask}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}"^^xsd:dateTime ;
.
fxs:Bid_${i}
  rdf:type pip:Bid ;
  pip:Value "${bid}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}"^^xsd:dateTime ;
.
fxs:PriceFastSlowPattern_1
		pip:hasAsk	fxs:Ask_${i} ;
		pip:hasBid	fxs:Bid_${i} ;
		.
"""

		ruleEngine.insertModel(prolog + s);

		ModelWrapper mw = ruleEngine.run();
		if (ruleEngine.hasOutcomeResults()){
			trades++;
		}

	}
	void m5EMA(int i,def fields){
		def ask = fields[3].toDouble()
		def bid = fields[4].toDouble()
		def fast = fields[5].toDouble()
		def slow = fields[6].toDouble()
		def dt = DateTime.parse( fields[7]);
		setTime(dt)
		
		Triple.remove(ruleEngine.getModel(), "pip:RuleContext"); // Change the context!!

		String s = """

		pip:RuleContext
		rdf:type pip:RuleContextSingleton ;
		pip:hasInstrument pip:AUDJPY ;
		pip:hasTimeFrame pip:m5 ;
.

pip:EMAResult_f_${i}
  rdf:type pip:EMAResult ;
  pip:Value "${fast}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}"^^xsd:dateTime ;
.
pip:EMAResult_s_${i}
  rdf:type pip:EMAResult ;
  pip:Value "${slow}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}"^^xsd:dateTime ;
.
fxs:EMA_8 pip:hasIndicatorResult pip:EMAResult_f_${i}
.
fxs:EMA_34 pip:hasIndicatorResult pip:EMAResult_s_${i}
.
fxs:Ask_${i}
  rdf:type pip:Ask ;
  pip:Value "${ask}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}"^^xsd:dateTime ;
.
fxs:Bid_${i}
  rdf:type pip:Bid ;
  pip:Value "${bid}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}"^^xsd:dateTime ;
.
fxs:PriceFastSlowPattern_1
		pip:hasAsk	fxs:Ask_${i} ;
		pip:hasBid	fxs:Bid_${i} ;
		.
"""

		ruleEngine.insertModel(prolog + s);
		ModelWrapper mw = ruleEngine.run();
		if (ruleEngine.hasOutcomeResults()){
			trades++;
		}
	}
	void m5RSI(int i,def fields){
		def rsi = fields[3].toDouble()
		def dt = DateTime.parse( fields[4]);
		setTime(dt)
		
		Triple.remove(ruleEngine.getModel(), "pip:RuleContext"); // Change the context!!
		String s = """

		pip:RuleContext
		rdf:type pip:RuleContextSingleton ;
		pip:hasInstrument pip:AUDJPY ;
		pip:hasTimeFrame pip:m5 ;
.

pip:RSIResult_${i}
  rdf:type pip:RSIResult ;
  pip:Value "${rsi}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}"^^xsd:dateTime ;
.
fxs:RSI_9 pip:hasIndicatorResult pip:RSIResult_${i} ;
.
"""
		ruleEngine.insertModel(prolog + s);
		ModelWrapper mw = ruleEngine.run();
		
//		if (rsi == 27.3561496635973){
//			ruleEngine.scope();
//			println "here"
//		}
		if (ruleEngine.hasOutcomeResults()){
			trades++;
		}
	}
	

}