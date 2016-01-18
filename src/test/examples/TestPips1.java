package examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;
import org.pipseq.rdf.jena.model.Triple;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.PrintUtil;

public class TestPips1 {

	Model baseModel = FileManager.get().loadModel("file:data/pipseq.ttl");
	// Create OntModel with imports
	OntModel ontModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_MEM,baseModel);
	
	public TestPips1(){
		String uri = "http://pipseq.org#";
		PrintUtil.registerPrefix("pip", uri);
		// Initialize system functions and templates
		SPINModuleRegistry.get().init();
		// Register locally defined functions
		SPINModuleRegistry.get().registerAll(ontModel, null);


	}
	
	@Test
	public void test() throws Exception {

//		Object trig = getRes("rdf:type", "pip:Trigger");
//		System.out.println(trig);
		
		js3();
	}

    protected Object getRes(Object prop, Object obj) throws Exception
    {
        return Triple.getRes(baseModel, makeIRI(prop), makeIRI(obj));
    }

    protected Object makeIRI(Object res)
    {
        Object ro = res;

        if (res instanceof String)
        {
            String r = (String)res;
            if (!r.contains(":"))
                r = ":" + r;
            ro = r;
        }
        return ro;
    }

	public void run() {

		
		// Create and add Model for inferred triples
		Model newTriples = ModelFactory.createDefaultModel();
		ontModel.addSubModel(newTriples);

		// Run all inferences
		SPINInferences.run(ontModel, newTriples, null, null, true/*single pass*/, null);
		System.out.println("Inferred triples: " + newTriples.size());

		//org.pipseq.rdf.jena.util.SparqlScope.scope(ontModel);
		newTriples.write(System.out, "TTL");
		
		ontModel.removeSubModel(newTriples);
	}
	
	void js(){

		
		
        try
        {
            ScriptEngine engine = 
                new ScriptEngineManager().getEngineByName("javascript");
                FileReader fr = new FileReader("C:/work/Scripts/Jsfunctions.js");
                engine.eval(fr);
        }
        catch(IOException ioEx)
        {
            ioEx.printStackTrace();
        }
        catch(ScriptException scrEx)
        {
            scrEx.printStackTrace();
        }

//	    ScriptEngineManager manager = new ScriptEngineManager();
//	    ScriptEngine engine = manager.getEngineByName("JavaScript");
//	    // read script file
//	    
//	    
//	    try {
//			engine.eval(Files.newBufferedReader(Paths.get("C:/work/Scripts/Jsfunctions.js"), StandardCharsets.UTF_8));
//		} catch (ScriptException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	    Invocable inv = (Invocable) engine;
	    // call function from script file
//	    try {
//			inv.invokeFunction("test");  //("yourFunction", "param")
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ScriptException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}void js2(){
		

		      ScriptEngineManager manager = new ScriptEngineManager();
		      ScriptEngine engine = manager.getEngineByName("js");
		     try {
		  FileReader reader = new FileReader("C:/work/Scripts/Jsfunctions.js");
		  engine.eval(reader);
		  reader.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	void js3(){
        try {
            Process p = Runtime.getRuntime().exec("cmd /C node C:/work/Scripts/Jsfunctions.js");
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

	}
	
}
