package com.luigisgro.cqf.test;

import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression;
import org.junit.Assert;
import org.junit.Test;


public class TestRegression {
	@Test
	public void testLine() {
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.setNoIntercept(true);
		double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
		double[][] x = new double[][] { // x^3, x^2, x, 1
				{0, 0, 0, 1},
				{1, 1, 1, 1},
				{8, 4, 2, 1},
				{27, 9, 3, 1},
				{64, 16, 4, 1},
				{125, 25, 5, 1}};
		regression.newSampleData(y, x);
		double[] beta = regression.estimateRegressionParameters();
		Assert.assertEquals(0, beta[0], 0.0001);
		Assert.assertEquals(0, beta[1], 0.0001);
		Assert.assertEquals(1, beta[2], 0.0001);
		Assert.assertEquals(11, beta[3], 0.0001);
	}
	@Test
	public void testParabola() {
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.setNoIntercept(true);
		double[] y = new double[]{6.0, 3.0, 2.0, 3.0, 6.0, 11.0};
		double[][] x = new double[][] { // x^3, x^2, x, 1
				{0, 0, 0, 1},
				{1, 1, 1, 1},
				{8, 4, 2, 1},
				{27, 9, 3, 1},
				{64, 16, 4, 1},
				{125, 25, 5, 1}};
		regression.newSampleData(y, x);
		double[] beta = regression.estimateRegressionParameters();
		Assert.assertEquals(0, beta[0], 0.0001);
		Assert.assertEquals(1, beta[1], 0.0001);
		Assert.assertEquals(-4, beta[2], 0.0001);
		Assert.assertEquals(6, beta[3], 0.0001);
	}
}
