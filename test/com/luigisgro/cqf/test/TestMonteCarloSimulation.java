package com.luigisgro.cqf.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.CurveTimeSeriesOperator;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.TermStructure;
import com.luigisgro.cqf.montecarlo.CurveTimeSeriesOperatorEvaluator;
import com.luigisgro.cqf.montecarlo.MonteCarloEvaluator;
import com.luigisgro.cqf.montecarlo.MonteCarloSimulation;
import com.luigisgro.cqf.montecarlo.MultiFactorModel;
import com.luigisgro.cqf.montecarlo.RandomVectorGenerator;


public class TestMonteCarloSimulation {
	TermStructure termStructure = new TermStructure(new double[] { 0.5, 1.0, 1.5, 2.0 });
	DayTimePoint[] pointDates;
	double timeStep;
	
	RandomVectorGenerator generator = new RandomVectorGenerator() {
		@Override
		public double[] generateNextVector() {
			return new double[] { 0.1, 0.1, 0.1, 0.1 };
		}
	};
	
	MultiFactorModel model = new MultiFactorModel() {
		@Override
		public Curve nextCurve(Curve currentCurve, double[] randomVector, double timeStep) {
			double[] points = new double[termStructure.getTenors().length];
			for (int i = 0; i < termStructure.getTenors().length; i++) {
				points[i] = currentCurve.getPoints()[i] + randomVector[i];
			}
			return new Curve(points, termStructure);
		}
		@Override
		public int getDimension() {
			return 4;
		}
	};
	
	@Before
	public void createPointDates() {
		Calendar cal = Calendar.getInstance();
		Calendar oneYearLater = Calendar.getInstance();
		oneYearLater.add(Calendar.YEAR, 1);
		List<DayTimePoint> dates = new ArrayList<DayTimePoint>();
		for (; cal.before(oneYearLater); cal.add(Calendar.DATE, 1)) {
			dates.add(new DayTimePoint(cal.getTime()));
		}
		pointDates = dates.toArray(new DayTimePoint[0]);
		timeStep = 1 / pointDates.length;
	}
	
	@Test
	public void test() {
		MonteCarloEvaluator<DayTimePoint> evaluator = new CurveTimeSeriesOperatorEvaluator<DayTimePoint>(new CurveTimeSeriesOperator<DayTimePoint>() {
			@Override
			public double evaluate(CurveTimeSeries<DayTimePoint> curveTimeSeries) {
				return 10;
			}
		}, 10);
		MonteCarloSimulation<DayTimePoint> sim = new MonteCarloSimulation<DayTimePoint>(model, evaluator, generator, new Curve(new double[] { 0, 0, 0, 0 }, termStructure), pointDates, timeStep);
		Assert.assertEquals(10, sim.simulate());
		Assert.assertEquals(10, evaluator.getResult(), 0.00000001);
	}
}
