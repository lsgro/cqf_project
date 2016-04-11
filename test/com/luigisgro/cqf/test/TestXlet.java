package com.luigisgro.cqf.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.TermStructure;
import com.luigisgro.cqf.curve.TimePoint;
import com.luigisgro.cqf.hjm.Xlet;

public class TestXlet {
	class Caplet<T extends TimePoint> extends Xlet<T> {
		private double strike;
		public Caplet(double strike, T present, T evaluationTime, T cashflowTime, double tenor, double timeStep) {
			super(present, evaluationTime, cashflowTime, tenor, timeStep);
			this.strike = strike;
		}
		@Override
		protected double payoff(double forwardInterestRate) {
			return Math.max(forwardInterestRate - strike, 0);
		}
	}
	
	@Test
	public void test() {
		Calendar cal = Calendar.getInstance();
		cal.set(2012, 0, 2);
		Date start = cal.getTime();
		cal.set(2013, 0, 1);
		Date end = cal.getTime();		
		DayTimePoint[] timeSeriesCalendar = DayTimePoint.createDayTimePointCalendar(start, end);
		
		TermStructure termStructure = new TermStructure(new double[] { 0.1, 0.25, 0.5, 0.75, 1.0 });
		
		CurveTimeSeries<DayTimePoint> scenario = new CurveTimeSeries<DayTimePoint>(termStructure, 1.0 / 252);
		
		int numberOfSteps = 0;
		for (DayTimePoint timePoint : timeSeriesCalendar) {
			double increment = numberOfSteps * 0.00001;
			scenario.put(timePoint, new Curve(new double[] {
					0.02 + increment,
					0.025 + increment,
					0.026 + increment,
					0.027 + increment,
					0.028 + increment}, termStructure));
			numberOfSteps++;
		}
		
		DayTimePoint present = new DayTimePoint(start);
		cal.set(2012, 3, 2); // April 2, 2012 [65] - rates: 
		DayTimePoint evaluationTime = new DayTimePoint(cal.getTime());
		cal.set(2012, 6, 2); // July 2, 2012 [130]
		DayTimePoint cashflowTime = new DayTimePoint(cal.getTime());
		
		Caplet<DayTimePoint> capletOp = new Caplet<DayTimePoint>(0.02, present, evaluationTime, cashflowTime, 0.25, 1.0/260);
		
		double threeMonthsRateOnApril2 = ((0.02 + (65 * 0.00001)) * 0.1 + (0.025 + (65 * 0.00001)) * 0.15) / 0.25;
		double averageInstantRateToJuly2 = (0.02 + (130 * 0.00001) / 2) * 0.5;

		double value = capletOp.evaluate(scenario);
		
		Assert.assertEquals((threeMonthsRateOnApril2 - 0.02) * 0.25 /* tenor */ * Math.exp(-averageInstantRateToJuly2), value, 0.00001);
	}
}
