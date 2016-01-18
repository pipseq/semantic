/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.pipseq.common.DateAlone;
import org.pipseq.common.DateTime;
import org.pipseq.rdf.jena.cfg.NSPrefixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;

// TODO: Auto-generated Javadoc
/**
 * The Class TripleData.
 */
public class TripleData extends TripleBase {
	private static final Logger log = LoggerFactory.getLogger(TripleData.class);
	private static final String scheme = "http:";
	private static final String bracketScheme = "<http:";
	private static final String endBracket = ">";
	private static Pattern prefixPattern = Pattern
			.compile("^([A-Za-z_]*):([A-Za-z_][A-Za-z0-9_]*)$");

	/**
	 * Checks for prefix scheme.
	 *
	 * @param s the s
	 * @return true, if successful
	 */
	public static boolean hasPrefixScheme(String s) {
		return s.startsWith(scheme) || s.startsWith(bracketScheme)
				&& s.endsWith(endBracket);
	}

	/**
	 * Checks for prefix.
	 *
	 * @param s the s
	 * @return true, if successful
	 */
	public static boolean hasPrefix(String s) {
		if (!hasPrefixScheme(s)) {
			Matcher m = prefixPattern.matcher(s);
			return m.matches();
		}
		return false;
	}

	/**
	 * Gets the prefixed string.
	 *
	 * @param s the s
	 * @return the prefixed string
	 */
	public static String getPrefixedString(String s) {
		if (!hasPrefixScheme(s)) {
			Matcher m = prefixPattern.matcher(s);
			if (m.matches()) {
				return m.group(1);
			}
		}
		return null;
	}

	/**
	 * As resource.
	 *
	 * @param o the o
	 * @return the object
	 */
	public static Object asResource(Object o) {
		if (o instanceof String) {
			String s = o.toString();
			if (isResource(s))
				return s;
			return ":" + s;
		}
		return o; // TODO: try turning o into Resource?
	}

	/**
	 * Checks if is resource.
	 *
	 * @param s the s
	 * @return true, if is resource
	 */
	public static boolean isResource(String s) {
		return hasPrefixScheme(s) || hasPrefix(s);
	}

	private static String prefixErrStr = "Prefix unknown for entity={0}";

	/**
	 * Gets the NS entity.
	 *
	 * @param m the m
	 * @param s the s
	 * @param pre the pre
	 * @return the NS entity
	 */
	public static String getNSEntity(Model m, String s, String pre) {
		String uri = s;
		String prefix = getPrefixedString(s);
		if (prefix != null) {
			uri = s.substring(prefix.length() + 1);
			pre = m.getNsPrefixURI(prefix);
			if (pre == null) {
				String err = msgFormat(prefixErrStr, s);
				log.error(err);
				throw new RuntimeException(err);
			}
			uri = msgFormat("{0}{1}", pre, uri);
		} else if (!hasPrefixScheme(s)) {
			pre = m.getNsPrefixURI(pre);
			if (pre == null) {
				String err = msgFormat(prefixErrStr, s);
				log.error(err);
				throw new RuntimeException(err);
			}
			uri = msgFormat("{0}{1}", pre, s);
		}
		return uri;
	}

	/**
	 * Make resource.
	 *
	 * @param m the m
	 * @param pre the pre
	 * @param s the s
	 * @return the resource
	 */
	public static Resource makeResource(Model m, String pre, String s) {
		String uri = getNSEntity(m, s, pre);
		Resource nr = m.createResource(uri);
		return nr;
	}

	/**
	 * Make resource.
	 *
	 * @param m the m
	 * @param s the s
	 * @return the resource
	 */
	public static Resource makeResource(Model m, String s) {
		if (s.equals("_:") || s.equals("[]")) {
			return m.createResource(); // blank node
		}
		String pre = NSPrefixManager.getInstance().getDomainPrefix();
		return makeResource(m, pre, s);
	}

