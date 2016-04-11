package com.luigisgro.cqf.test;

import org.junit.Assert;
import org.junit.Test;

import com.luigisgro.cqf.curve.CurveApproximatingPolynomial;
import com.luigisgro.cqf.curve.TermStructure;


public class TestPolynomialApproximatedCurve {
	@Test
	public void test() {
		TermStructure ts = new TermStructure(new double[] { 0.1, 0.5, 1, 2, 5, 10 });
		double[] points = new double[] { 0.010, 0.011, 0.9, 0.12, 0.8, 0.7 };
		CurveApproximatingPolynomial pac = new CurveApproximatingPolynomial(points, ts, 4);
		for (int i = 0; i < pac.getCoefficients().length; i++)
			System.out.println("" + i + "->" + pac.getCoefficients()[i]);
		for (int i = 0; i < ts.getTenors().length; i++) {
			double y = pac.value(ts.getTenors()[i]);
			System.out.println("" + y + " ~ " + points[i]);
			Assert.assertEquals(points[i], y, 1.5);
		}
	}
}
