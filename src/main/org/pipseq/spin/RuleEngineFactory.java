package org.pipseq.spin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pipseq.content.ConnectorFactory;
import org.pipseq.content.FileConnector;
import org.pipseq.rdf.jena.cfg.ModelWrapper;
import org.pipseq.rdf.jena.listener.ReleaseListener;
import org.pipseq.rdf.jena.listener.ListenerPublisher;

public class RuleEngineFactory implements ITboxSource {

	private static RuleEngineFactory instance = new RuleEngineFactory();

	public static RuleEngineFactory getInstance() {
		return instance;
	}

	private Map<String, RuleEngine> map = new HashMap<String, RuleEngine>();

	private RuleEngineFactory() {
		ConnectorFactory pf = new ConnectorFactory();
		pf.setInstance(pf);
		pf.setProvider(new FileConnector());
	}
	
	public static void reset(){
		instance = new RuleEngineFactory();		
	}

	private ModelWrapper tbox = new ModelWrapper("tbox");

	public RuleEngine getRuleEngine(String name) {
		RuleEngine re = null;
		if (!map.containsKey(name)) {
			ModelWrapper abox = new ModelWrapper("abox-"+name);
			re = new RuleEngine(name);
			this.map.put(name, re);
			re.setAbox(abox);
			re.setTboxSource(this);
			re.init();
		}
		re = map.get(name);
		return re;
	}

	public void setModelFiles(String[] modelFiles) {
		setModelFiles(Arrays.asList(modelFiles));
	}

	public void setModelFiles(List<String> modelFiles) {
		tbox.setModelFiles(modelFiles);
	}

	@Override
	public ModelWrapper getTbox() {
		// TODO Auto-generated method stub
		return tbox;
	}
}
