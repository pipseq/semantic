package org.pipseq.rdf.jena.listener;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pipseq.spin.RuleEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.listeners.StatementListener;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class ListenerMetrics extends StatementListener implements Closeable {
	
	private static final Logger log = LoggerFactory.getLogger(ListenerMetrics.class);
	//List<IListenerPublisherSubscriber> list = new ArrayList<IListenerPublisherSubscriber>();
	private String name="n/a";
	private Map<String,Integer> map = new HashMap<String,Integer>();
	public ListenerMetrics(String name){
		this.name = name;
	}
	
	private void put(String key){
		int value = 1;
		if (!map.containsKey(key)){
			map.put(key, value);
		} else {
			int v = map.get(key);
			v += value;
			map.put(key, v);
		}
	}
	
	private void putType(String key, Statement s){
//		String sname = s.getURI();
		Property p = s.getPredicate();
		String pname = p.getLocalName();
		RDFNode rdf = s.getObject();
		if (pname.equalsIgnoreCase("type")){
			put(key + " "+ rdf.toString());
		}
	}
	/*
	(non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.listeners.StatementListener#addedStatement(com.hp.hpl.jena.rdf.model.Statement)
	 */
	@Override
	public void addedStatement(Statement s){
		//log.debug("?-"+s);
		Resource sub = s.getSubject();
		if (sub.isAnon()) {
			put("ADD blank");
			putType("ADD blank",s);
			return;
		}
		putType("ADD",s);
		put("ADD");
	}
	
	@Override
	public void removedStatement(Statement s){
		Resource sub = s.getSubject();
		if (sub.isAnon()) {
			put("DEL blank");
			putType("DEL blank",s);
			return;
		}
		putType("DEL",s);
		put("DEL");
	}
	
	public String dump(){
		String fmt = "%-34s %5d\n";
		List<String> list = new ArrayList<String>();
		Map<String,Integer> difMap = new HashMap<String,Integer>();
		for (String key : map.keySet()){
			int sum = map.get(key);
			list.add(String.format(fmt,getName(key),sum));
			String key2 = key.substring(3);
			if (key.startsWith("ADD")){
				if (!difMap.containsKey(key2))
					difMap.put(key2,map.get(key));
				else {
					int v = difMap.get(key2);
					v += sum;
					difMap.put(key2, v);
				}
			}
			if (key.startsWith("DEL")){
				if (!difMap.containsKey(key2))
					difMap.put(key2,-map.get(key));
				else {
					int v = difMap.get(key2);
					v -= sum;
					difMap.put(key2, v);
				}
			}
		}
		for (String k2 : difMap.keySet()){
			String n = getName(k2);
			list.add(String.format(fmt,"dif"+n,difMap.get(k2)));
		}
		Collections.sort(list);
		StringBuffer sb = new StringBuffer();
		sb.append("TPL STATS\n");
		for (String s : list){
			sb.append(s);
		}
		return sb.toString();
	}
	
	String getName(String s0){
		String[] sf = s0.split(" ");
		String sb = "";
		for (String s : sf){
			if (s.contains("#"))
				sb += s.substring(s.indexOf('#')+1);
			else sb += s;
			sb += " ";
		}
		return sb;
	}

	@Override
	public void close() throws IOException {
		log.debug(dump());
	}

}
