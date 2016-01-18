package org.pipseq.common;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Clock implements IClock {

	private Clock(){}
	private static IClock instance = new Clock();
	@Override
	public DateTime now() {
		// now
		return this.Now();
	}

	/**
	 * Now.
	 *
	 * @return the date time
	 */
	public static final DateTime Now() {
		return Now(DateTime.getMachineTimeZone());
	}

	/**
	 * Now.
	 *
	 * @param timeZone
	 *            the time zone
	 * @return the date time
	 */
	public static final DateTime Now(TimeZone timeZone) {
		return new DateTime(Calendar.getInstance(timeZone));
	}

	public static IClock getInstance() {
		return instance;
	}

	@Override
	public DateTime now(TimeZone timeZone) {
		// now
		return Now(timeZone);
	}

}
