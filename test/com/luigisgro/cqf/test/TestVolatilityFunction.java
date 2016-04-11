package com.luigisgro.cqf.test;

import org.junit.Assert;
import org.junit.Test;

import com.luigisgro.cqf.function.Polynomial;


public class TestVolatilityFunction {
	@Test
	public void test() {
		Polynomial p = new Polynomial(new double[] { 10, 0, -1 }); // p(x) = -x^2 + 10
		Polynomial f = Polynomial.scale(p, 5); // f(x) = -5x^2 + 50
		Assert.assertEquals(5, f.value(3), 0.0001); // f(3) = -5 * 3^2 + 50 = 5
	}
}
