package org.pipseq.spin;

import static org.junit.Assert.*;

import com.hp.hpl.jena.graph.Graph
import com.hp.hpl.jena.graph.compose.MultiUnion
import com.hp.hpl.jena.rdf.model.*
import org.junit.Test;
import org.pipseq.content.ConnectorFactory;
import org.pipseq.content.FileConnector;
import org.pipseq.rdf.jena.cfg.ModelWrapper
import org.pipseq.rdf.jena.model.*;

class TestTtlLoad {

	//@Test
	public void test0() {
		
		def modelFiles = [
			"C:/users/rspates/Google Drive/work/fx/tbc/forex/pipseq.ttl",
			"C:/Users/rspates/Google Drive/work/fx/tbc/forexStrategy/pipseqStrategy.ttl"]

		ConnectorFactory pf = new ConnectorFactory();
		pf.setInstance(pf);
		pf.setProvider(new FileConnector());

		ModelWrapper tbox = new ModelWrapper("test")
		tbox.setModelFiles(modelFiles);
		try {
			tbox.read();
		} catch (Exception e) {
			e.printStackTrace();
			println e
		}
		println "here"
		int n=30;
		//Graph[] ga = new Graph[3];
		Model[] ma = new Model[n];
		Model union = ModelFactory.createDefaultModel();
		for (int i=0;i<n;i++){
			ma[i] = ModelFactory.createDefaultModel();
//			if (union == null){
//				union = ModelFactory.createUnion(ma[i],tbox.get());
//			}
//			else 
			union = ModelFactory.createUnion(ma[i],union);
		}
		union = ModelFactory.createUnion(union,tbox.get());
		def q = """
select * {
?a a pip:Setup .
}
"""
		Object results = Sparql.query(union,q)
		println results
	}

	//@Test
	public void test() {
		
		def modelFiles = [
			"C:/users/rspates/Google Drive/work/fx/tbc/forex/pipseq.ttl",
			"C:/Users/rspates/Google Drive/work/fx/tbc/forexStrategy/pipseqStrategy.ttl"]

		ConnectorFactory pf = new ConnectorFactory();
		pf.setInstance(pf);
		pf.setProvider(new FileConnector());

		ModelWrapper tbox = new ModelWrapper("test")
		tbox.setModelFiles(modelFiles);
		try {
			tbox.read();
		} catch (Exception e) {
			e.printStackTrace();
			println e
		}
		println "here"
	}

	@Test
	public void test2() {
		
		def modelFiles = [
			"C:/users/rspates/Google Drive/work/fx/tbc/forex/pipseq.ttl",
			"C:/Users/rspates/Google Drive/work/fx/tbc/forexStrategy/pipseqStrategy.ttl"]

		ConnectorFactory pf = new ConnectorFactory();
		pf.setInstance(pf);
		pf.setProvider(new FileConnector());

		ModelWrapper tbox = new ModelWrapper("test")
		tbox.setModelFiles(modelFiles);
		try {
			tbox.read();
		} catch (Exception e) {
			e.printStackTrace();
			println e
		}
		println "here"
	}

}
