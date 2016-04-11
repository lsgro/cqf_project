package com.luigisgro.cqf.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.TermStructure;
import com.luigisgro.cqf.hjm.ZCB;

public class TestDiscount {
	@Test
	public void test() {
		Calendar cal = Calendar.getInstance();
		cal.set(2012, 0, 1);
		Date start = cal.getTime();
		cal.set(2013, 0, 1);
		Date end = cal.getTime();
		DayTimePoint[] timeSeriesCalendar = DayTimePoint.createDayTimePointCalendar(start, end);
		TermStructure termStructure = new TermStructure(new double[] { 0.1, 0.5, 1.5, 2.0 });
		CurveTimeSeries<DayTimePoint> scenario = new CurveTimeSeries<DayTimePoint>(termStructure, 1.0 / 252);
		int numberOfSteps = 0;
		for (DayTimePoint timePoint : timeSeriesCalendar) {
			scenario.put(timePoint, new Curve(new double[] { 0.032 + 0.04 * numberOfSteps++ / (timeSeriesCalendar.length - 1), 0.034, 0.040, 0.043 }, termStructure));
		}
		ZCB<DayTimePoint> discOp = new ZCB<DayTimePoint>(timeSeriesCalendar[0], timeSeriesCalendar[timeSeriesCalendar.length - 1], 1.0 / (timeSeriesCalendar.length - 1));
		double discount = discOp.evaluate(scenario);
		Assert.assertEquals(-0.052, Math.log(discount), 0.001);
	}
}
