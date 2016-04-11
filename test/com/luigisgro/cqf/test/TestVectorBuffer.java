package com.luigisgro.cqf.test;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.linalg.VectorBuffer;


public class TestVectorBuffer {
	@Test
	public void testElementSetting() {
		VectorBuffer v = new VectorBuffer(10);
		v.set(0, 35);
		Assert.assertEquals(35.0, v.get(0));
	}
	@Test
	public void testDimension() {
		VectorBuffer v = new VectorBuffer(10);
		Assert.assertEquals(10, v.dimension());
	}
}
