package com.luigisgro.cqf.curve;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Day implementation of {@link TimePoint} class
 * @author Luigi Sgro
 *
 */
public class DayTimePoint implements TimePoint {
	private Date point;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // NOT THREAD SAFE!!!

	/**
	 * Creates an object set to the input date, at midnight
	 * @param time A {@link java.util.Date} object
	 */
	public DayTimePoint(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		point = cal.getTime();
	}
	@Override
	public Date getTime() {
		return point;
	}
	@Override
	public int compareTo(TimePoint o) {
		return getTime().compareTo(o.getTime());
	}
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof DayTimePoint))
			return false;
		return getTime().equals(((DayTimePoint)o).getTime());
	}
	@Override
	public int hashCode() {
		return getTime().hashCode();
	}
	@Override
	public String toString() {
		return sdf.format(getTime());
	}
	/**
	 * An utility method that creates an array of DayTimePoint object, one for each business
	 * day from firstDay to lastDay, included.
	 * @param firstDay The first day of the calendar
	 * @param lastDay The last day of the calendar
	 * @return An array of DayTimePoint, including firstDay (if business day), and all the business
	 * days up to lastDay included (if business day)
	 */
	public static DayTimePoint[] createDayTimePointCalendar(Date firstDay, Date lastDay) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(firstDay);
		Calendar last = Calendar.getInstance();
		last.setTime(lastDay);
		List<DayTimePoint> pointDateList = new ArrayList<DayTimePoint>();
		for (; cal.before(last); cal.add(Calendar.DATE, 1)) {
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				continue;
			pointDateList.add(new DayTimePoint(cal.getTime()));
		}
		return pointDateList.toArray(new DayTimePoint[pointDateList.size()]);
	}
}
