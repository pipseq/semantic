/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.update.UpdateAction;

// TODO: Auto-generated Javadoc
/**
 * The Class Sparql.
 */
public class Sparql extends TripleBase {

	private static final Logger log = LoggerFactory.getLogger(Sparql.class);
	private boolean useShortNames = true;
	private boolean useLiterals = false;

	/**
	 * Log.
	 *
	 * @param fmt the fmt
	 * @param args the args
	 */
	public static void log(String fmt, Object... args) {
		log.info(msgFormat(fmt, args));
	}

	/**
	 * Query srv.
	 *
	 * @param m the m
	 * @param query the query
	 * @return the list
	 */
	public List<List<Object>> querySrv(Model m, String query) {
		String sa = loadQueryString(m, query);
		return query(m, null, sa);
	}

	/**
	 * Query srv.
	 *
	 * @param m the m
	 * @param qryParmMap the qry parm map
	 * @param query the query
	 * @return the list
	 */
	public List<List<Object>> querySrv(Model m, Map<String, Object> qryParmMap,
			String query) {
		String sa = loadQueryString(m, query);
		return query(m, qryParmMap, sa);
	}

	/**
	 * Query.
	 *
	 * @param m the m
	 * @param query the query
	 * @return the list
	 */
	public static List<List<Object>> query(Model m, String query) {
		Sparql tq = new Sparql();

		// this could be changed to default shortName results if current
		// dependent rules
		// are modified to use short names
		tq.setUseShortNames(false);
		String sa = loadQueryString(m, query);

		return tq.queryProcess(m, null, sa);
	}

	/**
	 * Query describe.
	 *
	 * @param m the m
	 * @param query the query
	 * @return the model
	 */
	public static Model queryDescribe(Model m, String query) {
		Sparql tq = new Sparql();

		// this could be changed to default shortName results if current
		// dependent rules
		// are modified to use short names
		tq.setUseShortNames(true);
		String sa = loadQueryString(m, query);

		return tq.queryDescribe(m, null, sa);
	}

	/**
	 * Query.
	 *
	 * @param m the m
	 * @param query the query
	 * @param mapper the mapper
	 * @return the list
	 */
	public static List<List<Object>> query(Model m, String query,
			RowMapper mapper) {
		Sparql tq = new Sparql();

		// this could be changed to default shortName results if current
		// dependent rules
		// are modified to use short names
		tq.setUseShortNames(false);
		String sa = loadQueryString(m, query);

		return tq.queryProcess(m, null, sa, null, null, mapper);
	}

	/**
	 * Query short names.
	 *
	 * @param m the m
	 * @param query the query
	 * @return the list
	 */
	public static List<List<Object>> queryShortNames(Model m, String query) {
		Sparql tq = new Sparql();
		String sa = loadQueryString(m, query);

		return tq.queryProcess(m, null, sa);
	}

	/**
	 * Query short names.
	 *
	 * @param m the m
	 * @param qryParmMap the qry parm map
	 * @param query the query
	 * @return the list
	 */
	public static List<List<Object>> queryShortNames(Model m,
			Map<String, Object> qryParmMap, String query) {
		Sparql tq = new Sparql();
		String sa = loadQueryString(m, query);

		return tq.queryProcess(m, qryParmMap, sa);
	}

	/**
	 * Query short names.
	 *
	 * @param m the m
	 * @param query the query
	 * @param columnHeaders the column headers
	 * @return the list
	 */
	public static List<List<Object>> queryShortNames(Model m, String query,
			List<String> columnHeaders) {
		Sparql tq = new Sparql();
		String sa = loadQueryString(m, query);

		return tq.queryProcess(m, null, sa, columnHeaders, null);
	}

	/**
	 * Query verbose.
	 *
	 * @param m the m
	 * @param query the query
	 * @return the list
	 */
	public static List<List<Object>> queryVerbose(Model m, String query) {
		Sparql q = new Sparql();
		q.setUseLiterals(true);
		q.setUseShortNames(false);
		String sa = loadQueryString(m, query);
		return q.queryProcess(m, null, sa);
	}

