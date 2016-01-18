/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.model;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.pipseq.common.DateFormatterPattern;
import org.pipseq.common.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.n3.SelectedNodeModelWriter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO: Auto-generated Javadoc
/**
 * The Class TripleBase.
 */
public abstract class TripleBase {
	private static final Logger log = LoggerFactory.getLogger(TripleBase.class);

	/**
	 * Msg format.
	 *
	 * @param fmt the fmt
	 * @param args the args
	 * @return the string
	 */
	public static String msgFormat(String fmt, Object... args) {
		String s = MessageFormat.format(fmt, args);
		return s;
	}

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
	 * Log node.
	 *
	 * @param m the m
	 * @param r the r
	 */
	public static void logNode(Model m, Resource r) {
		log("{0}", printNode(m, r));
	}

	/**
	 * Printer.
	 *
	 * @param model the model
	 * @param output the output
	 * @param r the r
	 */
	static void printer(Model model, OutputStream output, Resource r) {

	}

	/**
	 * Prints the nodes.
	 *
	 * @param m the m
	 * @param lr the lr
	 * @return the string
	 */
	public static String printNodes(Model m, List<Resource> lr) {

		return printNodes(m, lr, true);
	}

	/**
	 * Prints the nodes.
	 *
	 * @param m the m
	 * @param lr the lr
	 * @param skipBlankNodes the skip blank nodes
	 * @return the string
	 */
	public static String printNodes(Model m, List<Resource> lr,
			boolean skipBlankNodes) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		printNodes(m, lr, skipBlankNodes, baos);
		String s = baos.toString();

		return s;
	}

	/**
	 * Prints the nodes.
	 *
	 * @param m the m
	 * @param lr the lr
	 * @param os the os
	 */
	public static void printNodes(Model m, List<Resource> lr, OutputStream os) {

		printNodes(m, lr, true, os);
	}

	/**
	 * Prints the nodes.
	 *
	 * @param m the m
	 * @param lr the lr
	 * @param skipBlankNodes the skip blank nodes
	 * @param os the os
	 */
	public static void printNodes(Model m, List<Resource> lr,
			boolean skipBlankNodes, OutputStream os) {

		SelectedNodeModelWriter smw = new SelectedNodeModelWriter();
		smw.setSkipPrintBlankNode(skipBlankNodes);
		smw.setSkipWritingPrefixes(true);
		smw.write(m, os, lr);
	}

	/**
	 * Write model ordered.
	 *
	 * @param m the m
	 * @param filename the filename
	 * @param headerComments the header comments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeModelOrdered(Model m, String filename,
			String[] headerComments) throws IOException {

		FileOutputStream fos = new FileOutputStream(filename);
		writeModelOrdered(m, fos, (String[]) headerComments);
		fos.close();
	}

	/**
	 * Write model ordered.
	 *
	 * @param m the m
	 * @param os the os
	 * @param headerComments the header comments
	 */
	public static void writeModelOrdered(Model m, OutputStream os,
			String[] headerComments) {

		SelectedNodeModelWriter smw = new SelectedNodeModelWriter();
		smw.setSkipPrintBlankNode(true);
		smw.setSkipWritingPrefixes(false);
		writeHeader(os, headerComments);
		smw.write(m, os, (List<Resource>) null);
	}

	/**
	 * Write header.
	 *
	 * @param os the os
	 * @param comments the comments
	 */
	protected static void writeHeader(OutputStream os, String[] comments) {
		PrintStream ps = new PrintStream(os);
		if (comments != null)
			for (String s : comments) {
				ps.println("# " + s);
			}
		ps.println(""); // add blank line
		ps.flush();
	}

	/**
	 * Prints the node.
	 *
	 * @param m the m
	 * @param r the r
	 * @return the string
	 */
	public static String printNode(Model m, Resource r) {

		List<Resource> lr = new ArrayList<Resource>();
		lr.add(r);
		return printNodes(m, lr);
	}

	/**
	 * Gets the date time from xsd date time.
	 *
	 * @param xsd the xsd
	 * @return the date time from xsd date time
	 */
	public static DateTime getDateTimeFromXsdDateTime(XSDDateTime xsd) {
		return new DateTime(xsd.asCalendar());
	}

	/**
	 * Gets the date time.
	 *
	 * @param ts the ts
	 * @return the date time
	 * @throws Exception the exception
	 */
	public static DateTime getDateTime(String ts) throws Exception {
		return DateFormatterPattern.getDateTime(ts);
	}

}
