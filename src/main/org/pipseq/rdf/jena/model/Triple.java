/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.pipseq.common.DateTime;
import org.pipseq.rdf.jena.id.IUid;
import org.pipseq.rdf.jena.id.SequenceIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

// TODO: Auto-generated Javadoc
/**
 * The Class Triple.
 */
public class Triple extends TripleBase {

	private static final Logger log = LoggerFactory.getLogger(Triple.class);
	private static IUid guidGenerator = new SequenceIdentity(100);

	/**
	 * Gets the literal.
	 *
	 * @param m the m
	 * @param res the res
	 * @param pro the pro
	 * @return the literal
	 * @throws Exception the exception
	 */
	public static Object getLiteral(Model m, Object res, Object pro)
			throws Exception {
		List al = getLiteralList(m, res, pro);
		if (al.size() == 1) {
			return al.get(0);
		} else if (al.size() == 0) {
			return null;
		}
		return al;
	}

	/**
	 * Gets the.
	 *
	 * @param m the m
	 * @param res the res
	 * @param pro the pro
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object get(Model m, Object res, Object pro) throws Exception {
		List al = getList(m, res, pro);
		if (al.size() == 1) {
			return al.get(0);
		} else if (al.size() == 0) {
			return null;
		}
		return al;
	}

	/**
	 * Gets the literal list.
	 *
	 * @param m the m
	 * @param res the res
	 * @param pro the pro
	 * @return the literal list
	 * @throws Exception the exception
	 */
	public static List getLiteralList(Model m, Object res, Object pro)
			throws Exception {
		return getList(m, res, pro, false);
	}

	/**
	 * Gets the list.
	 *
	 * @param m the m
	 * @param res the res
	 * @param pro the pro
	 * @return the list
	 * @throws Exception the exception
	 */
	public static List getList(Model m, Object res, Object pro)
			throws Exception {
		return getList(m, res, pro, true);
	}

	/**
	 * Gets the list.
	 *
	 * @param m the m
	 * @param res the res
	 * @param pro the pro
	 * @param makeObject the make object
	 * @return the list
	 * @throws Exception the exception
	 */
	public static List getList(Model m, Object res, Object pro,
			boolean makeObject) throws Exception {

		Resource nr = null;
		Property sp = null;

		if (res instanceof Resource) {
			nr = (Resource) res;
		} else if (res instanceof String) {
			nr = TripleData.makeResource(m, (String) res);
		} else if (res == null) {
			throw new RuntimeException("Resource arg res is null");
		} else {
			throw new RuntimeException("Unsupported resource datatype: "
					+ res.getClass());
		}

		if (pro instanceof Property) {
			sp = (Property) pro;
		} else if (pro instanceof Resource) {
			Resource r = (Resource) pro;
			String ns = r.getNameSpace();
			String pre = m.getNsURIPrefix(ns);
			String ln = r.getLocalName();
			// sp = m.createProperty(pre,ln);
			sp = TripleData.makeProperty(m, pre, ln);
		} else if (pro instanceof String) {
			sp = TripleData.makeProperty(m, (String) pro);
		} else if (pro == null) {
			throw new RuntimeException("Property arg pro is null");
		} else
			throw new RuntimeException("Unsupported property datatype: "
					+ pro.getClass());

		ArrayList<Object> al = new ArrayList<Object>();
		m.enterCriticalSection(true);
		try {
			NodeIterator ni = m.listObjectsOfProperty(nr, sp);
			for (; ni.hasNext();) {
				RDFNode rn = ni.nextNode();
				try {
					if (makeObject) {
						Object value = TripleData.makeObject(rn);
						al.add(value);
					} else {
						al.add(rn);
					}
				} catch (Exception e) {
					throw new RuntimeException("res=" + nr + ", prop=" + sp, e);
				}
			}
		} finally {
			m.leaveCriticalSection();
		}
		return al;
	}

	/**
	 * Gets the res.
	 *
	 * @param m the m
	 * @param pro the pro
	 * @param obj the obj
	 * @return the res
	 * @throws Exception the exception
	 */
	public static Object getRes(Model m, Object pro, Object obj)
			throws Exception {
		List al = getResList(m, pro, obj);
		if (al.size() == 1) {
			return al.get(0);
		} else if (al.size() == 0) {
			return null;
		}
		return al;
	}