	/**
	 * Query verbose.
	 *
	 * @param m the m
	 * @param query the query
	 * @param columnHeaders the column headers
	 * @return the list
	 */
	public static List<List<Object>> queryVerbose(Model m, String query,
			List<String> columnHeaders) {
		Sparql q = new Sparql();
		q.setUseLiterals(true);
		q.setUseShortNames(false);
		String sa = loadQueryString(m, query);
		return q.queryProcess(m, null, sa, columnHeaders, null);
	}

	/**
	 * Query.
	 *
	 * @param m the m
	 * @param qryParmMap the qry parm map
	 * @param query the query
	 * @return the list
	 */
	public static List<List<Object>> query(Model m,
			Map<String, Object> qryParmMap, String query) {
		Sparql tq = new Sparql();
		String sa = loadQueryString(m, query);

		return tq.queryProcess(m, qryParmMap, sa);
	}

	/**
	 * Query.
	 *
	 * @param m the m
	 * @param qryParmMap the qry parm map
	 * @param query the query
	 * @param headers the headers
	 * @return the list
	 */
	public static List<List<Object>> query(Model m,
			Map<String, Object> qryParmMap, String query, List<String> headers) {
		return query(m, qryParmMap, query, headers, null);
	}

	public static Map<String,Object> queryMap(Model model,String query){
		Map<String,Object> map = new HashMap<String,Object>();
		List<String> columnHeaders = new ArrayList<String>();
		List<List<Object>> results = Sparql.query(model, null, query, columnHeaders);
		if (columnHeaders.size() != 2)
			throw new RuntimeException("Map production requires two-column result");
		for (List<Object> l : results){
				String key = (String)l.get(0);
				Object value = l.get(1);
				map.put(key, value);
		}
		return map;
	}

	public static List<Map<String,Object>> queryListMap(Model model,String query){
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		List<String> columnHeaders = new ArrayList<String>();
		List<List<Object>> results = Sparql.query(model, null, query, columnHeaders);
		for (List<Object> l : results){
			Map<String,Object> map = new HashMap<String,Object>();
			listMap.add(map);
			for (int i=0;i<columnHeaders.size();i++){
				String key = columnHeaders.get(i);
				Object value = l.get(i);
				map.put(key, value);
			}
		}
		return listMap;
	}
	
	public static String queryJsonMap(Model model,String query){
		Map<String,Object> map = queryMap(model,query);
		Gson gson = new Gson();
		String json = gson.toJson(map);
		return json;
	}

	public static String queryJsonListMap(Model model,String query){
		List<Map<String,Object>> listMap = queryListMap(model,query);
		Gson gson = new Gson();
		String json = gson.toJson(listMap);
		return json;
	}
	
	// a queryJsonMapMap() version would key sub-maps on a unique identifier property/field

	/**
	 * Query.
	 *
	 * @param m the m
	 * @param qryParmMap the qry parm map
	 * @param query the query
	 * @param headers the headers
	 * @param additional the additional
	 * @return the list
	 */
	public static List<List<Object>> query(Model m,
			Map<String, Object> qryParmMap, String query, List<String> headers,
			List<String> additional) {
		Sparql tq = new Sparql();
		String sa = loadQueryString(m, query);

		return tq.queryProcess(m, qryParmMap, sa, headers, additional);
	}

