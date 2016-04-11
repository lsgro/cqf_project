package com.luigisgro.cqf.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.TermStructure;
import com.luigisgro.cqf.hjm.ForwardInterestRate;

public class TestForwardInterestRate {
	@Test
	public void testFullTenor() {
		Calendar cal = Calendar.getInstance();
		cal.set(2012, 0, 1);
		Date start = cal.getTime();
		cal.set(2013, 0, 1);
		Date end = cal.getTime();
		DayTimePoint[] timeSeriesCalendar = DayTimePoint.createDayTimePointCalendar(start, end);
		TermStructure termStructure = new TermStructure(new double[] { 0.1, 0.5, 1.5, 2.0 });
		CurveTimeSeries<DayTimePoint> scenario = new CurveTimeSeries<DayTimePoint>(termStructure, 1.0 / 252);
		int numOfTimeStep = 0;
		for (DayTimePoint timePoint : timeSeriesCalendar) {
			scenario.put(timePoint, new Curve(new double[] { 0.032 + 0.0001 * numOfTimeStep++, 0.034, 0.040, 0.043 }, termStructure));
		}
		ForwardInterestRate<DayTimePoint> irOp = new ForwardInterestRate<DayTimePoint>(timeSeriesCalendar[10], 2);
		double forwardInterestRate = irOp.evaluate(scenario);
		Assert.assertEquals((0.033 * 0.1 + 0.034 * 0.4 + 0.040 * 1.0 + 0.043 * 0.5) / 2, forwardInterestRate, 0.00001);
	}
	@Test
	public void testIntermediateTenor() {
		Calendar cal = Calendar.getInstance();
		cal.set(2012, 0, 1);
		Date start = cal.getTime();
		cal.set(2013, 0, 1);
		Date end = cal.getTime();
		DayTimePoint[] timeSeriesCalendar = DayTimePoint.createDayTimePointCalendar(start, end);
		TermStructure termStructure = new TermStructure(new double[] { 0.1, 0.5, 1.5, 2.0 });
		CurveTimeSeries<DayTimePoint> scenario = new CurveTimeSeries<DayTimePoint>(termStructure, 1.0 / 252);
		int numOfTimeStep = 0;
		for (DayTimePoint timePoint : timeSeriesCalendar) {
			scenario.put(timePoint, new Curve(new double[] { 0.032 + 0.0001 * numOfTimeStep++, 0.034, 0.040, 0.043 }, termStructure));
		}
		ForwardInterestRate<DayTimePoint> irOp = new ForwardInterestRate<DayTimePoint>(timeSeriesCalendar[10], 1.5);
		double forwardInterestRate = irOp.evaluate(scenario);
		Assert.assertEquals((0.033 * 0.1 + 0.034 * 0.4 + 0.040 * 1.0) / 1.5, forwardInterestRate, 0.00001);
	}
	@Test
	public void testFractionalTenor() {
		Calendar cal = Calendar.getInstance();
		cal.set(2012, 0, 1);
		Date start = cal.getTime();
		cal.set(2013, 0, 1);
		Date end = cal.getTime();
		DayTimePoint[] timeSeriesCalendar = DayTimePoint.createDayTimePointCalendar(start, end);
		TermStructure termStructure = new TermStructure(new double[] { 0.1, 0.5, 1.5, 2.0 });
		CurveTimeSeries<DayTimePoint> scenario = new CurveTimeSeries<DayTimePoint>(termStructure, 1.0 / 252);
		int numOfTimeStep = 0;
		for (DayTimePoint timePoint : timeSeriesCalendar) {
			scenario.put(timePoint, new Curve(new double[] { 0.032 + 0.0001 * numOfTimeStep++, 0.034, 0.040, 0.043 }, termStructure));
		}
		ForwardInterestRate<DayTimePoint> irOp = new ForwardInterestRate<DayTimePoint>(timeSeriesCalendar[10], 1.0);
		double forwardInterestRate = irOp.evaluate(scenario);
		Assert.assertEquals((0.033 * 0.1 + 0.034 * 0.4 + 0.040 * 0.5), forwardInterestRate, 0.00001);
	}

}
