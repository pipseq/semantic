package org.pipseq.rdf.jena.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class StatementWriter {

    public static String write(Statement stmt) {
    	OutputStream os = new ByteArrayOutputStream();
    	PrintWriter writer = new PrintWriter(os);
        write(stmt,writer);
        return os.toString();
}

    public static void write(Statement stmt, PrintWriter writer) {
        writeResource(stmt.getSubject(), writer);
        writer.print(" ");
        writeResource(stmt.getPredicate(), writer);
        writer.print(" ");
        writeNode(stmt.getObject(), writer);
        writer.println(" .");
}

    protected static void writeResource(Resource r, PrintWriter writer)
         {
        if (r.isAnon()) {
            writer.print(anonName(r.getId()));
        } else {
            writer.print("<");
            writeURIString(r.getURI(), writer);
            writer.print(">");
        }
    }
    static private boolean okURIChars[] = new boolean[128];
    static {
        for (int i = 32; i < 127; i++)
            okURIChars[i] = true;
        okURIChars['<'] = false;
        okURIChars['>'] = false;
        okURIChars['\\'] = false;

    }
    private static void writeURIString(String s, PrintWriter writer) {

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < okURIChars.length && okURIChars[c]) {
                writer.print(c);
            } else {
                String hexstr = Integer.toHexString(c).toUpperCase(Locale.ENGLISH);
                int pad = 4 - hexstr.length();
                writer.print("\\u");
                for (; pad > 0; pad--)
                    writer.print("0");
                writer.print(hexstr);
            }
        }
    }
    private static void writeString(String s, PrintWriter writer) {

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '"') {
                writer.print('\\');
                writer.print(c);
            } else if (c == '\n') {
                writer.print("\\n");
            } else if (c == '\r') {
                writer.print("\\r");
            } else if (c == '\t') {
                writer.print("\\t");
            } else if (c >= 32 && c < 127) {
                writer.print(c);
            } else {
                String hexstr = Integer.toHexString(c).toUpperCase();
                int pad = 4 - hexstr.length();
                writer.print("\\u");
                for (; pad > 0; pad--)
                    writer.print("0");
                writer.print(hexstr);
            }
        }
    }
    protected static void writeLiteral(Literal l, PrintWriter writer) {
        String s = l.getString();
        /*
        if (l.getWellFormed())
        	writer.print("xml");
        */
        writer.print('"');
        writeString(s, writer);
        writer.print('"');
        String lang = l.getLanguage();
        if (lang != null && !lang.equals(""))
            writer.print("@" + lang);
        String dt = l.getDatatypeURI();
        if (dt != null && !dt.equals(""))
            writer.print("^^<" + dt + ">");
    }

    public static String writeNode(Object n)
    {
    	OutputStream os = new ByteArrayOutputStream();
    	PrintWriter writer = new PrintWriter(os);
	   if (n instanceof Literal) {
	       writeLiteral((Literal) n, writer);
	   } else {
	       writeResource((Resource) n, writer);
	   }
	   writer.flush();
	   writer.close();
        return os.toString();
}

    protected static void writeNode(RDFNode n, PrintWriter writer)
    {
   if (n instanceof Literal) {
       writeLiteral((Literal) n, writer);
   } else {
       writeResource((Resource) n, writer);
   }
}

    protected static String anonName(AnonId id) {
        String name = "_:A";
        String sid = id.toString();
        for (int i = 0; i < sid.length(); i++) {
            char c = sid.charAt(i);
            if (c == 'X') {
                name = name + "XX";
            } else if (Character.isLetterOrDigit(c)) {
                name = name + c;
            } else {
                name = name + "X" + Integer.toHexString(c) + "X";
            }
        }
        return name;
    }}
