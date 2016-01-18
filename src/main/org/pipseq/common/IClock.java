package org.pipseq.common;

import java.util.TimeZone;

public interface IClock {

	DateTime now();
	DateTime now(TimeZone timeZone);
}
