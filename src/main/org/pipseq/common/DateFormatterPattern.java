/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.common;

import java.text.SimpleDateFormat;

// TODO: Auto-generated Javadoc
/**
 * The Class DateFormatterPattern.
 */
public class DateFormatterPattern {

	/** The Constant APPEND_NO_TIME_ZONE. */
	public static final int APPEND_NO_TIME_ZONE = 0;
    
    /** The patterns. */
    String[] patterns;

	/**
	 * Instantiates a new date formatter pattern.
	 *
	 * @param comment the comment
	 * @param patterns the patterns
	 * @param nil the nil
	 * @param mask the mask
	 */
	public DateFormatterPattern(String comment,String[] patterns, String nil, int mask){
		this.patterns = patterns;
	}
	
	/**
	 * Format.
	 *
	 * @param dt the dt
	 * @return the string
	 */
	String format(DateTime dt){
    	SimpleDateFormat format = new SimpleDateFormat();
        format = new SimpleDateFormat(patterns[0]);

        String dfs = format.format(dt.getCalendar().getTime());
        return dfs;

	}
    
    /**
     * Gets the date time.
     *
     * @param ts the ts
     * @return the date time
     * @throws Exception the exception
     */
    public static DateTime getDateTime(String ts) throws Exception {
		DateTime dt=null;
		java.util.Date date = null;
		java.text.DateFormat sdf = java.text.DateFormat.getInstance();
		{
			String[] formats = new String[]{
					"MM/dd/yyyy HH:mm:ss.SSS z",
					"MM/dd/yyyy HH:mm:ss.SSS",
					"MM/dd/yyyy HH:mm:ss",
					"MM/dd/yyyy HH:mm z",
					"MM/dd/yyyy HH:mm",
					"MM/dd/yyyy",
					"yyyy-MM-dd'T'HH:mm:ss+/-ZZ:ZZ",
					"yyyy-MM-dd'T'HH:mm:ss.SSS+/-ZZ:ZZ",
					"yyyy-MM-dd'T'HH:mm:ss.SSSZ",
					"yyyy-MM-dd'T'HH:mm:ss.SSS",
					"yyyy-MM-dd'T'HH:mm:ss",
					"yyyy-MM-dd"
					};
			sdf.setTimeZone(java.util.TimeZone.getDefault());
			for (int i=0; date == null && i<formats.length;i++){
				((java.text.SimpleDateFormat)sdf).applyPattern(formats[i]);
				try {
					date = sdf.parse(ts);
				} catch (java.text.ParseException pe){} // keep trying
			}
		}
		if (date != null) dt = new DateTime(sdf.getCalendar());
		else {
				String[] formats = new String[]{
					"HH:mm:ss",
					"HH:mm",
					"HH:mm:ss.SSS",
					"ss"};
			sdf.setTimeZone(java.util.TimeZone.getTimeZone(""));
			for (int i=0; date == null && i<formats.length;i++){
				((java.text.SimpleDateFormat)sdf).applyPattern(formats[i]);
				try {
					date = sdf.parse(ts);
				} catch (java.text.ParseException pe){} // keep trying
			}
		}
		if (date != null) dt = new DateTime(sdf.getCalendar());
		else 
			throw new Exception("getDate(), Could not parse date: "+ts);
		return dt;
	}

}
