/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pipseq.common.DateFormatterPattern;
import org.pipseq.common.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Literal;

// TODO: Auto-generated Javadoc
/**
 * The Class TripleUtil.
 */
public class TripleUtil extends TripleBase {
	private static final Logger log = LoggerFactory.getLogger(TripleUtil.class);

	/**
	 * Prints the query results.
	 *
	 * @param result the result
	 * @return the string
	 */
	public static String printQueryResults(List<List<Object>> result) {
		return printQueryResults(result, null);
	}

	/**
	 * Prints the query results.
	 *
	 * @param result the result
	 * @param headers the headers
	 * @return the string
	 */
	public static String printQueryResults(List<List<Object>> result,
			List<String> headers) {
		StringBuffer sb = new StringBuffer();

		int c = 0;
		for (int j = 0; j < result.size(); j++) {
			c = Math.max(c, result.get(j).size());
		}
		int size[] = new int[c];
		for (List<Object> list : result) {
			int i = 0;
			for (Object o : list) {
				String s = o.toString();
				if (o instanceof XSDDateTime) {
					XSDDateTime xdt = (XSDDateTime) o;
					DateTime dt = getDateTimeFromXsdDateTime(xdt);
					s = getDateTimeAsW3CDTF(dt);
				}
				size[i] = Math.max(size[i], s.length());
				i++;
			}
		}
		if (headers != null) {
			int j = 0;
			for (String s : headers) {
				sb.append(s.format("%-" + (size[j++] + 1) + "s", s));
			}
			sb.append('\n');
			j = 0;
			for (String s : headers) {
				sb.append(s.format("%-" + (size[j] + 1) + "s",
						makeLine(size[j])));
				j++;
			}
			sb.append('\n');
		}
		for (List<Object> list : result) {
			int i = 0;
			for (Object o : list) {

				String s = o.toString();

				if (o instanceof XSDDateTime) {
					XSDDateTime xdt = (XSDDateTime) o;
					DateTime dt = getDateTimeFromXsdDateTime(xdt);
					s = getDateTimeAsW3CDTF(dt);
				}

				sb.append(s.format("%-" + (size[i++] + 1) + "s", s));
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * Gets the xsd date time from rdf node.
	 *
	 * @param obj the obj
	 * @return the xsd date time from rdf node
	 * @throws Exception the exception
	 */
	public static XSDDateTime getXsdDateTimeFromRDFNode(Object obj)
			throws Exception {
		if (!(obj instanceof Literal))
			throw new Exception(
					"getXsdDateTimeFromRDFNode(), arg is not Literal");
		Literal lit = (Literal) obj;
		Object litobj = lit.getValue();
		if (!(litobj instanceof XSDDateTime))
			throw new Exception(
					"getXsdDateTimeFromRDFNode(), arg is not XSDDateTime");
		XSDDateTime xdt = (XSDDateTime) litobj;
		return xdt;
	}

	/**
	 * Gets the date time from xsd date time.
	 *
	 * @param xdt the xdt
	 * @return the date time from xsd date time
	 */
	public static DateTime getDateTimeFromXsdDateTime(XSDDateTime xdt) {
		DateTime dt = DateTime.valueOf(xdt.asCalendar().getTime(),
				DateTime.getMachineTimeZone());
		return dt;
	}

	/**
	 * Gets the date time as w3 cdtf.
	 *
	 * @param datetime the datetime
	 * @return the date time as w3 cdtf
	 */
	public static String getDateTimeAsW3CDTF(DateTime datetime) {
		String s = datetime.toString(datetime.getTimeZone(), RDF_DATE_TIME);
		return convertRFC822ToW3CDTF(s);
	}

	private static final DateFormatterPattern RDF_DATE_TIME = new DateFormatterPattern(
			"RDF date & time - almost (RFC822, not W3CDTF)",
			new String[] { "yyyy-MM-dd'T'HH:mm:ssZ" }, null,
			DateFormatterPattern.APPEND_NO_TIME_ZONE);

	private static Pattern RFC822_Pattern = Pattern
			.compile("^(.*[\\-\\+]\\d\\d)(\\d\\d)$");

	/**
	 * Convert rf c822 to w3 cdtf.
	 *
	 * @param rfc822 the rfc822
	 * @return the string
	 */
	public static String convertRFC822ToW3CDTF(String rfc822) {
		Matcher m = RFC822_Pattern.matcher(rfc822);
		if (m.matches()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= m.groupCount(); i++) {
				sb.append(m.group(i));
				if (i == 1)
					sb.append(":");
			}
			return sb.toString();
		}
		return rfc822;
	}

	private static String makeLine(int len) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++)
			sb.append('-');
		return sb.toString();
	}

	/**
	 * Gets the string first result.
	 *
	 * @param llo the llo
	 * @param reason the reason
	 * @return the string first result
	 */
	public static String getStringFirstResult(List<List<Object>> llo,
			String reason) {
		Object o = getNthResult(llo, 0, reason);
		if (!(o instanceof String))
			throw new RuntimeException("Result not String, " + reason);
		return (String) getNthResult(llo, 0, reason);
	}

	/**
	 * Gets the first column.
	 *
	 * @param llo the llo
	 * @param reason the reason
	 * @return the first column
	 */
	public static List<Object> getFirstColumn(List<List<Object>> llo,
			String reason) {
		return getNthColumn(llo, 0, reason);
	}

	/**
	 * Gets the second column.
	 *
	 * @param llo the llo
	 * @param reason the reason
	 * @return the second column
	 */
	public static List<Object> getSecondColumn(List<List<Object>> llo,
			String reason) {
		return getNthColumn(llo, 1, reason);
	}

	/**
	 * Gets the nth column.
	 *
	 * @param llo the llo
	 * @param n the n
	 * @param reason the reason
	 * @return the nth column
	 */
	public static List<Object> getNthColumn(List<List<Object>> llo, int n,
			String reason) {
		List<Object> lo = new ArrayList<Object>();
		for (int i = 0; i < llo.size(); i++) {
			if (llo.get(0).size() < n)
				throw new RuntimeException("Insufficient column results, "
						+ reason);
			lo.add(llo.get(i).get(n));
		}
		return lo;
	}

	/**
	 * Gets the first result.
	 *
	 * @param llo the llo
	 * @param reason the reason
	 * @return the first result
	 */
	public static Object getFirstResult(List<List<Object>> llo, String reason) {
		return getNthResult(llo, 0, reason);
	}

	/**
	 * Gets the second result.
	 *
	 * @param llo the llo
	 * @param reason the reason
	 * @return the second result
	 */
	public static Object getSecondResult(List<List<Object>> llo, String reason) {
		return getNthResult(llo, 1, reason);
	}

	/**
	 * Gets the first row results.
	 *
	 * @param llo the llo
	 * @param reason the reason
	 * @return the first row results
	 */
	public static List<Object> getFirstRowResults(List<List<Object>> llo,
			String reason) {
		if (llo.size() < 1)
			throw new RuntimeException("Insufficient row results, " + reason);
		return llo.get(0);
	}

	/**
	 * Gets the nth result.
	 *
	 * @param llo the llo
	 * @param n the n
	 * @param reason the reason
	 * @return the nth result
	 */
	public static Object getNthResult(List<List<Object>> llo, int n,
			String reason) {
		if (llo.size() < 1)
			throw new RuntimeException("Insufficient row results, " + reason);
		if (llo.get(0).size() < n + 1)
			throw new RuntimeException("Insufficient column results, " + reason);
		return llo.get(0).get(n);
	}
}
