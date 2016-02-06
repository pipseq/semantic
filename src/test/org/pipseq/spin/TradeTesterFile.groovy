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
public class TradeTesterFile {

	private static final Logger log = LoggerFactory.getLogger(TradeTesterFile.class);
	static String prolog = """
@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
@prefix fxs: <http://pipseq.org/2016/01/fx/strategy#> .
@prefix pip: <http://pipseq.org/2016/01/forex#> .
@prefix owl:     <http://www.w3.org/2002/07/owl#> .
@prefix xsd:     <http://www.w3.org/2001/XMLSchema#> .
@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix fn:		<http://www.w3.org/2005/xpath-functions#> .

"""

	@After
	public void shutdown(){
		ruleEngine.close();
	}

	@Before
	public void setup() {
		WrapperRegistry.getInstance().setDefaultLoggingLevel(2);
		RuleEngineFactory.getInstance().setListenParms(
			["http://pipseq.org/2016/01/forex#RuleContext","http://pipseq.org/2016/01/forex#hasTimeFrame"]);
		RuleEngineFactory.getInstance().setModelFiles(
			[
			"C:/work/semFxModel/var/models/pipseq.org/2016/01/forex.ttl",
			"C:/work/semFxModel/var/models/pipseq.org/2016/01/fx/strategy.ttl"
			]);
		RuleEngineFactory.getInstance().setSyphonQuery("""
		describe ?s {
			?s a <http://pipseq.org/2016/01/forex#TradeRecommendation> .
		}
		""");
		ruleEngine = RuleEngineFactory.getInstance().getRuleEngine("test");
		ruleEngine.init();
		ruleEngine.setDiagnostics(false);
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
			ruleEngine.init();
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
		def file = "stream.txt"
		//def file = "C:/work/semFxModel/fxModelDemo/bin/Debug/stream.txt"
		int i=100;
		new File(file).eachLine { line ->
			i++;
			def fields = line.trim().split('\t')
			if (fields.size() < 5) return;
			if (fields[0] == 'EMA' && fields[2] =='m1'){
				m1EMA(i,fields);
			}
			ModelWrapper conclusions = null;
			if (fields[0] == 'RSI' && fields[2] =='m5'){
				m5RSI(i,fields);
				conclusions = ruleEngine.run2();
			}
			if (fields[0] == 'EMA' && fields[2] =='m5'){
				m5EMA(i,fields);
				conclusions = ruleEngine.run2();
			}
			if (conclusions != null
				&& conclusions.get().size() == 3){
				log.debug("Trade!!!");
			}
		}
	}
	void m1EMA(int i,def fields){
		def ask = fields[3].toDouble()
		def bid = fields[4].toDouble()
		def fast = fields[5].toDouble()
		def slow = fields[6].toDouble()
		def dt = DateTime.parse( fields[7]);

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
  pip:TimeStamp "${dt.toISOString()}" ;
.
pip:EMAResult_s_${i}
  rdf:type pip:EMAResult ;
  pip:Value "${slow}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}" ;
.
fxs:EMA_8_1 pip:hasIndicatorResult pip:EMAResult_f_${i}
.
fxs:EMA_34_1 pip:hasIndicatorResult pip:EMAResult_s_${i}
.
fxs:Ask_${i}
  rdf:type pip:Ask ;
  pip:Value "${ask}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}" ;
.
fxs:Bid_${i}
  rdf:type pip:Bid ;
  pip:Value "${bid}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}" ;
.
fxs:PriceFastSlowPattern_1
		pip:hasAsk	fxs:Ask_${i} ;
		pip:hasBid	fxs:Bid_${i} ;
		.
"""

		ruleEngine.insertModel(prolog + s);

		long c = ruleEngine.run();
		if (c>0){
			log.debug("m1EMA inferences="+c);
		}
//		if (mw.get().size()>0){
//			//						println "${i}-EMA, m1, ${ask}, ${bid}, ${fast}, ${slow}, ${dt.toISOString()}"
//			//						println "c=${mw.get().size()}"
//		}

	}
	void m5EMA(int i,def fields){
		def ask = fields[3].toDouble()
		def bid = fields[4].toDouble()
		def fast = fields[5].toDouble()
		def slow = fields[6].toDouble()
		def dt = DateTime.parse( fields[7]);

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
  pip:TimeStamp "${dt.toISOString()}" ;
.
pip:EMAResult_s_${i}
  rdf:type pip:EMAResult ;
  pip:Value "${slow}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}" ;
.
fxs:EMA_8 pip:hasIndicatorResult pip:EMAResult_f_${i}
.
fxs:EMA_34 pip:hasIndicatorResult pip:EMAResult_s_${i}
.
fxs:Ask_${i}
  rdf:type pip:Ask ;
  pip:Value "${ask}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}" ;
.
fxs:Bid_${i}
  rdf:type pip:Bid ;
  pip:Value "${bid}"^^xsd:float ;
  pip:TimeStamp "${dt.toISOString()}" ;
.
fxs:PriceFastSlowPattern_1
		pip:hasAsk	fxs:Ask_${i} ;
		pip:hasBid	fxs:Bid_${i} ;
		.
"""

		ruleEngine.insertModel(prolog + s);
		long c = ruleEngine.run();
		if (c>0){
			log.debug("m5EMA inferences="+c);
		}
//		if (mw.get().size()>0){
//			//		println "${i}-EMA, m5, ${ask}, ${bid}, ${fast}, ${slow}, ${dt.toISOString()}"
//			//		println "c=${c}"
//		}
	}
	void m5RSI(int i,def fields){
		def rsi = fields[3].toDouble()
		def dt = DateTime.parse( fields[4]);

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
  pip:TimeStamp "${dt.toISOString()}" ;
.
fxs:RSI_9 pip:hasIndicatorResult pip:RSIResult_${i} ;
.
"""
		ruleEngine.insertModel(prolog + s);
		long c = ruleEngine.run();
		if (c>0){
			log.debug("m5RSI inferences="+c);
		}
	}

}