	/**
	 * Load query file.
	 *
	 * @param m the m
	 * @param fn the fn
	 * @return the string
	 */
	public static String loadQueryFile(Model m, String fn) {
		BufferedReader bis;
		try {
			bis = new BufferedReader(new FileReader(fn));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return loadQueryBufferedReader(m, bis);
	}

	/**
	 * Load query string.
	 *
	 * @param m the m
	 * @param query the query
	 * @return the string
	 */
	public static String loadQueryString(Model m, String query) {
		BufferedReader bis = new BufferedReader(new StringReader(query));
		return loadQueryBufferedReader(m, bis);
	}

	/**
	 * Load prefixes.
	 *
	 * @param m the m
	 * @param q the q
	 * @return the string
	 */
	public static String loadPrefixes(Model m, String q) {
		StringBuffer sb = new StringBuffer();
		loadPrefixes(m, sb);
		sb.append(q);
		return sb.toString();
	}

	/**
	 * Load prefixes.
	 *
	 * @param m the m
	 * @param sb the sb
	 */
	public static void loadPrefixes(Model m, StringBuffer sb) {

		Map<String, String> nsmap = m.getNsPrefixMap();
		if (nsmap.size() < 2) { // get default map TODO--figure out why oracle
								// occassionally doesn't return a map!
			try {
				nsmap = m.getNsPrefixMap();
			} catch (Exception e) {
				;// do nothing, have an empty map
			}
		}
		for (String pre : nsmap.keySet()) {
			String ns = nsmap.get(pre);
			// PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
			String s = msgFormat("PREFIX {0}: <{1}>", pre, ns);
			sb.append(s);
			sb.append('\n');
		}
	}

	/**
	 * Sets the query parameters.
	 *
	 * @param model the model
	 * @param qryParmMap the qry parm map
	 * @return the query solution
	 */
	public static QuerySolution setQueryParameters(Model model,
			Map<String, Object> qryParmMap) {
		QuerySolutionMap qrySoln = new QuerySolutionMap();
		qrySoln = new QuerySolutionMap();
		for (Map.Entry<String, Object> entry : qryParmMap.entrySet()) {
			String key = (String) entry.getKey();
			Object value = entry.getValue();
			RDFNode node = TripleData.makeObject(model, value);
			qrySoln.add(key, node);
		}
		return qrySoln;
	}

	/**
	 * Load query buffered reader.
	 *
	 * @param m the m
	 * @param bis the bis
	 * @return the string
	 */
	public static String loadQueryBufferedReader(Model m, BufferedReader bis) {
		String line;
		StringBuffer sb = new StringBuffer();
		loadPrefixes(m, sb);

		try {
			while ((line = bis.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				sb.append(line);
				sb.append('\n');
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// uncomment to debug final form of query
		// System.err.println(""+sb.toString());
		return sb.toString();
	}

	/**
	 * Query.
	 *
	 * @param modelObj the model obj
	 * @param arg the arg
	 * @return the list
	 */
	public List<List<Object>> query(Object modelObj, String arg)

	{
		return queryProcess(modelObj, null, arg);
	}

	private static Pattern pat = Pattern.compile("^[\\s]*select[\\s]+.*",
			Pattern.CASE_INSENSITIVE);

	private boolean isSelect(String qs) {
		String[] sa = qs.split("\n");
		for (String s : sa) {
			Matcher mat = pat.matcher(s);
			if (mat.matches()) { // not a select, must be an update
				return true;
			}
		}
		return false;
	}

	private static Pattern pat2 = Pattern.compile("^[\\s]*construct[\\s]+.*",
			Pattern.CASE_INSENSITIVE);

	private boolean isConstruct(String qs) {
		String[] sa = qs.split("\n");
		for (String s : sa) {
			Matcher mat = pat2.matcher(s);
			if (mat.matches()) { // not a select, must be an update
				return true;
			}
		}
		return false;
	}

	private static Pattern pat3 = Pattern.compile("^[\\s]*ask[\\s]+.*",
			Pattern.CASE_INSENSITIVE);

	private boolean isAsk(String qs) {
		String[] sa = qs.split("\n");
		for (String s : sa) {
			Matcher mat = pat3.matcher(s);
			if (mat.matches()) { // not a select, must be an update
				return true;
			}
		}
		return false;
	}

	private static Pattern pat4 = Pattern.compile("^[\\s]*describe[\\s]+.*",
			Pattern.CASE_INSENSITIVE);

	private boolean isDescribe(String qs) {
		String[] sa = qs.split("\n");
		for (String s : sa) {
			Matcher mat = pat4.matcher(s);
			if (mat.matches()) { // not a select, must be an update
				return true;
			}
		}
		return false;
	}

	/**
	 * Query process.
	 *
	 * @param modelObj the model obj
	 * @param qryParmMap the qry parm map
	 * @param queryString the query string
	 * @return the list
	 */
	public List<List<Object>> queryProcess(Object modelObj,
			Map<String, Object> qryParmMap, String queryString) {
		return queryProcess(modelObj, qryParmMap, queryString, null);
	}

	/**
	 * Query process.
	 *
	 * @param modelObj the model obj
	 * @param qryParmMap the qry parm map
	 * @param queryString the query string
	 * @param columnHeaders the column headers
	 * @return the list
	 */
	public List<List<Object>> queryProcess(Object modelObj,
			Map<String, Object> qryParmMap, String queryString,
			List<String> columnHeaders)

	{
		return queryProcess(modelObj, qryParmMap, queryString, columnHeaders,
				null);
	}

	/**
	 * Query process.
	 *
	 * @param modelObj the model obj
	 * @param qryParmMap the qry parm map
	 * @param queryString the query string
	 * @param columnHeaders the column headers
	 * @param additional the additional
	 * @return the list
	 */
	public List<List<Object>> queryProcess(Object modelObj,
			Map<String, Object> qryParmMap, String queryString,
			List<String> columnHeaders, List<String> additional) {
		return queryProcess(modelObj, qryParmMap, queryString, columnHeaders,
				additional, new SparqlResultRowObject((Model) modelObj,
						useLiterals, useShortNames));
	}

	/**
	 * Query process.
	 *
	 * @param modelObj the model obj
	 * @param qryParmMap the qry parm map
	 * @param queryString the query string
	 * @param columnHeaders the column headers
	 * @param additional the additional
	 * @param mapper the mapper
	 * @return the list
	 */
	public List<List<Object>> queryProcess(Object modelObj,
			Map<String, Object> qryParmMap, String queryString,
			List<String> columnHeaders, List<String> additional,
			RowMapper mapper)

	{
		boolean foundSelect = isSelect(queryString);
		boolean foundConstruct = isConstruct(queryString);
		boolean foundAsk = isAsk(queryString);
		boolean foundDescribe = isDescribe(queryString);
		if (!(foundSelect || foundConstruct || foundAsk || foundDescribe)) { // not
																				// a
																				// select,
																				// must
																				// be
																				// an
																				// update
			executeProcess(modelObj, qryParmMap, queryString);
			List<List<Object>> llo = new ArrayList<List<Object>>();
			return llo;
		}
		log.debug(queryString);
		QueryExecution qexec = null;
		Model m = null;
		com.hp.hpl.jena.query.Query query = null;
		if (modelObj instanceof Model) {
			Model model = (Model) modelObj;
			// execute the query
			if (additional != null) {
				additional.add(queryString);
			}
			query = QueryFactory.create(queryString, Syntax.syntaxARQ);
			qexec = QueryExecutionFactory.create(query, model);
			m = model;
		}
		if (qryParmMap != null) {
			QuerySolution qs = setQueryParameters(m, qryParmMap);
			qexec.setInitialBinding(qs);
		}

		List<List<Object>> al = new ArrayList<List<Object>>();

		if (foundConstruct) {
			Model modelCon = qexec.execConstruct();
			m.add(modelCon);
			return al;
		}

		try {
			// process the result set
			ResultSet rs;
			try {
				m.enterCriticalSection(true);
				rs = qexec.execSelect();
			} finally {
				m.leaveCriticalSection();
			}
			if (columnHeaders != null)
				for (String var : rs.getResultVars())
					columnHeaders.add(var);

			Integer rowNum = 0;
			for (; rs.hasNext();) {
				rowNum++;
				List<Object> list = mapper.mapRow(rs, rowNum);
				al.add(list);
			}
			if (additional != null) {
				additional.add(rowNum.toString());
			}
		} finally {
			qexec.close();
		}
		return al;
	}

	/**
	 * Query describe.
	 *
	 * @param modelObj the model obj
	 * @param qryParmMap the qry parm map
	 * @param queryString the query string
	 * @return the model
	 */
	public Model queryDescribe(Object modelObj, Map<String, Object> qryParmMap,
			String queryString)

	{
		Model modelResult = ModelFactory.createDefaultModel();
		boolean foundDescribe = isDescribe(queryString);
		if (!foundDescribe) { // not a select, must be an update
			return modelResult;
		}
		log.debug(queryString);
		QueryExecution qexec = null;
		Model m = null;
		com.hp.hpl.jena.query.Query query = null;
		if (modelObj instanceof Model) {
			Model model = (Model) modelObj;
			// execute the query
			query = QueryFactory.create(queryString, Syntax.syntaxARQ);
			qexec = QueryExecutionFactory.create(query, model);
			m = model;
		}
		if (qryParmMap != null) {
			QuerySolution qs = setQueryParameters(m, qryParmMap);
			qexec.setInitialBinding(qs);
		}

		if (foundDescribe) {
			modelResult = qexec.execDescribe();
		}
		return modelResult;
	}

	/**
	 * Construct.
	 *
	 * @param m the m
	 * @param query the query
	 * @return the model
	 */
	public static Model construct(Model m, String query) {
		Sparql tq = new Sparql();

		// this could be changed to default shortName results if current
		// dependent rules
		// are modified to use short names
		String sa = loadQueryString(m, query);

		return tq.constructProcess(m, null, sa);
	}

	/**
	 * Construct process.
	 *
	 * @param modelObj the model obj
	 * @param qryParmMap the qry parm map
	 * @param queryString the query string
	 * @return the model
	 */
	public Model constructProcess(Object modelObj,
			Map<String, Object> qryParmMap, String queryString)

	{
		log.debug(queryString);
		QueryExecution qexec = null;
		Model m = null;
		if (modelObj instanceof Model) {
			Model model = (Model) modelObj;
			// execute the query
			com.hp.hpl.jena.query.Query query = QueryFactory.create(
					queryString, Syntax.syntaxARQ);
			qexec = QueryExecutionFactory.create(query, model);
			m = model;
		}
		if (qryParmMap != null) {
			QuerySolution qs = setQueryParameters(m, qryParmMap);
			qexec.setInitialBinding(qs);
		}

		Model resultModel = ModelFactory.createDefaultModel();
		try {
			// process the result set
			m.enterCriticalSection(true);
			resultModel = qexec.execConstruct();
		} finally {
			qexec.close();
			m.leaveCriticalSection();
		}
		return resultModel;
	}

	/**
	 * Checks if is use short names.
	 *
	 * @return true, if is use short names
	 */
	public boolean isUseShortNames() {
		return useShortNames;
	}

	/**
	 * Sets the use short names.
	 *
	 * @param useShortNames the new use short names
	 */
	public void setUseShortNames(boolean useShortNames) {
		this.useShortNames = useShortNames;
	}

	/**
	 * Execute.
	 *
	 * @param m the m
	 * @param query the query
	 */
	public static void execute(Model m, String query) {
		Sparql tq = new Sparql();

		// this could be changed to default shortName results if current
		// dependent rules
		// are modified to use short names
		tq.setUseShortNames(false);
		String sa = loadExecString(m, query);

		tq.executeProcess(m, null, sa);
	}

	/**
	 * Load exec string.
	 *
	 * @param m the m
	 * @param q the q
	 * @return the string
	 */
	public static String loadExecString(Model m, String q) {
		BufferedReader bis = new BufferedReader(new StringReader(q));
		return loadQueryBufferedReader(m, bis);
	}

	/**
	 * Execute process.
	 *
	 * @param modelObj the model obj
	 * @param qryParmMap the qry parm map
	 * @param queryString the query string
	 */
	public void executeProcess(Object modelObj, Map<String, Object> qryParmMap,
			String queryString)

	{
		log.debug(queryString);
		ArrayList<String> varList = new ArrayList<String>();
		QuerySolution qs = null;
		Model m = null;
		if (qryParmMap != null) {
			if (modelObj instanceof Model) {
				m = (Model) modelObj;
			}
			qs = setQueryParameters(m, qryParmMap);
		}

		try {
			if (modelObj instanceof Model) {
				m = (Model) modelObj;
				m.enterCriticalSection(false);

				// execute the query
				if (qs == null) {
					UpdateAction.parseExecute(queryString, m.getGraph());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (m != null)
				m.leaveCriticalSection();
		}
	}

	/**
	 * Sets the use literals.
	 *
	 * @param useLiterals the new use literals
	 */
	public void setUseLiterals(boolean useLiterals) {
		this.useLiterals = useLiterals;
	}

	/**
	 * Checks if is use literals.
	 *
	 * @return true, if is use literals
	 */
	public boolean isUseLiterals() {
		return useLiterals;
	}

}
