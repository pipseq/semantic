/**
 * 
 */
package org.pipseq.common.external;

import java.util.TimeZone;

import org.pipseq.common.DateTime;
import org.pipseq.common.IClock;

/**
 * Agent accepting times from external time authorities
 * Useful for c# time synch during journal replay
 * @author rspates
 *
 */
public class ExternalClock implements IClock {

	private DateTime now;
	
	public ExternalClock(){
		DateTime.setClock(this);
	}
	
	public void setTime(long tms){
		now = DateTime.valueOf(tms);
	}
	
	public void setTime(String dateTimeStr){
		now = new DateTime().parse(dateTimeStr);
	}
	
	/* (non-Javadoc)
	 * @see org.pipseq.common.IClock#now()
	 */
	@Override
	public DateTime now() {
		// now
		return now;
	}

	/* (non-Javadoc)
	 * @see org.pipseq.common.IClock#now(java.util.TimeZone)
	 */
	@Override
	public DateTime now(TimeZone timeZone) {
		// now
		return now;
	}

}