	/**
	 * Gets the res list.
	 *
	 * @param m the m
	 * @param pro the pro
	 * @return the res list
	 * @throws Exception the exception
	 */
	public static List getResList(Model m, Object pro) throws Exception {
		return getResList(m, pro, null);
	}

	/**
	 * Gets the res list.
	 *
	 * @param m the m
	 * @param pro the pro
	 * @param obj the obj
	 * @return the res list
	 * @throws Exception the exception
	 */
	public static List getResList(Model m, Object pro, Object obj)
			throws Exception {

		RDFNode rn = null;
		Property sp = null;

		if (pro instanceof Property) {
			sp = (Property) pro;
		} else if (pro instanceof String) {
			sp = TripleData.makeProperty(m, (String) pro);
		} else
			throw new RuntimeException("Unsupported property datatype: "
					+ pro.getClass());

		if (obj != null)
			rn = TripleData.makeObject(m, obj);

		ArrayList<Resource> al = new ArrayList<Resource>();
		m.enterCriticalSection(true);
		try {
			ResIterator ri = null;
			if (rn != null) {
				ri = m.listResourcesWithProperty(sp, rn);
			} else {
				ri = m.listResourcesWithProperty(sp);
			}
			for (; ri.hasNext();) {

				Resource res = ri.nextResource();
				al.add(res);
			}
		} finally {
			m.leaveCriticalSection();
		}
		return al;
	}

	private static Resource makeResource(Model m, Object or) {
		Resource res = null;

		if (or instanceof Resource) {
			res = (Resource) or;
		} else if (or instanceof String) {
			res = TripleData.makeResource(m, (String) or);
		} else if (or == null) {
			throw new RuntimeException("Resource arg res is null");
		} else {
			throw new RuntimeException("Unsupported resource datatype: "
					+ or.getClass());
		}
		return res;
	}

	/**
	 * Gets the properties.
	 *
	 * @param m the m
	 * @param or the or
	 * @return the properties
	 */
	public static List<String> getProperties(Model m, Object or) {

		Resource res = makeResource(m, or);
		List<String> ls = new ArrayList<String>();

		ExtendedIterator<com.hp.hpl.jena.graph.Triple> iter = m.getGraph().find(res.asNode(),
				Node.ANY, Node.ANY);
		for (; iter.hasNext();) {
			com.hp.hpl.jena.graph.Triple t = iter.next();
			Node prop = t.getPredicate();
			ls.add(prop.getURI());
		}

		return ls;
	}

	/**
	 * Sets the.
	 *
	 * @param m the m
	 * @param res the res
	 * @param pro the pro
	 * @param o the o
	 */
	public static void set(Model m, Object res, Object pro, Object o) {
		set(m, res, pro, o, true);
	}

