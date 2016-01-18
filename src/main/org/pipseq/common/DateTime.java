/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.common;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

// TODO: Auto-generated Javadoc
/**
 * The Class DateTime.
 */
/*
 * similar to c# DateTime
 */
public class DateTime implements Cloneable, Serializable, Comparable<DateTime> {
	public static final int YEAR = 1;
	public static final int MONTH = 2;
	public static final int DAY = 3;
	public static final int HOUR = 4;
	public static final int MINUTE = 5;
	public static final int SECOND = 6;
	public static final int MILLISECOND = 7;
	private Calendar calendar;
	private static final Calendar calendarInstance = Calendar.getInstance();
	private static IClock clock = Clock.getInstance();

	public static IClock getClock() {
		return clock;
	}

	public static void setClock(IClock clock) {
		DateTime.clock = clock;
	}

	/**
	 * Gets the calendar instance.
	 *
	 * @return the calendar instance
	 */
	public static Calendar getCalendarInstance() {
		return calendarInstance;
	}

	/**
	 * Instantiates a new date time.
	 */
	public DateTime() {
		this.calendar = DateTime.getCalendarInstance();
		this.getCalendar().setTime(new Date(0));
	}

	/**
	 * Instantiates a new date time.
	 *
	 * @param calendar
	 *            the calendar
	 */
	public DateTime(Calendar calendar) {
		this.setCalendar((Calendar) calendar.clone());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		DateTime object = null;
		try {
			object = (DateTime) super.clone();
			object.setCalendar((Calendar) this.getCalendar().clone());
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException("DateTime.clone failed", ex);
		}
		return object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DateTime o) {
		return this.getDate().compareTo(o.getDate());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other instanceof DateTime) {
			DateTime otherDateTime = (DateTime) other;
			return this.getTimeInMillis() == otherDateTime.getTimeInMillis()
					&& this.getTimeZone().equals(otherDateTime.getTimeZone());
		}
		return false;
	}

	/**
	 * Arithmetic (+/-) with time components of dateTime
	 * @param field (day, hour, minute, second, millisecond)
	 * @param amount
	 * @return
	 */
	public DateTime addTime(int field, int amount) {
		if (!(field == HOUR || field == MINUTE 
				|| field == SECOND || field == MILLISECOND))
			throw new RuntimeException("Bad field value(" + field + ")"
					+ " in DateTime.addTime()");

		DateTime newDate = (DateTime) this.clone();
		GregorianCalendar calendar = (GregorianCalendar) newDate.getCalendar();
		if (field == DAY) {
			long milliseconds = calendar.getTime().getTime();
			calendar.setTime(new Date(milliseconds += 24 * 3600000 * amount));
		} else if (field == HOUR) {
			long milliseconds = calendar.getTime().getTime();
			calendar.setTime(new Date(milliseconds += 3600000 * amount));
		} else if (field == MINUTE) {
			long milliseconds = calendar.getTime().getTime();
			calendar.setTime(new Date(milliseconds += 60000 * amount));
		} else if (field == SECOND) {
			long milliseconds = calendar.getTime().getTime();
			calendar.setTime(new Date(milliseconds += 1000 * amount));
		} else if (field == MILLISECOND) {
			long milliseconds = calendar.getTime().getTime();
			calendar.setTime(new Date(milliseconds += amount));
		} else {
			calendar.add(field, (int) amount); // shouldn't get here
		}
		return newDate;
	}

	/**
	 * Gets the calendar.
	 *
	 * @return the calendar
	 */
	public Calendar getCalendar() {
		return this.calendar;
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return new Date(this.getTimeInMillis());
	}

	/**
	 * Gets the date only.
	 *
	 * @return the date only
	 */
	public DateAlone getDateOnly() {
		return (DateAlone) this;
	}

	/**
	 * Gets the machine time zone.
	 *
	 * @return the machine time zone
	 */
	public static TimeZone getMachineTimeZone() {
		return TimeZone.getDefault();
	}

	/**
	 * Gets the time in millis.
	 *
	 * @return the time in millis
	 */
	public long getTimeInMillis() {
		return this.getCalendar().getTime().getTime();
	}

	/**
	 * Gets the time zone.
	 *
	 * @return the time zone
	 */
	public TimeZone getTimeZone() {
		return (TimeZone) this.getCalendar().getTimeZone().clone();
	}

	/**
	 * Checks if is after.
	 *
	 * @param other
	 *            the other
	 * @return true, if is after
	 */
	public boolean isAfter(DateAlone other) {
		return this.getDateOnly().isAfter(other);
	}

	/**
	 * Checks if is after.
	 *
	 * @param other
	 *            the other
	 * @return true, if is after
	 */
	public boolean isAfter(DateTime other) {
		return this.getCalendar().after(other.getCalendar());
	}

	/**
	 * Checks if is before.
	 *
	 * @param other
	 *            the other
	 * @return true, if is before
	 */
	public boolean isBefore(DateAlone other) {
		return this.getDateOnly().isBefore(other);
	}

	/**
	 * Checks if is before.
	 *
	 * @param other
	 *            the other
	 * @return true, if is before
	 */
	public boolean isBefore(DateTime other) {
		return this.getCalendar().before(other.getCalendar());
	}

	/**
	 * Checks if is between.
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return true, if is between
	 */
	public boolean isBetween(DateAlone start, DateAlone end) {
		return this.getDateOnly().isBetween(start, end);
	}

	/**
	 * Checks if is between.
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return true, if is between
	 */
	public boolean isBetween(DateTime start, DateTime end) {
		return !this.getCalendar().before(start.getCalendar())
				&& !this.getCalendar().after(end.getCalendar());
	}

	private void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat();
		format = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss.SSS");

		String dfs = format.format(this.getCalendar().getTime());
		return dfs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toISOString() {
		SimpleDateFormat format = new SimpleDateFormat();
		format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

		String dfs = format.format(this.getCalendar().getTime());
		return dfs;
	}

	/**
	 * To string.
	 *
	 * @param timeZone
	 *            the time zone
	 * @param dfp
	 *            the dfp
	 * @return the string
	 */
	public String toString(TimeZone timeZone, DateFormatterPattern dfp) {
		return dfp.format(this);
	}

	/**
	 * Value of.
	 *
	 * @param dateTime
	 *            the date time
	 * @return the date time
	 */
	public static final DateTime valueOf(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return (DateTime) dateTime.clone();
	}

	/**
	 * Now.
	 *
	 * @param timeZone
	 *            the time zone
	 * @return the date time
	 */
	public static final DateTime now(TimeZone timeZone) {
		return clock.now(timeZone);
	}

	/**
	 * Now.
	 *
	 * @return the date time
	 */
	public static final DateTime now() {
		return clock.now();
	}

	/**
	 * Value of.
	 *
	 * @param date
	 *            the date
	 * @return the date time
	 */
	public static final DateTime valueOf(long tms) {
		return DateTime.valueOf(new Date(tms), DateTime.getMachineTimeZone());
	}

	/**
	 * Value of.
	 *
	 * @param date
	 *            the date
	 * @return the date time
	 */
	public static final DateTime valueOf(Date date) {
		return DateTime.valueOf(date, DateTime.getMachineTimeZone());
	}

	/**
	 * Value of.
	 *
	 * @param date
	 *            the date
	 * @param timeZone
	 *            the time zone
	 * @return the date time
	 */
	public static final DateTime valueOf(Date date, TimeZone timeZone) {
		if (timeZone == null) {
			timeZone = DateTime.getMachineTimeZone();
		}
		if (date == null) {
			return null;
		}
		Calendar calendar = DateTime.getCalendarInstance();
		calendar.setTimeInMillis(date.getTime());
		return new DateTime(calendar);
	}
	
	public static final DateTime parse(String ts){
		try {
			return DateTime.valueOf(ts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    public static final DateTime valueOf(String value) throws Exception {
        if (value == null || value.trim().equals("")) {
            return null;
        }
        return DateFormatterPattern.getDateTime(value);
    }

}
