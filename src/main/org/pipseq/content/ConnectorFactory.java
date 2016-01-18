/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.content;


// TODO: Auto-generated Javadoc
/**
 * A factory for creating Provider objects.
 */
public class ConnectorFactory {

	private static ConnectorFactory factory;
	
	/**
	 * Instantiates a new connector factory.
	 */
	public ConnectorFactory(){}
	
	/**
	 * Sets the connector.
	 *
	 * @param connector the new connector
	 */
	public void setProvider(IConnector connector) {
		this.connector = connector;
	}
	private IConnector connector;
	
	/**
	 * Gets the single instance of ConnectorFactory.
	 *
	 * @return single instance of ConnectorFactory
	 */
	public static ConnectorFactory getInstance(){
		if (factory == null){
			throw new RuntimeException("ConnectorFactory not configured");
		}
		return factory; 
	}
	
	/**
	 * Sets the instance.
	 *
	 * @param f the new instance
	 */
	public void setInstance(ConnectorFactory f){
		this.factory = f;
	}
	
	/**
	 * Gets the connector.
	 *
	 * @return the connector
	 */
	public IConnector getProvider(){
		if (connector == null){
			connector = new FileConnector();	// default w/ no config available
		}
		return connector.clone();
	}
	
}
