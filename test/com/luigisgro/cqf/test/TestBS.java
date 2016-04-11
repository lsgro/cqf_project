package com.luigisgro.cqf.test;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.junit.Test;

import com.luigisgro.cqf.fdm.Option.Direction;
import com.luigisgro.cqf.util.BlackScholes;


public class TestBS {
	private static final NormalDistribution N = new NormalDistributionImpl();
	@Test
	public void test2c() throws IOException, MathException {
		double v = BlackScholes.vanillaOptionValue(100, 100, 1, 0.4, 0.04, Direction.CALL);
		System.out.println(v);
	}
	
	@Test
	public void test2p() throws IOException, MathException {
		double v = BlackScholes.vanillaOptionValue(100, 100, 1, 0.2, 0.04, Direction.PUT);
		System.out.println(v);
	}

	@Test
	public void test3() throws IOException, MathException {
		double d1 = BlackScholes.d1(69, 100, 0.0024, 0.2, 0.04);
		double d2 = BlackScholes.d2(69, 100, 0.0024, 0.2, 0.04);
		double n1 = N.cumulativeProbability(d1);
		double n2 = N.cumulativeProbability(d2);
		Assert.assertTrue(!Double.isInfinite(n1));
		Assert.assertTrue(!Double.isInfinite(n2));
	}

	@Test
	public void test4c() throws IOException, MathException {
		double v = BlackScholes.binaryCashOrNothingValue(100, 100, 1, 0.2, 0.04, Direction.CALL);
		System.out.println(v);
	}

	@Test
	public void test4p() throws IOException, MathException {
		double v = BlackScholes.binaryCashOrNothingValue(100, 100, 1, 0.2, 0.04, Direction.PUT);
		System.out.println(v);
	}
}
