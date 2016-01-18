package org.pipseq.rdf.jena.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hp.hpl.jena.rdf.listeners.StatementListener;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class ListenerPublisher extends StatementListener {
	
	List<IListenerPublisherSubscriber> list = new ArrayList<IListenerPublisherSubscriber>();

	private String subjectURI; 
	private String propertyURI; 
	public ListenerPublisher(String subjectURI, String propertyURI){
		this.subjectURI = subjectURI;
		this.propertyURI = propertyURI;
	}
	// could support other conventions (lists of lists?)
	public ListenerPublisher(List<String> listURIs){
		if (listURIs == null
				|| listURIs.size() < 2)
			throw new RuntimeException("Insufficient ListenerPublisher() params");
		this.subjectURI = listURIs.get(0);	// by convention
		this.propertyURI = listURIs.get(1);
	}
	public ListenerPublisher(String[] listURIs){
		this(Arrays.asList(listURIs));
	}
	/*
	 * context listening
 		pip:RuleContext pip:hasTimeFrame pip:m1 ;
 		w/ FQNs
	 */
	/*
	(non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.listeners.StatementListener#addedStatement(com.hp.hpl.jena.rdf.model.Statement)
	 */
	@Override
	public void addedStatement(Statement s){
		Resource sub = s.getSubject();
		if (sub.isAnon()) return;
		String sname = sub.getURI();
		Property p = s.getPredicate();
		String pname = p.getURI();
		RDFNode rdf = s.getObject();
		if (subjectURI.equals(sname)
				&& propertyURI.equals(pname)){
			String value = rdf.asResource().getLocalName();
			for (IListenerPublisherSubscriber subscriber : list) {
				subscriber.listen(value);
			}
		}
	}
	
	@Override
	public void removedStatement(Statement s){
		Resource sub = s.getSubject();
		if (sub.isAnon()) return;
		String sname = sub.getURI();
		Property p = s.getPredicate();
		String pname = p.getURI();
		RDFNode rdf = s.getObject();
		if (subjectURI.equals(sname)
				&& propertyURI.equals(pname)){
			String value = rdf.asResource().getLocalName();
			for (IListenerPublisherSubscriber subscriber : list) {
				subscriber.listen("");
			}
		}
	}
	
	public void subscribe(IListenerPublisherSubscriber subscriber){
		list.add(subscriber);
	}

}
