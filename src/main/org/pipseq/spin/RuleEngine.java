/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.spin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.io.Closeable;

import org.pipseq.common.DateTime;
import org.pipseq.content.ConnectorFactory;
import org.pipseq.content.FileConnector;
import org.pipseq.rdf.jena.aggregate.AggMethodRegistry;
import org.pipseq.rdf.jena.cfg.ModelWrapper;
import org.pipseq.rdf.jena.cfg.OntoBoxWrapper;
import org.pipseq.rdf.jena.listener.ReleaseListener;
import org.pipseq.rdf.jena.listener.ReleaseBlankAndPredicateListener;
import org.pipseq.rdf.jena.listener.ReleasePredicateListener;
import org.pipseq.rdf.jena.listener.ListenerMetrics;
import org.pipseq.rdf.jena.listener.ListenerPublisher;
import org.pipseq.rdf.jena.model.Sparql;
import org.pipseq.spin.compare.RestrictionSPINRuleComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.spin.inference.SPINExplanations;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.inference.SPINRuleComparator;
import org.topbraid.spin.progress.ProgressMonitor;
import org.topbraid.spin.statistics.SPINStatistics;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.CommandWrapper;
import org.topbraid.spin.util.SPINQueryFinder;
import org.topbraid.spin.vocabulary.SPIN;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

// TODO: Auto-generated Javadoc
/**
 * The Class RuleEngine.
 * 
 * This rule engine performs inferencing collecting
 * inferences in an inference model. The model is returned
 * to the client.
 * 
 */
public class RuleEngine implements Closeable {

	private static final Logger log = LoggerFactory.getLogger(RuleEngine.class);
	public boolean Diagnostics = false;
	//private ModelWrapper tbox = new ModelWrapper("tbox");
	ITboxSource tboxSource;
	private ModelWrapper abox;
	private OntoBoxWrapper ontBoxWrapper;
	private String feedbackQuery;
	private String outcomeQuery;
	private boolean outcomeResults;
	private ProgressMonitor monitor = new SpinMonitor();
	
	public boolean hasOutcomeResults() {
		return outcomeResults;
	}

	public ITboxSource getTboxSource() {
		return tboxSource;
	}

	public void setTboxSource(ITboxSource tboxSource) {
		this.tboxSource = tboxSource;
	}

	public boolean isDiagnostics() {
		return Diagnostics;
	}

	public void setDiagnostics(boolean diagnostics) {
		Diagnostics = diagnostics;
	}

	public ModelWrapper getTbox() {
		return getTboxSource().getTbox();
	}

	public ModelWrapper getAbox() {
		if (abox == null){
			abox = new ModelWrapper("abox");
		}
		return abox;
	}

	public void setAbox(ModelWrapper abox) {
		this.abox = abox;
	}

	private String name;
	/**
	 * Instantiates a new setup.
	 */
	protected RuleEngine(String name) {
		this.name = name;
		// Initialize system functions and templates
		SPINModuleRegistry.get().init();
		AggMethodRegistry.init();	// TODO-can this live elsewhere?
	}

	/* (non-Javadoc)
	 * @see org.pipseq.spin.IRuleEngine#init()
	 */
	public void init() {
		
		getAbox().addListener(new ReleaseListener(".*_[0-9][0-9][0-9]+$",100));	//TODO-review this!
		getAbox().addListener(new ReleaseBlankAndPredicateListener("TimeStamp",20));	//TODO-review this!
		getAbox().addListener(new ReleasePredicateListener("hasIndicatorResult",40));	//TODO-review this!
		getAbox().addListener(new ReleasePredicateListener("hasAsk",40));	//TODO-review this!
		getAbox().addListener(new ReleasePredicateListener("hasBid",40));	//TODO-review this!
		//abox.addListener(new ReleaseListener(".*",1500));	//TODO-review this!
		getAbox().addListener(new ListenerMetrics("abox-"+name));
		ontBoxWrapper = new OntoBoxWrapper("ontoBox-"+name,getAbox(), getTboxSource().getTbox());
		ontBoxWrapper.init();
		
		// Register locally defined functions
		SPINModuleRegistry.get().registerAll(ontBoxWrapper.get(), null);

		log.debug("initialized: "+name);
	}
	
