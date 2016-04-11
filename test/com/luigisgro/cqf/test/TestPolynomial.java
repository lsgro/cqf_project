package com.luigisgro.cqf.test;

import org.junit.Assert;
import org.junit.Test;

import com.luigisgro.cqf.function.Polynomial;


public class TestPolynomial {
	@Test
	public void testConstant() {
		Polynomial line = new Polynomial(new double[] { 12.5 });
		Assert.assertEquals(12.5, line.value(1e22), 0.00001);
	}
	@Test
	public void testLine() {
		Polynomial line = new Polynomial(new double[] { 3, 2 });
		Assert.assertEquals(7, line.value(2), 0.00001);
	}
	@Test
	public void testParabola() {
		Polynomial line = new Polynomial(new double[] { 1, 0, -1 });
		Assert.assertEquals(-3, line.value(2), 0.00001);
	}
	@Test
	public void testIntegrateLine() {
		Polynomial p = new Polynomial(new double[] { 0, 1 }); // f = x
		Polynomial integral = p.integrate();
		Assert.assertEquals(0.0, integral.getCoefficients()[0], 000.1);
		Assert.assertEquals(0.0, integral.getCoefficients()[1], 000.1);
		Assert.assertEquals(0.5, integral.getCoefficients()[2], 000.1);
		for (double x = 0.0; x < 10; x++) {
			Assert.assertEquals(p.value(x) * p.value(x) / 2, integral.value(x), 0.001);
		}
	}
	@Test
	public void testIntegrateThird() {
		Polynomial p = new Polynomial(new double[] { 12, -1, 3, 1 }); // f(x) = x^3 + 3x^2 - x + 12 -> i(x) = 1/4x^4 + x^3 - 1/2x^2 + 12x
		Polynomial integral = p.integrate();
		Assert.assertEquals(0.0, integral.getCoefficients()[0], 000.1);
		Assert.assertEquals(12.0, integral.getCoefficients()[1], 000.1);
		Assert.assertEquals(-0.5, integral.getCoefficients()[2], 000.1);
		Assert.assertEquals(1.0, integral.getCoefficients()[3], 000.1);
		Assert.assertEquals(0.25, integral.getCoefficients()[4], 000.1);
	}
	@Test
	public void testDerivateThird() {
		Polynomial p = new Polynomial(new double[] { 12, -1, 3, 1 }); // f(x) = x^3 + 3x^2 - x + 12 -> d(x) = 3x^2 + 6x -1
		Polynomial integral = p.derivate();
		Assert.assertEquals(-1.0, integral.getCoefficients()[0], 000.1);
		Assert.assertEquals(6.0, integral.getCoefficients()[1], 000.1);
		Assert.assertEquals(3.0, integral.getCoefficients()[2], 000.1);
	}
	@Test
	public void testIntegrateDerivative() {
		Polynomial p = new Polynomial(new double[] { 12, -1, 3, 1 });
		Polynomial q = p.integrate().derivate();
		Assert.assertEquals(p.getCoefficients()[0], q.getCoefficients()[0], 000.1);
		Assert.assertEquals(p.getCoefficients()[1], q.getCoefficients()[1], 000.1);
		Assert.assertEquals(p.getCoefficients()[2], q.getCoefficients()[2], 000.1);
		Assert.assertEquals(p.getCoefficients()[3], q.getCoefficients()[3], 000.1);
	}
	@Test
	public void testDefiniteIntegral() {
		Polynomial p = new Polynomial(new double[] { 0, 1 }); // a 45 degree line passing through 0,0
		Polynomial q = p.integrate();
		
		// calculate numeric integral between 10 and 20
		double theoreticalIntegral = 20.0 * 20.0 / 2.0 - 10.0 * 10.0 / 2.0;
		
		double calculatedIntegral = q.value(20) - q.value(10);
		
		Assert.assertEquals(theoreticalIntegral, calculatedIntegral, 0.000001);
	}
}
