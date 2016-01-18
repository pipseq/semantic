/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.cfg;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pipseq.content.ConnectorFactory;
import org.pipseq.content.IConnector;
import org.pipseq.rdf.jena.listener.ReleaseListener;
import org.pipseq.rdf.jena.listener.LoggingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelWrapper.
 */
public class ModelWrapper implements IWrapperAccept {
	private static final Logger log = LoggerFactory.getLogger(ModelWrapper.class);

	private Model model;
	private String saveFile;
	private String modelName;
	private boolean autoConfigure=true;
	private List<String> modelFiles = new ArrayList<String>();
	private List<ModelChangedListener> listeners = new ArrayList<ModelChangedListener>();
	private LoggingListener loggingListener;
	private Map<Model,List<ModelChangedListener>> mapRegisteredListeners = new HashMap<Model,List<ModelChangedListener>>();
	private boolean load;
	
	public LoggingListener getLoggingListener() {
		return loggingListener;
	}

	public void setLoggingListener(LoggingListener loggingListener) {
		this.loggingListener = loggingListener;
	}

	/**
	 * Gets the listeners.
	 *
	 * @return the listeners
	 */
	public List<ModelChangedListener> getListeners() {
		return listeners;
	}

	/**
	 * Sets the listeners.
	 *
	 * @param listeners the new listeners
	 */
	public void setListeners(List<ModelChangedListener> listeners) {
		this.listeners = listeners;
	}

	public void addListeners(List<ModelChangedListener> listeners) {
		this.listeners.addAll(listeners);
	}