	/**
	 * Sets the.
	 *
	 * @param m the m
	 * @param res the res
	 * @param pro the pro
	 * @param o the o
	 * @param checkObjResource the check obj resource
	 */
	public static void set(Model m, Object res, Object pro, Object o,
			boolean checkObjResource) {

		Resource nr = null;
		Property sp = null;

		if (res instanceof Resource) {
			nr = (Resource) res;
		} else if (res instanceof String) {
			nr = TripleData.makeResource(m, (String) res);
		} else if (res == null) {
			throw new RuntimeException("Resource arg res is null");
		} else
			throw new RuntimeException("Unsupported resource datatype: "
					+ res.getClass());

		if (pro instanceof Property) {
			sp = (Property) pro;
		} else if (pro instanceof String) {
			sp = TripleData.makeProperty(m, (String) pro);
		} else if (pro == null) {
			throw new RuntimeException("Property arg pro is null");
		} else
			throw new RuntimeException("Unsupported property datatype: "
					+ pro.getClass());

		if (log.isDebugEnabled()) {
			log.debug("res=" + nr + ", prop=" + sp + ",obj=" + o);
			log.debug("o type=" + (o == null ? "null" : o.getClass()));
		}
		m.enterCriticalSection(false);
		try {
			if (o instanceof Resource) {
				Resource nr3 = (Resource) o;
				m.add(nr, sp, nr3);
			} else if (o instanceof String) {
				String s = (String) o;
				if (checkObjResource && TripleData.isResource(s)) {
					// if it looks like a resource, treat it like one
					Resource r = TripleData.makeResource(m, s);
					m.add(nr, sp, r);
				} else {
					m.add(nr, sp, s);
				}
			} else if (o instanceof Double) {
				Double l = (Double) o;
				m.addLiteral(nr, sp, l.doubleValue());
			} else if (o instanceof Float) {
				Float l = (Float) o;
				m.addLiteral(nr, sp, l.floatValue());
			} else if (o instanceof Integer) {
				Integer l = (Integer) o;
				m.addLiteral(nr, sp, l.intValue());
			} else if (o instanceof Long) {
				Long l = (Long) o;
				m.addLiteral(nr, sp, l.longValue());
				if (log.isDebugEnabled()) {
					log.debug("found long");
				}
			} else if (o instanceof BigInteger) {
				BigInteger l = (BigInteger) o;
				m.addLiteral(nr, sp, l.longValue());
				if (log.isDebugEnabled()) {
					log.debug("found BigInteger -> long");
				}
			} else if (o instanceof Boolean) {
				Boolean l = (Boolean) o;
				m.addLiteral(nr, sp, l.booleanValue());
			} else if (o instanceof DateTime) {
				m.add(nr, sp, TripleData
						.makeXSDDateTimeLiteralFromDateTime((DateTime) o));
			} else if (o instanceof Literal) {
				m.add(nr, sp, (Literal) o);
			} else if (o instanceof RDFNode) {
				m.add(nr, sp, (RDFNode) o);
			} else {
				String s3 = msgFormat("{0}", o);
				m.add(nr, sp, s3);
			}
		} finally {
			m.leaveCriticalSection();
		}
	}

	/**
	 * Removes the.
	 *
	 * @param m the m
	 * @param r the r
	 */
	public static void remove(Model m, Object r) {
		Resource res = null;
		if (r instanceof String) {
			res = TripleData.makeResource(m, (String) r);
		} else if (r instanceof Resource) {
			res = (Resource) r;
		} else
			return;
		m.removeAll(res, (Property) null, (RDFNode) null);
	}

