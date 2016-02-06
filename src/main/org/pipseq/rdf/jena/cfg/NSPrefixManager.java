/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.cfg;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class NSPrefixManager.
 */
public class NSPrefixManager {
	private static final Logger log = LoggerFactory.getLogger(NSPrefixManager.class);
	private static NSPrefixManager manager;
	private String domainPrefix = "pip";
	private String defaultNamespace = "http://pipseq.org/2016/01/forex#";
	private Map<String,String> prefixMap = new HashMap<String,String>();

	/**
	 * Instantiates a new rdf prefix manager.
	 */
	public NSPrefixManager(){}
	
	
	/**
	 * Gets the single instance of NSPrefixManager.
	 *
	 * @return single instance of NSPrefixManager
	 */
	public static NSPrefixManager getInstance(){
		if (manager==null){
			manager = new NSPrefixManager();
			PrefixMappingImpl.Standard.getNsPrefixMap();
			manager.setPrefixMap(PrefixMappingImpl.Standard.getNsPrefixMap());
			manager.prefixMap.put(manager.getDomainPrefix(), manager.getDefaultNamespace());
		}
		return manager;
	}
	
	/**
	 * Sets the prefix map.
	 *
	 * @param prefixMap the prefix map
	 */
	public void setPrefixMap(Map<String,String> prefixMap) {
		this.prefixMap = prefixMap;
	}

	/**
	 * Gets the prefix map.
	 *
	 * @return the prefix map
	 */
	public Map<String,String> getPrefixMap() {
		return prefixMap;
	}

	/**
	 * Gets the prefix map.
	 *
	 * @return the prefix map
	 */
	public String getPrefixFromNameSpace(String nameSpace, boolean blankPrefixOk) {
		for (String pfx : prefixMap.keySet()){
			String ns = prefixMap.get(pfx);
			if (ns.equals(nameSpace)
			&& pfx.equals("") == blankPrefixOk)
				return pfx;
		}
		return "";
	}

	/**
	 * Gets the prefix map.
	 *
	 * @return the prefix map
	 */
	public String getPrefixFromNameSpace(String nameSpace) {
		return getPrefixFromNameSpace(nameSpace, true);
	}

	/**
	 * Gets the ns prefixes from a given model.
	 * Reports conflicts
	 *
	 * @param m the new ns prefixes
	 */
	public void getNsPrefixes(Model m){
		if (m == null) return;
		Map<String,String> map = new HashMap<String,String>();
		
		for (String key : m.getNsPrefixMap().keySet()){
			if (!prefixMap.containsKey(key)){
				map.put(key, m.getNsPrefixMap().get(key));
			}
		}
		this.prefixMap.putAll(map);
	}
	
	/**
	 * Sets the ns prefixes in the given model from this map.
	 *
	 * @param m the new ns prefixes
	 */
	public void setNsPrefixes(Model m){
		if (m == null) return;
		for (String prefix : prefixMap.keySet()){	// check conflicts?
			if (m.getNsPrefixURI(prefix) == null){
				String ns = prefixMap.get(prefix);
				m.setNsPrefix(prefix, ns);
			}
		}
		for (String prefix : m.getNsPrefixMap().keySet()){	// check conflicts?
			if (!prefixMap.containsKey(prefix)){
				String ns = m.getNsPrefixURI(prefix);
				prefixMap.put(prefix, ns);
			}
		}
	}
	
	/**
	 * Sets the domain prefix.
	 *
	 * @param domainPrefix the new domain prefix
	 */
	public void setDomainPrefix(String domainPrefix) {
		this.domainPrefix = domainPrefix;
	}

	/**
	 * Gets the domain prefix.
	 *
	 * @return the domain prefix
	 */
	public String getDomainPrefix() {
		return domainPrefix;
	}

	/**
	 * Sets the default namespace.
	 *
	 * @param defaultNamespace the new default namespace
	 */
	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}

	/**
	 * Gets the default namespace.
	 *
	 * @return the default namespace
	 */
	public String getDefaultNamespace() {
		return defaultNamespace;
	}

	/**
	 * Sets the manager.
	 *
	 * @param manager the new manager
	 */
	public void setManager(NSPrefixManager manager) {
		NSPrefixManager.manager = manager;
	}

	/**
	 * Gets the manager.
	 *
	 * @return the manager
	 */
	public static NSPrefixManager getManager() {
		return manager;
	}

}
