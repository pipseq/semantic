package org.pipseq.rdf.jena.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.pipseq.rdf.jena.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase;
import com.hp.hpl.jena.sparql.function.FunctionEnv;

// TODO: Auto-generated Javadoc
/**
 * Example filter function that returns an indicative type string.
 * <ul>
 * <li>"Number", if it's a number of some kind</li>
 * <li>"String", if it's string</li>
 * <li>"DateTime", if it's a date time</li>
 * <li>"unknown" otherwise</li>
 * </ul>
 *
 * Usage:
 * <pre>
 * PREFIX ext: <java:arq.examples.ext.>
 * </pre>
 * <pre>
 * FILTER ext:classify(3+?x)
 * <pre>
 *
 * @author Andy Seaborne
 */
/**
 * start up node app.js listener # use this query to test this function <code>
 PREFIX f: <java:org.pipseq.rdf.jena.filter.>
  
  select * { 
  let (?a := 1) 
  let (?b := 2) 
  let (?c := f:jexec("sum",?a,?b,?a,?b)
  )
   }
 </code>
 * see NodeRestTest for more.
 * 
 * @author rspates
 *
 */
public class jexec extends FunctionBase {

	private static final Logger log = LoggerFactory.getLogger(jexec.class);
	private static final int COUNT = 1;
	private static final int SUM = 2;
	private static final int MAX = 3;
	private static final int MIN = 4;
	private static final int AVG = 5;
	private static final int GETLISTOBJS = 105;
	private static final int NUMERICFCNLIMIT = 100;
	private static Map<String, Integer> funcMap = new HashMap<String, Integer>();
	static {
		funcMap.put("count", COUNT);
		funcMap.put("sum", SUM);
		funcMap.put("max", MAX);
		funcMap.put("min", MIN);
		funcMap.put("avg", AVG);
		funcMap.put("getListObjs", GETLISTOBJS);
	}

	static {// TODO-needed here but perhaps best set somewhere else
		BaseDatatype listDatatype = new BaseDatatype("java:java.util.List/"){
			   public Class<?> getJavaClass() {
			        return List.class;
			   }
		};
		BaseDatatype mapDatatype = new BaseDatatype("java:java.util.Map/"){
			   public Class<?> getJavaClass() {
			        return Map.class;
			   }
		};
		TypeMapper.getInstance().registerDatatype(listDatatype);
		TypeMapper.getInstance().registerDatatype(mapDatatype);
}
	public jexec() {
		super();
	}
    private Model model = null;
    
	private Map<String,Map<String, Object>> objMap;
	private Resource blanknode;
	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.function.FunctionBase#exec(com.hp.hpl.jena.sparql.engine.binding.Binding, com.hp.hpl.jena.sparql.expr.ExprList, java.lang.String, com.hp.hpl.jena.sparql.function.FunctionEnv)
	 */
	@Override
	public NodeValue exec(Binding arg0, ExprList arg1, String arg2,
			FunctionEnv arg3) {
		// TODO Auto-generated method stub
		//convert.ConvertUtil.log("exec-{0},{1}", arg0,arg1);
		if (model == null) {
			model = ModelFactory.createModelForGraph(arg3.getActiveGraph());
			blanknode = model.createResource();
			objMap = new LinkedHashMap<String,Map<String, Object>>();
		}
		return super.exec(arg0, arg1, arg2, arg3);
	}


	@Override
	public void checkBuild(String uri, ExprList args) {
	}