	public void addListener(ModelChangedListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Instantiates a new rdf model resource.
	 *
	 * @param modelName the model name
	 */
	public ModelWrapper(String modelName) {
		this.modelName = modelName;
		WrapperRegistry.getInstance().put(this);
		loggingListener = new LoggingListener(modelName, 
				WrapperRegistry.getInstance().getDefaultLoggingLevel());
		addListener(loggingListener);
	}

	/**
	 * Instantiates a new rdf model resource.
	 */
	public ModelWrapper() {
		this("unknown");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		if (this instanceof BoxWrapper){
			ModelWrapper mwa = ((BoxWrapper)this).abox;
			ModelWrapper mwt = ((BoxWrapper)this).tbox;
			Model ma = mwa.get();
			Model mt = mwt.get();
			long ca = 0;
			long ct = 0;
			if (ma!=null)
				ca = ma.size();
			if (mt!=null)
				ct = mt.size();
			return this.getClass().getName()
					+" abox={"+mwa.getModelName()+" ("+ca+") "+modelFiles +"},"
					+" tbox={"+mwt.getModelName()+" ("+ct+") "+modelFiles +"}"
					;
		}else {
		Model m = get();
		long c = 0;
		if (m!=null)
			c = m.size();
		return this.getClass().getName()+" ("+c+") "+modelFiles;
		}
	}
	
	public long size(){
		return getModel().size();
	}
	
	/**
	 * Checks if is model initialized.
	 *
	 * @return true, if is model initialized
	 */
	protected boolean isModelInitialized(){
		return model!=null;
	}
	
	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	protected Model getModel() {
		if (model==null){
			model = ModelFactory.createDefaultModel();
			configure(model);
			configureListeners(model);
		}
		return model;
	}

	/**
	 * Gets the.
	 *
	 * @return the model
	 */
	public Model get() {
		return getModel();
	}

	/**
	 * Gets the.
	 *
	 * @param context the context
	 * @return the model
	 * @throws Exception the exception
	 */
	public Model get(Object context) throws Exception{
		return get();
	}

	/**
	 * Sets the model.
	 *
	 * @param model the new model
	 */
	public void setModel(Model model) {
		this.model = model;
		configure(model);
	}

	/**
	 * Read.
	 */
	public void read() {
		read(getModel());
	}

	private boolean read;
	
	/**
	 * Checks if is read.
	 *
	 * @return true, if is read
	 */
	public boolean isRead() {
		return read;
	}

	/**
	 * Sets the read.
	 *
	 * @param read the new read
	 */
	public void setRead(boolean read) {
		this.read = read;
	}

	/**
	 * Read.
	 *
	 * @param model the model
	 */
	public void read(Model model) {
		if (isAutoConfigure()){
			if (isRead()) return;
			unconfigureListeners(model);
		}
		for (String modelFile : modelFiles){
			IConnector connector = ConnectorFactory.getInstance().getProvider();
			try {
				InputStream is = connector.getInputStream(modelFile);
				if (is==null){
					//log.warn("File cannot be read, "+modelFile);
					//return;
					throw new Exception("file cannot be found");
				}
				model.read(is,null, getModelType(modelFile));
				
				NSPrefixManager.getInstance().setNsPrefixes(model);
			} catch (Exception e) {
				throw new RuntimeException("Error reading model file, "+modelFile, e);
			} finally {
				connector.close();
			}
		}
		if (isAutoConfigure()){
			configureListeners(model);
			setRead(true);
		}
	}

	/**
	 * Write.
	 */
	public void write() {
		write(getModel());
	}
	
	/**
	 * Write.
	 *
	 * @param model the model
	 */
	public void write(Model model) {
		if (saveFile != null) {
			IConnector connector = ConnectorFactory.getInstance().getProvider();
			try {
				OutputStream os = connector.getOutputStream(saveFile);
				if (os==null){
					log.warn("Cannot write to file, "+saveFile);
					return;
				}
				model.write(os, getModelType(saveFile));
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				connector.close();
			}
		}
	}
	
	/**
	 * Gets the model type.
	 *
	 * @param file the file
	 * @return the model type
	 */
	protected static String getModelType(String file){
		if (file.toLowerCase().endsWith("ttl")) return "TTL";
		if (file.toLowerCase().endsWith("nt")) return "N-TRIPLE";
		if (file.toLowerCase().endsWith("n3")) return "N3";
		if (file.toLowerCase().endsWith("owl")) return "RDF/XML";
		if (file.toLowerCase().endsWith("xml")) return "RDF/XML";
		if (file.toLowerCase().endsWith("rdf")) return "RDF/XML";
		
		return "RDF";
	}

	/**
	 * Configure.
	 *
	 * @param model the model
	 */
	protected void configure(Model model){
		
		NSPrefixManager.getInstance().setNsPrefixes(model);
	}
	
	/**
	 * Configure listeners.
	 *
	 * @param model the model
	 */
	protected void configureListeners(Model model){
		
		List<ModelChangedListener> registeredListeners = null;
		
		if (!mapRegisteredListeners.containsKey(model)){
			registeredListeners = new ArrayList<ModelChangedListener>();
			mapRegisteredListeners.put(model, registeredListeners);
		} else {
			registeredListeners = mapRegisteredListeners.get(model);
		}

		for (ModelChangedListener mcl : listeners){
			if (!registeredListeners.contains(mcl)){
				model.register(mcl);
				registeredListeners.add(mcl);
				log.debug("adding listener "+mcl+" model= "+this);
			}
		}
	}
	
	/**
	 * Unconfigure listeners.
	 *
	 * @param model the model
	 */
	protected void unconfigureListeners(Model model){
		List<ModelChangedListener> registeredListeners = null;
		
		if (!mapRegisteredListeners.containsKey(model)){
			return;
		} else {
			registeredListeners = mapRegisteredListeners.get(model);
		}

		for (ModelChangedListener mcl : listeners){
			if (registeredListeners.contains(mcl)){
				model.unregister(mcl);
				registeredListeners.remove(mcl);
				log.debug("removing listener "+mcl+" model= "+this);
			}
		}
	}
	
	/**
	 * Gets the save file.
	 *
	 * @return the save file
	 */
	public String getSaveFile() {
		return saveFile;
	}

	/**
	 * Sets the save file.
	 *
	 * @param saveFile the new save file
	 */
	public void setSaveFile(String saveFile) {
		this.saveFile = saveFile;
	}

	/**
	 * Gets the model files.
	 *
	 * @return the model files
	 */
	public List<String> getModelFiles() {
		return modelFiles;
	}

	/**
	 * Sets the model files.
	 *
	 * @param modelFiles the new model files
	 */
	public void setModelFiles(List<String> modelFiles) {
		this.modelFiles = modelFiles;
		read();
	}

	/**
	 * Gets the model name.
	 *
	 * @return the model name
	 */
	/* (non-Javadoc)
	 * @see org.pipseq.config.ModelResource#getModelName()
	 */
	private static int modelNameCnt=0;
	public String getModelName() {
		if (modelName == null){
			modelName = this.getClass().getSimpleName()+"_"+modelNameCnt++;
		}
		return modelName;
	}
	
	/**
	 * Sets the model name.
	 *
	 * @param modelName the new model name
	 */
	/* (non-Javadoc)
	 * @see org.pipseq.config.ModelResource#setModelName(java.lang.String)
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * Inits the.
	 */
	public void init(){
		if (load){
			read();
		}
		if (isAutoConfigure()){
			configureListeners(getModel());
		}
		log.debug(""+getModelName()+" initialized. "+getModel().size()+" triples.");
	}
	
	/**
	 * Sets the load.
	 *
	 * @param load the new load
	 */
	public void setLoad(boolean load) {
		this.load = load;
	}

	/**
	 * Checks if is load.
	 *
	 * @return true, if is load
	 */
	public boolean isLoad() {
		return load;
	}
	
	/**
	 * Close.
	 */
	public void close(){
		log.debug(""+getModelName()+" closing. "+getModel().size()+" triples.");
		getModel().close();
		for (ModelChangedListener mcl : listeners){
			if (mcl instanceof Closeable)
				try {
					((Closeable)mcl).close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Sets the auto configure.
	 *
	 * @param autoConfigure the new auto configure
	 */
	public void setAutoConfigure(boolean autoConfigure) {
		this.autoConfigure = autoConfigure;
	}

	/**
	 * Checks if is auto configure.
	 *
	 * @return true, if is auto configure
	 */
	public boolean isAutoConfigure() {
		return autoConfigure;
	}

	/**
	 * Begin.
	 */
	public void begin(){
		Model m = get();
		if (m.supportsTransactions()){
			m.begin();
		}
	}

	/**
	 * Commit.
	 */
	public void commit(){
		Model m = get();
		if (m.supportsTransactions()){
			m.commit();
		}
	}

	/**
	 * Abort.
	 */
	public void abort(){
		Model m = get();
		if (m.supportsTransactions()){
			m.abort();
		}
	}

	/* (non-Javadoc)
	 * @see org.pipseq.rdf.jena.cfg.IWrapperAccept#accept(org.pipseq.rdf.jena.cfg.WrapperVisitor)
	 */
	public void accept(WrapperVisitor visitor) {
		visitor.visit(this);
	}

}