	/**
	 * Resource exists.
	 *
	 * @param m the m
	 * @param r the r
	 * @return true, if successful
	 */
	public static boolean resourceExists(Model m, Resource r) {
		return m.containsResource(r);
	}

	/**
	 * Make property.
	 *
	 * @param m the m
	 * @param r the r
	 * @return the property
	 */
	public static Property makeProperty(Model m, Resource r) {
		Property pr = m.createProperty(r.toString());
		return pr;
	}

	/**
	 * Make property.
	 *
	 * @param m the m
	 * @param pre the pre
	 * @param s the s
	 * @return the property
	 */
	public static Property makeProperty(Model m, String pre, String s) {
		String uri = getNSEntity(m, s, pre);
		Property pr = m.createProperty(uri);
		return pr;
	}

	/**
	 * Make property.
	 *
	 * @param m the m
	 * @param s the s
	 * @return the property
	 */
	public static Property makeProperty(Model m, String s) {
		String pre = NSPrefixManager.getInstance().getDomainPrefix();
		return makeProperty(m, pre, s);
	}

	/**
	 * Make object.
	 *
	 * @param rn the rn
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object makeObject(RDFNode rn) throws Exception {

		Object value = null;
		if (rn.isURIResource()) {
			Resource r = (Resource) rn;
			String ln = r.getLocalName();
			if (!ln.equals(""))
				value = ln;
			else
				value = rn.toString();
		} else if (rn.isResource()) {
			Resource r = (Resource) rn;
			value = r;
		} else if (rn.isLiteral()) {
			Literal lit = (Literal) rn;
			String dt = lit.getDatatypeURI();
			if (dt != null) {
				if (dt.endsWith("integer")) {
					value = new Integer(lit.getInt());
				} else if (dt.endsWith("int")) {
					value = new Integer(lit.getInt());
				} else if (dt.endsWith("long")) {
					value = new Long(lit.getLong());
				} else if (dt.endsWith("decimal")) {
					value = new Double(lit.getDouble());
				} else if (dt.endsWith("double")) {
					value = new Double(lit.getDouble());
				} else if (dt.endsWith("float")) {
					value = new Float(lit.getFloat());
				} else if (dt.endsWith("boolean")) {
					value = new Boolean(lit.getBoolean());
				} else if (dt.endsWith("string")) {
					value = lit.getString();
				} else if (dt.endsWith("dateTime") || dt.endsWith("date")
						|| dt.endsWith("time")) {
					value = makeDateTimeFromXSDDateTimeLiteral(lit);
				} else if (dt.endsWith("USD")) { // http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/USD
					String sv = lit.getString();
					String[] sa = sv.split("[\\^]+");
					value = Double.parseDouble(sa[0]);
				} else if (dt.equals("java:java.util.List/")) {
					List list = (List)lit.getValue();
					value = list;
				} else if (dt.equals("java:java.util.Map/")) {
					Map map = (Map)lit.getValue();
					value = map;
				}
			}
			if (value == null) {
				value = rn.toString();
			}
		}
		else {
			value = rn.toString();
		}
		return value;
	}

	/**
	 * Make object.
	 *
	 * @param m the m
	 * @param obj the obj
	 * @return the RDF node
	 */
	public static RDFNode makeObject(Model m, Object obj) {

		RDFNode rn = null;
		if (obj instanceof Resource) {
			rn = (Resource) obj;
		} else if (obj instanceof String) {
			String os = (String) obj;
			if (isResource(os)) {
				rn = makeResource(m, os);
			} else {
				rn = m.createTypedLiteral(obj, XSDDatatype.XSDstring);
			}
		} else if (obj instanceof DateTime) {
			try {
				Calendar c = Calendar.getInstance();
				c.setTime(((DateTime) obj).getDate());
				rn = m.createTypedLiteral(c);
			} catch (Exception e) {
				// do nothing
			}
		} else if (obj instanceof DateAlone) {
			try {
				Calendar c = Calendar.getInstance();
				c.setTime(((DateAlone) obj).getDate());
				Object value = new XSDDateTime(c);
				LiteralLabel ll = LiteralLabelFactory.create(value, "",
						XSDDatatype.XSDdate);
				rn = new LiteralImpl(Node.createLiteral(ll), (ModelCom) m);

			} catch (Exception e) {
				// do nothing
			}
		} else {
			try {
				rn = m.createTypedLiteral(obj);
			} catch (Exception e) {
				// do nothing
			}
		}
		return rn;
	}

