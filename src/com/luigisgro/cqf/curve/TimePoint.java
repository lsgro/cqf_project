package com.luigisgro.cqf.curve;

import java.util.Date;

/**
 * Point in time used to tag curves. It can refer to the time the curve has been generated.
 * @author Luigi Sgro
 *
 */
public interface TimePoint extends Comparable<TimePoint> {
	Date getTime();
}