	/**
	 * Removes the.
	 *
	 * @param m the m
	 * @param r the r
	 * @param p the p
	 */
	public static void remove(Model m, Object r, Object p) {

		try {
			List<Object> l = getLiteralList(m, r, p);
			for (Object o : l) {
				remove(m, r, p, o);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Removes the.
	 *
	 * @param m the m
	 * @param res the res
	 * @param pro the pro
	 * @param l the l
	 */
	public static void remove(Model m, Object res, Object pro, List<?> l) {
		for (Object o : l) {
			remove(m, res, pro, o);
		}
	}

	/**
	 * Removes the.
	 *
	 * @param m the m
	 * @param res the res
	 * @param pro the pro
	 * @param o the o
	 */
	public static void remove(Model m, Object res, Object pro, Object o) {

		Resource nr = null;
		Property sp = null;

		if (res instanceof Resource) {
			nr = (Resource) res;
		} else if (res instanceof String) {
			nr = TripleData.makeResource(m, (String) res);
		} else if (res == null) {
			throw new RuntimeException("Resource arg res is null");
		} else
			throw new RuntimeException("Unsupported resource datatype: "
					+ res.getClass());

		if (pro instanceof Property) {
			sp = (Property) pro;
		} else if (pro instanceof String) {
			sp = TripleData.makeProperty(m, (String) pro);
		} else if (pro == null) {
			throw new RuntimeException("Property arg pro is null");
		} else
			throw new RuntimeException("Unsupported property datatype: "
					+ pro.getClass());

		if (log.isDebugEnabled()) {
			log.debug("res=" + nr + ", prop=" + sp + ",obj=" + o);
		}
		RDFNode rdf = null;

		m.enterCriticalSection(false);
		try {
			if (o instanceof Resource) {
				Resource nr3 = (Resource) o;
				m.removeAll(nr, sp, nr3);
			} else if (o instanceof Literal) {
				m.removeAll(nr, sp, (Literal) o);
			} else if (o instanceof String) {
				String s = (String) o;
				if (TripleData.isResource(s)) {
					// if it looks like a resource, treat it like one
					rdf = TripleData.makeResource(m, s);
					m.removeAll(nr, sp, rdf);
				} else {
					m.removeAll(nr, sp, m.createLiteral(s));
				}
			} else if (o instanceof Double) {
				Double l = (Double) o;
				m.removeAll(nr, sp, m.createTypedLiteral(l));
			} else if (o instanceof Integer) {
				Integer l = (Integer) o;
				m.removeAll(nr, sp, m.createTypedLiteral(l));
			} else if (o instanceof Boolean) {
				Boolean l = (Boolean) o;
				m.removeAll(nr, sp, m.createTypedLiteral(l));
			} else if (o instanceof DateTime) {
				DateTime dt = (DateTime) o;
				Literal lit = TripleData.makeXSDDateTimeLiteralFromDateTime(dt);
				m.removeAll(nr, sp, lit);
			} else {
				String s3 = msgFormat("{0}", o);
				m.removeAll(nr, sp, m.createLiteral(s3));
			}
		} finally {
			m.leaveCriticalSection();
		}
	}

	/**
	 * Creates the resource.
	 *
	 * @param m the m
	 * @param type the type
	 * @return the resource
	 */
	public static Resource createResource(Model m, Object type, Object suffix) {
		Resource nr = null;

		if (type instanceof Resource) {
			nr = (Resource) type;
		} else if (type instanceof String) {
			nr = TripleData.makeResource(m, (String) type);
		} else
			throw new RuntimeException("Unsupported type datatype: "
					+ type.getClass());

		String id = createResourceId(m, nr, suffix);
		return m.createResource(id, nr);
	}

	private static String createResourceId(Model m, Resource type, Object suffix) {
		String id = type.getLocalName();
		String ns = type.getNameSpace();
		String uriNs = m.expandPrefix(ns);
		String result = msgFormat("{0}{1}_{2}", uriNs, id, suffix);
		return result;
	}

	/**
	 * Creates the resource.
	 *
	 * @param m the m
	 * @param type the type
	 * @return the resource
	 */
	public static Resource createResource(Model m, Object type) {
		Resource nr = null;

		if (type instanceof Resource) {
			nr = (Resource) type;
		} else if (type instanceof String) {
			nr = TripleData.makeResource(m, (String) type);
		} else
			throw new RuntimeException("Unsupported type datatype: "
					+ type.getClass());

		String id = createResourceId(m, nr);
		return m.createResource(id, nr);
	}

	private static String createResourceId(Model m, Resource type) {
		String id = type.getLocalName();
		String ns = type.getNameSpace();
		String uriNs = m.expandPrefix(ns);
		String result = msgFormat("{0}{1}_{2}", uriNs, id, getGuidGenerator()
				.getGuid());
		return result;
	}

	/**
	 * Creates the resource id.
	 *
	 * @param rdfTypeUri the rdf type uri
	 * @return the string
	 */
	public static String createResourceId(String rdfTypeUri) {
		String result = msgFormat("{0}_{1}", rdfTypeUri, getGuidGenerator()
				.getGuid());
		return result;
	}

	/**
	 * Timestamp.
	 *
	 * @param model the model
	 * @param resource the resource
	 * @param property the property
	 */
	public static void timestamp(Model model, Object resource, Object property) {
		DateTime now = DateTime.now();
		set(model, resource, property, now);
	}

	/**
	 * Sets the guid generator.
	 *
	 * @param guidGenerator the new guid generator
	 */
	public void setGuidGenerator(IUid guidGenerator) {
		Triple.guidGenerator = guidGenerator;
	}

	/**
	 * Gets the guid generator.
	 *
	 * @return the guid generator
	 */
	public static IUid getGuidGenerator() {
		return guidGenerator;
	}

}