	/**
	 * Gets the expanded name.
	 *
	 * @param m the m
	 * @param obj the obj
	 * @return the expanded name
	 */
	public static String getExpandedName(Model m, Object obj) {
		if (obj instanceof RDFNode) {
			RDFNode rn = (RDFNode) obj;
			return rn.toString();
		} else if (obj instanceof Property) {
			Property pro = (Property) obj;
			return pro.getURI();
		} else if (obj instanceof String) {
			String s = (String) obj;
			if (hasPrefix(s)) {
				Resource res = makeResource(m, s);
				return getExpandedName(m, res);
			} else
				return s;
		}
		return obj.toString();
	}

	/**
	 * Gets the short name.
	 *
	 * @param m the m
	 * @param obj the obj
	 * @return the short name
	 */
	public static String getShortName(Model m, Object obj) {
		if (obj instanceof RDFNode) {
			RDFNode rn = (RDFNode) obj;
			if (rn.isResource()) {
				Resource res = (Resource) rn;
				String ns = res.getNameSpace();
				String ln = res.getLocalName();
				return msgFormat("{0}:{1}", m.getNsURIPrefix(ns), ln);
			} else if (rn.isLiteral()) {
				try {
					return makeObject(rn).toString();
				} catch (Exception e) {
					throw new RuntimeException("getShortName()", e);
				}
			}
		} else if (obj instanceof Property) {
			Property pro = (Property) obj;
			String ns = pro.getNameSpace();
			String ln = pro.getLocalName();
			return msgFormat("{0}:{1}", m.getNsURIPrefix(ns), ln);
		} else if (obj instanceof String) {
			String s = (String) obj;
			if (hasPrefixScheme(s)) {
				Resource res = makeResource(m, s);
				return getShortName(m, res);
			} else
				return s;
		}
		return obj.toString();
	}

	/**
	 * Gets the resource.
	 *
	 * @param m the m
	 * @param o the o
	 * @return the resource
	 */
	public static Resource getResource(Model m, Object o) {
		Resource res = null;
		if (o instanceof Resource) {
			res = (Resource) o;
		} else if (o instanceof String) {
			String s = (String) o;
			if (!isResource(s)) { // i.e., has no prefix or URI
				s = msgFormat("{0}:{1}", NSPrefixManager.getInstance()
						.getDomainPrefix(), s);
			}
			res = makeResource(m, s);
		}
		return res;
	}

	/**
	 * Checks if is type.
	 *
	 * @param m the m
	 * @param resObj the res obj
	 * @param typeObj the type obj
	 * @return true, if is type
	 * @throws Exception the exception
	 */
	public static boolean isType(Model m, Object resObj, Object typeObj)
			throws Exception {
		Resource type = getResource(m, typeObj);
		Resource res = getResource(m, resObj);

		Object o = Triple.get(m, res, "rdf:type");
		Resource tr = getResource(m, o);

		return type.equals(tr);
	}

	/**
	 * Make xsd date time from date time.
	 *
	 * @param dt the dt
	 * @return the XSD date time
	 */
	public static XSDDateTime makeXSDDateTimeFromDateTime(DateTime dt) {

		Calendar cal = Calendar.getInstance();
		TimeZone tz = dt.getTimeZone();
		cal.setTimeZone(tz);
		cal.setTime(dt.getDate());
		XSDDateTime xdt = new XSDDateTime(cal);
		return xdt;
	}

