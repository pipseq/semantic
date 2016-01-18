/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pipseq.rdf.jena.cfg.ModelWrapper;
import org.pipseq.rdf.jena.model.Sparql;

import com.hp.hpl.jena.rdf.model.Model;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelSyphon.
 */
public class ModelSyphon {
	
	private Model sourceModel;
	private Model targetModel;
	public Model getTargetModel() {
		return targetModel;
	}

	private List<String> queries = new ArrayList<String>();
	
	/**
	 * Instantiates a new model syphon.
	 */
	public ModelSyphon(){
		
	}
	
	/**
	 * Syphon.
	 *
	 * @return the int
	 */
	public int syphon(){
		int c =0 ;
		for (String query : queries){
			Model model  = Sparql.queryDescribe(sourceModel, query);
			c += model.size();
			targetModel.add(model);
		}
		return c;
	}
	
	/**
	 * Sets the source model.
	 *
	 * @param sourceModel the new source model
	 */
	public void setSourceModel(Model sourceModel){
		this.sourceModel = sourceModel;
	}
	
	/**
	 * Sets the target model.
	 *
	 * @param targetModel the new target model
	 */
	public void setTargetModel(Model targetModel){
		this.targetModel = targetModel;
	}
	
	/**
	 * Sets the source model.
	 *
	 * @param sourceModel the new source model
	 */
	public void setSourceModel(ModelWrapper sourceModelWrapper){
		this.sourceModel = sourceModelWrapper.get();
	}
	
	/**
	 * Sets the target model.
	 *
	 * @param targetModel the new target model
	 */
	public void setTargetModel(ModelWrapper targetModelWrapper){
		this.targetModel = targetModelWrapper.get();
	}
	
	/**
	 * Adds the query.
	 *
	 * @param query the query
	 */
	public void addQuery(String query){
		queries.add(query);
	}

	/**
	 * Adds the queries.
	 *
	 * @param queries the queries
	 */
	public void addQueries(List<String> queries){
		queries.addAll(queries);
	}

	/**
	 * Adds the queries.
	 *
	 * @param queries the queries
	 */
	public void addQueries(String[] queries){
		addQueries(Arrays.asList(queries));
	}

}
