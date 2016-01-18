package org.pipseq.spin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.pipseq.rdf.jena.aggregate.AggMethodRegistry;
import org.pipseq.rdf.jena.cfg.ModelWrapper;
import org.pipseq.rdf.jena.listener.ReleaseListener;
import org.pipseq.rdf.jena.listener.IListenerPublisherSubscriber;
import org.pipseq.rdf.jena.listener.ListenerPublisher;
import org.pipseq.rdf.jena.model.Sparql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import com.hp.hpl.jena.rdf.model.Model;

public class TwoStageRuleEngine implements IListenerPublisherSubscriber {

	private static final Logger log = LoggerFactory.getLogger(TwoStageRuleEngine.class);
	private RuleEngine ruleEngine;
	private String syphonQuery = "";
	private ModelWrapper instAbox;
	private Map<String,CircularFifoQueue<ModelWrapper>> mwMap;
	private String contextTimeframe = "";
	private List<String> listenParms = new ArrayList<String>();
	private int ModelWrapperQueueMax = 100;
	private Map <String,Integer> coalesceParmsMap = new HashMap<String,Integer>();
	
	public void setCoalesceParmsMap(Map<String, Integer> coalesceParmsMap) {
		this.coalesceParmsMap = coalesceParmsMap;
	}

	public void setModelWrapperQueueMax(int modelWrapperQueueMax) {
		ModelWrapperQueueMax = modelWrapperQueueMax;
	}

	public List<String> getListenParms() {
		return listenParms;
	}

	public void setListenParms(List<String> listenParms) {
		this.listenParms = listenParms;
	}

	public String getSyphonQuery() {
		return syphonQuery;
	}

	public void setSyphonQuery(String syphonQuery) {
		this.syphonQuery = syphonQuery;
	}

	public RuleEngine getRuleEngine() {
		return ruleEngine;
	}

	public void setRuleEngine(RuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}

	public TwoStageRuleEngine(String[] modelFiles, String syphonQuery) {
		this(Arrays.asList(modelFiles),syphonQuery);
	}

	public TwoStageRuleEngine(List<String> modelFiles, String syphonQuery) {
		//ruleEngine = new RuleEngine(modelFiles);
		this.syphonQuery = syphonQuery;
		init();	// req'd before RE processing
	}

	protected TwoStageRuleEngine() {
		// TODO Auto-generated constructor stub
	}

	// mainly a re-init
	public void init() {
		AggMethodRegistry.init();	// TODO-can this live elsewhere?
		
		instAbox = new ModelWrapper("instAbox");
		instAbox.addListener(new ReleaseListener(".*_[0-9][0-9][0-9]+$",100));
		ListenerPublisher lp = new ListenerPublisher(listenParms);

		instAbox.addListener(lp);
		lp.subscribe(this);
		ruleEngine.setAbox(instAbox);
		mwMap = new HashMap<String,CircularFifoQueue<ModelWrapper>>();
		ruleEngine.init();
	}

	public long run() {
		ModelWrapper mw = ruleEngine.run();
		if (!mwMap.containsKey(contextTimeframe)){
			mwMap.put(contextTimeframe,new CircularFifoQueue<ModelWrapper>(ModelWrapperQueueMax));
		}
		mwMap.get(contextTimeframe).add(mw);
		return mw.get().size();
	}
	
	// strategy using one rule engine w/ swapping the abox
	// could use two rule engines instead
	public ModelWrapper run2(){
		ModelWrapper preliminaryAbox = new ModelWrapper("preliminaryAbox");
		ruleEngine.setAbox(preliminaryAbox);
		ruleEngine.init();

		int i = coalesce();
		ModelWrapper conclusions = new ModelWrapper("conclusions");
		if (i>0){
			conclusions = ruleEngine.run();
		}

		ruleEngine.setAbox(instAbox);
		ruleEngine.init();
		return conclusions;
	}

	private int coalesce(){
		int infCnt = 0;
		// coalesce
		for (String tf : mwMap.keySet()){
			int cnt = coalesceParmsMap.get(tf);
			CircularFifoQueue<ModelWrapper>lmw = mwMap.get(tf);
			int size = lmw.size();
			if (size>=cnt)
			for (int j = size-1;j>size-1-cnt;j--){
				Model m = Sparql.queryDescribe(lmw.get(j).get(), syphonQuery);
				ruleEngine.getModel().add(m);
				infCnt += m.size();
			}
		}
		return infCnt;
	}

	public Model getModel() {
		return ruleEngine.getModel();
	}

	public void close() throws IOException {
		ruleEngine.close();
		
	}

	// input string TTL to model
	public void insertModel(String ttl) {
		insertModel(getModel(), ttl);
	}

	// input string TTL to model
	public static void insertModel(Model model, String ttl) {
		InputStream is = new ByteArrayInputStream((ttl).getBytes());
		model.read(is, null, "TTL");
	}

	/**
	 * Scope.
	 */
	public void scope() {
		scope(ruleEngine.getModel());
	}

	/**
	 * Scope.
	 *
	 * @param model
	 *            the model
	 */
	public static void scope(Model model) {
		org.pipseq.rdf.jena.util.SparqlScope.scope(model);
	}

	public void setDiagnostics(boolean diagnostics) {
		ruleEngine.setDiagnostics(diagnostics);
	}

	public boolean isDiagnostics() {
		// TODO Auto-generated method stub
		return ruleEngine.isDiagnostics();
	}

	@Override
	public void listen(String event) {
		this.contextTimeframe = event;
		
	}

}
