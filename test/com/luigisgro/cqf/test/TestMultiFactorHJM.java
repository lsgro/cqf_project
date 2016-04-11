package com.luigisgro.cqf.test;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.TermStructure;
import com.luigisgro.cqf.function.Polynomial;
import com.luigisgro.cqf.hjm.MultiFactorHJM;


public class TestMultiFactorHJM {
	@Test
	public void testMonoFactor() {
		
		final double constantVol = 2.0;
		final double randomScalar = 0.5;
		final double timeStep = 0.36;
		final double squareRootOfTimeStep = 0.6;

		final double[] randomVector = new double[] { randomScalar };
		
		TermStructure termStructure = new TermStructure(new double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7 });
		Curve curve = new Curve(new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0 }, termStructure);
		Polynomial volatilityFunction = new Polynomial(new double[] { constantVol }); // Constant = 2
		MultiFactorHJM hjm = new MultiFactorHJM(new Polynomial[] { volatilityFunction }, termStructure);
		
		// theoretical values for next curve;
		// next = point + ( vol * integ(vol) + d(curve)/d(tau) ) * timeStep + vol * randomVector * SQRT(timeStep)
		int numberOfPoints = termStructure.getTenors().length;
		double[] theoreticalCurve = new double[numberOfPoints];
		for (int t = 0; t < numberOfPoints; t++) {
			double tenor = termStructure.getTenors()[t];
			double integralOfVolatility = constantVol * tenor; // integral of constant = const * x
			double dFdTau = 10.0; // curve values are 10 times the tenors
			if (t == numberOfPoints - 1)
				dFdTau = 0; // derivative can not be calculated at the right extreme
			theoreticalCurve[t] = curve.getPoints()[t] + ( constantVol * integralOfVolatility + dFdTau ) * timeStep + constantVol * randomScalar * squareRootOfTimeStep;
		}
		
		Curve calculatedCurve = hjm.nextCurve(curve, randomVector, timeStep);
		
		for (int t = 0; t < numberOfPoints; t++) {
			//System.out.println("Tenor: " + termStructure.getTenors()[t] + "; Theoretical: " + theoreticalCurve[t] + "; Calculated: " + calculatedCurve.getPoints()[t]);
			Assert.assertEquals(theoreticalCurve[t], calculatedCurve.getPoints()[t], 0.000001);
		}
	}
}
