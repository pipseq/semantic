package examples;

import org.junit.Test;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.PrintUtil;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

public class TestPips0 {

	@Test
	public void test() {

	while (true){
		run();
	}
}


	public void run() {

		String uri = "http://pipseq.org#";
		PrintUtil.registerPrefix("pip", uri);
		Model baseModel = FileManager.get().loadModel("file:data/pipseq.ttl");
		// Initialize system functions and templates
		SPINModuleRegistry.get().init();

		// Create OntModel with imports
		OntModel ontModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_MEM,baseModel);
		
		// Create and add Model for inferred triples
		Model newTriples = ModelFactory.createDefaultModel();
		ontModel.addSubModel(newTriples);

		// Register locally defined functions
		SPINModuleRegistry.get().registerAll(ontModel, null);

		// Run all inferences
		SPINInferences.run(ontModel, newTriples, null, null, true, null);
		System.out.println("Inferred triples: " + newTriples.size());

		//org.pipseq.rdf.jena.util.SparqlScope.scope(ontModel);
		newTriples.write(System.out, "TTL");
	}
	
	public void run0() {

		String uri = "http://pipseq.org#";
		PrintUtil.registerPrefix("pip", uri);
		Model m0 = FileManager.get().loadModel("file:data/pip.ttl");

		// Create an instance of such a reasoner
		Resource configuration = m0.createResource();
		configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
		configuration.addProperty(ReasonerVocabulary.PROPruleSet,
				"data/pip.rules");
		Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(
				configuration);

		// derive
		InfModel inf = ModelFactory.createInfModel(reasoner, m0);

		//org.pipseq.rdf.jena.util.SparqlScope.scope(inf);
		inf.write(System.out, "TTL");
	}
}
