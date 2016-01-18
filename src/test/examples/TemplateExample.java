package examples;

import org.topbraid.spin.arq.ARQ2SPIN;
import org.topbraid.spin.arq.ARQFactory;
import org.topbraid.spin.model.Query;
import org.topbraid.spin.model.Select;
import org.topbraid.spin.model.Template;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.util.SystemTriples;
import org.topbraid.spin.vocabulary.ARG;
import org.topbraid.spin.vocabulary.SPIN;
import org.topbraid.spin.vocabulary.SPL;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


/**
 * Creates a SPIN template and "calls" it.
 * 
 * @author Holger Knublauch
 */
public class TemplateExample {
	
	private static final String NS = "http://example.org/model#";
	
	private static final String PREFIX = "ex";
	
	// Sparql of the template - argument will be arg:predicate
	private static final String QUERY =
		"SELECT *\n" +
		"WHERE {\n" +
		"    owl:Thing ?predicate ?object .\n" +
		"}";

	
	public static void main(String[] args) {
		
		// Initialize system functions and templates
		SPINModuleRegistry.get().init();

		// Create main model
		Model model = JenaUtil.createDefaultModel();
		JenaUtil.initNamespaces(model.getGraph());
		model.add(SystemTriples.getVocabularyModel()); // Add some queryable triples
		model.setNsPrefix(PREFIX, NS);
		model.setNsPrefix(ARG.PREFIX, ARG.NS);
		
		// Create template
		Template template = createTemplate(model);

		// Now call the template
		com.hp.hpl.jena.query.Query arq = ARQFactory.get().createQuery((Select)template.getBody());
		QueryExecution qexec = ARQFactory.get().createQueryExecution(arq, model);
		QuerySolutionMap arqBindings = new QuerySolutionMap();
		arqBindings.add("predicate", RDFS.label);
		qexec.setInitialBinding(arqBindings); // Pre-assign the arguments
		ResultSet rs = qexec.execSelect();
		RDFNode object = rs.next().get("object");
		System.out.println("Label is " + object);
	}


	private static Template createTemplate(Model model) {
		
		// Create a template
		com.hp.hpl.jena.query.Query arqQuery = ARQFactory.get().createQuery(model, QUERY);
		Query spinQuery = new ARQ2SPIN(model).createQuery(arqQuery, null);
		Template template = model.createResource(NS + "MyTemplate", SPIN.Template).as(Template.class);
		template.addProperty(SPIN.body, spinQuery);
		
		// Define spl:Argument at the template
		Resource argument = model.createResource(SPL.Argument);
		argument.addProperty(SPL.predicate, model.getProperty(ARG.NS + "predicate"));
		argument.addProperty(SPL.valueType, RDF.Property);
		argument.addProperty(RDFS.comment, "The predicate to get the value of.");
		template.addProperty(SPIN.constraint, argument);
		
		return template;
	}
}
