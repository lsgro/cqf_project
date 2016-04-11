package com.luigisgro.cqf.test;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.TermStructure;
import com.luigisgro.cqf.hjm.SeriesOfOptions;
import com.luigisgro.cqf.hjm.Xlet;


public class TestSeriesOfOptions {
	@Test
	public void test() {
		Calendar present = Calendar.getInstance();
		present.set(2012, 0, 2);
		Calendar lastDay = Calendar.getInstance();
		lastDay.set(2022, 3, 1);
		DayTimePoint[] calendar = DayTimePoint.createDayTimePointCalendar(present.getTime(), lastDay.getTime());
		
		TermStructure termStructure = new TermStructure(new double[] { 0.1, 0.25, 0.50, 0.75, 1.0 });
		CurveTimeSeries<DayTimePoint> scenario = new CurveTimeSeries<DayTimePoint>(termStructure, 1.0 / 252);
		for (int i = 0; i < calendar.length; i++) {
			scenario.put(calendar[i], new Curve(new double[] { 0.03, 0.04, 0.05, 0.055, 0.054}, termStructure));
		}
		
		SeriesOfOptions<DayTimePoint> seriesOfOptions = new SeriesOfOptions<DayTimePoint>(0, 0.25, 10, calendar, 1.0/260) {
			@Override
			public Xlet<DayTimePoint> createXlet(DayTimePoint present, DayTimePoint evaluationTime, DayTimePoint cashflowTime, double tenor, double timeStep) {
				return new Xlet<DayTimePoint>(present, evaluationTime, cashflowTime, tenor, timeStep){
					@Override
					protected double payoff(double forwardInterestRate) {
						return 1; // a ZCB in disguise
					}};
			}};
			
		double theoreticalValue = 0.0;
		for (int i = 1; i < 41; i++) {
			theoreticalValue += Math.exp(-0.03 * i * 65 * 1.0/260) * 0.25; // Xlet pays 1/4 of the payoff each quarter
		}
		
		double value = seriesOfOptions.evaluate(scenario);
		
		Assert.assertEquals(theoreticalValue, value, 0.000001);
	}
}