	@Override
	public NodeValue exec(List<NodeValue> args) {

		if (args.size() > 0)
			try {
				NodeValue a0 = args.get(0);
				args.remove(0);
				String func = a0.getString();
				if (funcMap.get(func) >= NUMERICFCNLIMIT) {
					switch (funcMap.get(func)) {
					case GETLISTOBJS:
						int resObj = getListObjs(args);
						//System.out.println(objMap);
						Triple.remove(model, blanknode, "pip:hasMap");
						Literal lit = model.createTypedLiteral(objMap, "java:java.util.Map/");
						Triple.set(model, blanknode, "pip:hasMap", lit);
						return NodeValue.makeNode(blanknode.asNode());

					default:
						throw new Exception("func unknown: " + func);
					}
				} else {
					List<Number> ln = getNumberList(args);
					switch (funcMap.get(func)) {
					case COUNT:
						int resInt = args.size();
						return NodeValue.makeInteger(resInt);

					case SUM:
						double resDbl = sum(ln);
						return NodeValue.makeDecimal(new BigDecimal(resDbl));

					case MAX:
						resDbl = max(ln);
						return NodeValue.makeDecimal(new BigDecimal(resDbl));

					case MIN:
						resDbl = min(ln);
						return NodeValue.makeDecimal(new BigDecimal(resDbl));

					case AVG:
						resDbl = avg(ln);
						return NodeValue.makeDecimal(new BigDecimal(resDbl));

					default:
						throw new Exception("func unknown: " + func);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		return NodeValue.FALSE;
	}

	private List<Number> getNumberList(List<NodeValue> args) {
		List<Number> ln = new ArrayList<Number>();
		for (NodeValue nv : args) {
			Number n = nv.getDecimal();
			ln.add(n);
		}
		return ln;
	}

	private double sum(List<Number> args) {
		double result = 0;
		for (Number nv : args) {
			result += nv.doubleValue();
		}
		return result;
	}

	private double max(List<Number> args) {
		double result = Double.MIN_VALUE;
		for (Number nv : args) {
			double d = nv.doubleValue();
			result = (d > result ? d : result);
		}
		return result;
	}

	private double min(List<Number> args) {
		double result = Double.MAX_VALUE;
		for (Number nv : args) {
			double d = nv.doubleValue();
			result = (d < result ? d : result);
		}
		return result;
	}

	private double avg(List<Number> args) {
		double result = 0;
		for (Number nv : args) {
			result += nv.doubleValue();
		}
		return result / args.size();
	}

	/**
	 * 
	 * <code>
 PREFIX f: <java:org.pipseq.rdf.jena.filter.>
  
  select * { 
  	?a a pip:Setup
  	?b a pip:Trigger 
  let (?c := f:jexec("getListObjs",?a,?b)
  )
   }
 </code>
	 * 
	 * @param args
	 * @return
	 */
	private int getListObjs(List<NodeValue> args) {
		Iterator<NodeValue> it = args.iterator();
		if (it.hasNext()){
			NodeValue nv = it.next();
			if (!objMap.containsKey(getQuotedNodeValue(nv)))
				objMap.put(getQuotedNodeValue(nv), new HashMap<String,Object>());
			Map<String,Object> map = objMap.get(getQuotedNodeValue(nv));
			putList(map,it);
		}
		return 0;
	}
	
	private void putList(Map<String,Object> map, Iterator<NodeValue> it){
		if (it.hasNext()){	// key
			NodeValue prop = it.next();
			if (it.hasNext()){	// value
				NodeValue obj = it.next();
				if (obj.isIRI()){
					if (!map.containsKey(getQuotedNodeValue(prop))){
						map.put(getQuotedNodeValue(prop), new HashMap<String,Object>());
					}
					Map<String,Object> map2 = (Map<String,Object>)map.get(getQuotedNodeValue(prop));
					if (!map2.containsKey(getQuotedNodeValue(obj))){
						map2.put(getQuotedNodeValue(obj),new HashMap<String,Object>());
					}
					Map<String,Object> map3 = (Map<String,Object>)map2.get(getQuotedNodeValue(obj));;
					putList(map3, it);
				}
				else 
					map.put(getQuotedNodeValue(prop), 
							obj.isLiteral()?obj.asNode().getLiteralValue():
								obj.isBlank()? "_:b0" : getQuotedNodeValue(obj));
										
			}
		}
	}
	
	private String getQuotedNodeValue(NodeValue nv){
		return nv.asNode().getLocalName().toString();
	}
}
