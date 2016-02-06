package org.pipseq.rdf.jena.filter;

import java.math.BigInteger;

import org.pipseq.common.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase3;

/**
 * isWithin
 * For use as a filter function within a SPARQL query.
 * Returns true if arg0 > DateTime.now() - {value as time component}
 * i.e., arg0 more recent than now - time component * factor
 * arg0 the dateTime
 * arg1 is an integer value
 * arg2 is a time component
 */
public class isWithin extends FunctionBase3 {
	private static final Logger log = LoggerFactory
			.getLogger(isWithin.class);

	/**
	 * Instantiates a new recent result.
	 */
	public isWithin() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.sparql.function.FunctionBase2#exec(com.hp.hpl.jena.sparql
	 * .expr.NodeValue, com.hp.hpl.jena.sparql.expr.NodeValue)
	 */
	@Override
	public NodeValue exec(NodeValue arg0, NodeValue arg1, NodeValue arg2) {

		// compare xsd:DateTimes
		if (arg0.isDateTime() && arg1.isInteger()) {
			DateTime dt0 = new DateTime(arg0.getDateTime()
					.toGregorianCalendar());
			BigInteger value = arg1.getInteger();
			int val = value.intValue();
			String component = arg2.getString();
			int timeComp = 0;
			if (component.equalsIgnoreCase("day")) timeComp = DateTime.DAY;
			else if (component.equalsIgnoreCase("days")) timeComp = DateTime.DAY;
			else if (component.equalsIgnoreCase("hour")) timeComp = DateTime.HOUR;
			else if (component.equalsIgnoreCase("hours")) timeComp = DateTime.HOUR;
			else if (component.equalsIgnoreCase("minute")) timeComp = DateTime.MINUTE;
			else if (component.equalsIgnoreCase("minutes")) timeComp = DateTime.MINUTE;
			else if (component.equalsIgnoreCase("second")) timeComp = DateTime.SECOND;
			else if (component.equalsIgnoreCase("seconds")) timeComp = DateTime.SECOND;
			else if (component.equalsIgnoreCase("millisecond")) timeComp = DateTime.MILLISECOND;
			else if (component.equalsIgnoreCase("milliseconds")) timeComp = DateTime.MILLISECOND;

			DateTime dt1 = dt0.addTime(timeComp, val);
			DateTime now = DateTime.now();
			boolean recent = dt1.isAfter(now);
			return recent ? NodeValue.TRUE : NodeValue.FALSE;
		}

		return NodeValue.FALSE;
	}

}
