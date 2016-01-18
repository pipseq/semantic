package org.pipseq.rdf.jena.cfg;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.hp.hpl.jena.rdf.model.Model;

public class WrapperRegistry {

	private static final Logger log = LoggerFactory.getLogger(WrapperRegistry.class);
	private static WrapperRegistry registry = new WrapperRegistry();
	private Map<String,ModelWrapper> map=new WeakHashMap<String,ModelWrapper>();
	private int defaultLoggingLevel = 0;

	private WrapperRegistry(){}

	public static WrapperRegistry getInstance(){
		if (registry==null){
			String err = "registry not configured";
			log.error(err); 
			throw new RuntimeException(err);
		}
		return registry;
	}
	
	public Map<String, ModelWrapper> getMap() {
		return map;
	}
	
	public void put(ModelWrapper mw){
		getMap().put(mw.getModelName(),mw);
	}
	
	public void setMap(Map<String, ModelWrapper> map) {
		this.map = map;
	}
	
	public ModelWrapper getModelResource(String name){
		return map.get(name);
	}
	
	/**
	 * Get a model given its name
	 * @param name
	 * @return
	 */
	public Model getModel(String name){
		Model m=map.get(name).get();
		return m;
	}
	
	/**
	 * Get a model given its name and context
	 * @param name
	 * @param obj
	 * @return
	 * @throws Exception 
	 */
	public Model getModel(String name, Object obj) throws Exception{
		Model m=map.get(name).get(obj);
		return m;
	}
	
	// logging
	public void setLoggingLevel(int level){
		for (String key : map.keySet()){
			setLoggingLevel(key, level);
		}
	}
	
	public void setLoggingLevel(String name, int level){
		ModelWrapper mw = map.get(name);
		mw.getLoggingListener().setLoggingLevel(level);
	}
	
	public int getDefaultLoggingLevel() {
		return defaultLoggingLevel;
	}

	public void setDefaultLoggingLevel(int level){
		defaultLoggingLevel = level;
	}
}
