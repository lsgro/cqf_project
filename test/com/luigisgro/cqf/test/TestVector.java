package com.luigisgro.cqf.test;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.linalg.Vector;
import com.luigisgro.cqf.linalg.VectorBuffer;


public class TestVector {
	@Test
	public void testEqualOfEquals() {
		VectorBuffer v1 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		VectorBuffer v2 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		Assert.assertEquals(v1, v2);
		Assert.assertEquals(v2, v1);
	}
	@Test
	public void testEqualOfDifferent() {
		VectorBuffer v1 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		VectorBuffer v2 = new VectorBuffer(new double[] { 1, 2, 3, 4, 666 });
		Assert.assertFalse(v1.equals(v2));
		Assert.assertFalse(v2.equals(v1));
	}
	@Test
	public void testHashCode() {
		VectorBuffer v1 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		VectorBuffer v2 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		Assert.assertEquals(v1.hashCode(), v2.hashCode());
	}
	@Test
	public void testMean() {
		VectorBuffer v = new VectorBuffer(new double[] { 1.9, 2, 3, 4, 4.1 });
		Assert.assertEquals(3.0, Vector.mean(v));
	}
	@Test
	public void testScale() {
		VectorBuffer v = new VectorBuffer(new double[] { 1.9, 2, 3, 4, 4.1 });
		Assert.assertEquals(new VectorBuffer(new double[] { 1.9 * 1.5, 2 * 1.5, 3 * 1.5, 4 * 1.5, 4.1 * 1.5 }), v.scale(1.5));
	}
	@Test
	public void testDotProduct() {
		VectorBuffer v1 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		VectorBuffer v2 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		Assert.assertEquals(1.0 + 2*2 + 3*3 + 4*4 + 5*5, Vector.dotProduct(v1, v2));
	}
	@Test
	public void testCovariance1() {
		VectorBuffer v1 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		VectorBuffer v2 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		Assert.assertEquals(2.0, Vector.covariance(v1, v2));
	}
	@Test
	public void testCovariance2() {
		VectorBuffer v1 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		VectorBuffer v2 = new VectorBuffer(new double[] { 5, 4, 3, 2, 1 });
		Assert.assertEquals(-2.0, Vector.covariance(v1, v2));
	}
	@Test
	public void testCovariance3() {
		VectorBuffer v1 = new VectorBuffer(new double[] { 1, 2, 3, 4, 5 });
		VectorBuffer v2 = new VectorBuffer(new double[] { 1, 1, 1, 1, 1 });
		Assert.assertEquals(0.0, Vector.covariance(v1, v2));
	}
}