	/**
	 * Make xsd date time literal from date time.
	 *
	 * @param m the m
	 * @param dt the dt
	 * @return the literal
	 */
	public static Literal makeXSDDateTimeLiteralFromDateTime(ModelCom m,
			DateTime dt) {

		XSDDateTime xdt = makeXSDDateTimeFromDateTime(dt);
		return makeXSDDateTimeLiteralFromXSDDateTime(m, xdt);
	}

	/**
	 * Make xsd date time literal from xsd date time.
	 *
	 * @param m the m
	 * @param xdt the xdt
	 * @return the literal
	 */
	public static Literal makeXSDDateTimeLiteralFromXSDDateTime(ModelCom m,
			XSDDateTime xdt) {
		LiteralLabel ll = LiteralLabelFactory.create(xdt, "",
				XSDDatatype.XSDdateTime);
		Literal lit = new LiteralImpl(Node.createLiteral(ll), m);
		return lit;
	}

	/**
	 * Make xsd date time literal from date time.
	 *
	 * @param dt the dt
	 * @return the literal
	 */
	public static Literal makeXSDDateTimeLiteralFromDateTime(DateTime dt) {

		return makeXSDDateTimeLiteralFromDateTime(null, dt);
	}

	/**
	 * Make xsd date literal from date.
	 *
	 * @param m the m
	 * @param dt the dt
	 * @return the literal
	 */
	public static Literal makeXSDDateLiteralFromDate(ModelCom m, DateTime dt) {

		XSDDateTime xdt = makeXSDDateTimeFromDateTime(dt);
		xdt.narrowType(XSDDatatype.XSDdate);
		return makeXSDDateLiteralFromXSDDate(m, xdt);
	}

	/**
	 * Make xsd date literal from xsd date.
	 *
	 * @param m the m
	 * @param xdt the xdt
	 * @return the literal
	 */
	public static Literal makeXSDDateLiteralFromXSDDate(ModelCom m,
			XSDDateTime xdt) {
		LiteralLabel ll = LiteralLabelFactory.create(xdt, "",
				XSDDatatype.XSDdate);
		Literal lit = new LiteralImpl(Node.createLiteral(ll), m);
		return lit;
	}

	/**
	 * Make xsd date literal from date.
	 *
	 * @param date the date
	 * @return the literal
	 */
	public static Literal makeXSDDateLiteralFromDate(DateAlone date) {
		DateTime dt = DateTime.valueOf(date);
		return makeXSDDateLiteralFromDate(null, dt);
	}

	/**
	 * Make date time from xsd date time.
	 *
	 * @param xdt the xdt
	 * @return the date time
	 */
	public static DateTime makeDateTimeFromXSDDateTime(XSDDateTime xdt) {
		Literal lit = makeXSDDateTimeLiteralFromXSDDateTime(null, xdt);
		DateTime dt = makeDateTimeFromXSDDateTimeLiteral(lit);
		return dt;
	}

	/**
	 * Make date time from xsd date time literal.
	 *
	 * @param lit the lit
	 * @return the date time
	 */
	public static DateTime makeDateTimeFromXSDDateTimeLiteral(Literal lit) {
		XSDDateTime xdt = (XSDDateTime) lit.getValue();
		Calendar cal = xdt.asCalendar();
		DateTime dt = DateTime.valueOf(cal.getTime(), cal.getTimeZone());
		return dt;
	}

	/**
	 * Format xsd date time with tz offset.
	 *
	 * @param xdt the xdt
	 * @return the string
	 */
	public static String formatXSDDateTimeWithTZOffset(XSDDateTime xdt) {
		TimeZone tz = TimeZone.getDefault();// .getTimeZone("America/New_York");
		Calendar cal = Calendar.getInstance(tz);
		cal.setTime(xdt.asCalendar().getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); // close
		// "yyyy-MM-dd'T'HH:mm:ssXXX"); // use this in Java 7.0, true ISO 8601
		df.setTimeZone(tz);
		String dts = df.format(cal.getTime());
		dts = dts.substring(0, dts.length() - 2) + ":00"; // remove this hack w/
															// Java 7.0
		return dts;
	}