	ModelWrapper nuTrpls;
	ModelWrapper getLastInferences(){
		return nuTrpls;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.spin.IRuleEngine#run()
	 */
	public ModelWrapper run() {
		
		try {
			// Create and add Model for inferred triples
			nuTrpls = new ModelWrapper("newTriples");
			//Model newTriples = ModelFactory.createDefaultModel();
			ontBoxWrapper.get().addSubModel(nuTrpls.get());

			// Run all inferences
			SPINExplanations explain = new SPINExplanations();
			List<SPINStatistics> lstat = new ArrayList<SPINStatistics>();

			// diagnostics
			if (Diagnostics) {
				log.debug("Now="+DateTime.now().toISOString());
				run(ontBoxWrapper.get(), nuTrpls.get(), explain, lstat, true/* single pass */, monitor);
				for (StmtIterator si = nuTrpls.get().listStatements(); si.hasNext();){
					Statement st = si.nextStatement();
					Triple t = new Triple(st.getSubject().asNode(), st.getPredicate().asNode(), st.getObject().asNode());
					log.debug("explain: "+explain.getClass(t) + " for "
							+(t.getSubject().isBlank()
									? "_:b"+t.getSubject().getBlankNodeLabel()
									//? "_:b"+t.getSubject().getBlankNodeLabel().hashCode()
									: t.getSubject().getLocalName())  + " "
							+t.getPredicate().getLocalName() + " "
							+t.getObject() + " "
					);
				}
				for (SPINStatistics ss : lstat){
					log.debug("spinStat: "+ ss.getLabel()+" duration="+ss.getDuration()+" context="+ss.getContext());
				}
			} else {
				run(ontBoxWrapper.get(), nuTrpls.get(), null, null, true/* single pass */, null);
			}

			ontBoxWrapper.get().removeSubModel(nuTrpls.get()); // cleanup
			
			// feedback
			if (getFeedbackQuery() != null){
				Model m = Sparql.queryDescribe(nuTrpls.get(), getFeedbackQuery());
				getModel().add(m);
			}

			// outcome
			if (getOutcomeQuery() != null){
				Model m = Sparql.queryDescribe(nuTrpls.get(), getOutcomeQuery());
				//getModel().add(m);	// TODO - feedback too?
				outcomeResults = m.size() > 0;
			}
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			e.printStackTrace(ps);
			String err = e.getMessage()+", "+baos.toString();
			log.error(err);
			throw new RuntimeException(err);
		}

		return nuTrpls;
	}

	/**
	 * Run.
	 *
	 * @param queryModel
	 *            the query model
	 * @param newTriples
	 *            the new triples
	 * @param explanations
	 *            the explanations
	 * @param statistics
	 *            the statistics
	 * @param singlePass
	 *            the single pass
	 * @param monitor
	 *            the monitor
	 * @return the int
	 */
	// providing own implementation mostly to roll own comparator
	public static int run(Model queryModel, Model newTriples,
			SPINExplanations explanations, List<SPINStatistics> statistics,
			boolean singlePass, ProgressMonitor monitor) {
		Map<Resource, List<CommandWrapper>> cls2Query = SPINQueryFinder
				.getClass2QueryMap(queryModel, queryModel, SPIN.rule, true,
						false);
		Map<Resource, List<CommandWrapper>> cls2Constructor = SPINQueryFinder
				.getClass2QueryMap(queryModel, queryModel, SPIN.constructor,
						true, false);
		SPINRuleComparator comparator = new RestrictionSPINRuleComparator(
				queryModel);
		return SPINInferences.run(queryModel, newTriples, cls2Query,
				cls2Constructor, explanations, statistics, singlePass,
				SPIN.rule, comparator, monitor);
	}

	/* (non-Javadoc)
	 * @see org.pipseq.spin.IRuleEngine#getModel()
	 */
	public Model getModel() {
		return ontBoxWrapper.get();
	}

	/* (non-Javadoc)
	 * @see org.pipseq.spin.IRuleEngine#getModel()
	 */
	public ModelWrapper getModelWrapper() {
		return ontBoxWrapper;
	}


	/* (non-Javadoc)
	 * @see org.pipseq.spin.IRuleEngine#close()
	 */
	@Override
	public void close() throws IOException {
		// tbox owned by RuleEngineFactory
		getAbox().close(); 
		log.debug(""+name+" close");
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
		scope(getModel());
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

	public String getFeedbackQuery() {
		return feedbackQuery;
	}

	public void setFeedbackQuery(String feedbackQuery) {
		this.feedbackQuery = feedbackQuery;
	}

	public String getOutcomeQuery() {
		return outcomeQuery;
	}

	public void setOutcomeQuery(String outcomeQuery) {
		this.outcomeQuery = outcomeQuery;
	}

}