	/**
	 * Format xsd date time with tz offset.
	 *
	 * @param xdt the xdt
	 * @param timeZoneId the time zone id
	 * @return the string
	 */
	public static String formatXSDDateTimeWithTZOffset(XSDDateTime xdt,
			String timeZoneId) {
		TimeZone tz = TimeZone.getTimeZone(timeZoneId);
		Calendar cal = Calendar.getInstance(tz);
		cal.setTime(xdt.asCalendar().getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); // close
		// "yyyy-MM-dd'T'HH:mm:ssXXX"); // use this in Java 7.0, true ISO 8601
		df.setTimeZone(tz);
		String dts = df.format(cal.getTime());
		dts = dts.substring(0, dts.length() - 2) + ":00"; // remove this hack w/
															// Java 7.0
		return dts;
	}

	// the "XXX" in the date format is not supported until Java 7.0
	// this method will return incorrect result w/r.t. timezone until the
	/**
	 * Gets the xsd date time from string.
	 *
	 * @param m the m
	 * @param s the s
	 * @return the xsd date time from string
	 * @throws ParseException the parse exception
	 */
	// full format with "XXX" can be used
	public static XSDDateTime getXsdDateTimeFromString(Model m, String s)
			throws ParseException {
		Calendar cal = Calendar.getInstance();
		// SimpleDateFormat sdf = new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = sdf.parse(s);
		cal.setTime(date);
		Literal lit = m.createTypedLiteral(cal);
		XSDDateTime xdt = (XSDDateTime) lit.getValue();
		return xdt;
	}

	// public static String getXsdExpression(Object obj) throws Exception{
	// return getXsdExpression(Control.getModel(),obj);
	/**
	 * Gets the xsd expression.
	 *
	 * @param m the m
	 * @param obj the obj
	 * @return the xsd expression
	 * @throws Exception the exception
	 */
	// }
	public static String getXsdExpression(Model m, Object obj) throws Exception {

		if (obj instanceof Resource) {
			String res = obj.toString();
			if (res.startsWith(scheme))
				return "<" + res + ">";
			return res.toString();
		} else if (obj instanceof String && isResource((String) obj)) {
			String rs = (String) obj;
			if (rs.startsWith(scheme))
				return "<" + rs + ">";
			return obj.toString();
		} else if (obj == null)
			return "<>";
		else if (obj instanceof DateTime || obj instanceof DateAlone) {
			RDFNode rn = makeObject(m, obj);
			LiteralLabel l = LiteralLabelFactory.create(rn.asLiteral()
					.getValue());
			return format(l);
		} else if (obj instanceof Literal) {
			Literal l = (Literal) obj;
			return format(l);
		} else if (obj instanceof String) {
			String s = (String) obj;
			s = StringEscapeUtils.escapeJava(s);
			LiteralLabel l = LiteralLabelFactory.create(s);
			return format(l);
		} else {
			LiteralLabel l = LiteralLabelFactory.create(obj);
			return format(l);
		}
	}

	private static String format(Literal l) {

		String s2 = l.getLexicalForm();
		String dt = l.getDatatypeURI();
		String ls = null;
		if (dt == null)
			ls = String.format("\"%s\"", s2);
		else
			ls = String.format("\"%s\"^^<%s>", s2, dt);
		return ls;
	}

	private static String format(LiteralLabel l) {

		String s2 = l.getLexicalForm();
		String dt = l.getDatatypeURI();
		String ls = null;
		if (dt == null)
			ls = String.format("\"%s\"", s2);
		else
			ls = String.format("\"%s\"^^<%s>", s2, dt);
		return ls;
	}

	/**
	 * Format.
	 *
	 * @param m the m
	 * @param fmt the fmt
	 * @param args the args
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String format(Model m, String fmt, Object... args)
			throws Exception {
		int i = 0;
		Object[] args2 = new Object[args.length];
		for (Object arg : args) {
			args2[i++] = getXsdExpression(m, arg);
		}
		return String.format(fmt, args2);
	}
}
