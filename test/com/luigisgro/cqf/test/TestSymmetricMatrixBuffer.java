package com.luigisgro.cqf.test;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.linalg.Matrix;
import com.luigisgro.cqf.linalg.SymmetricMatrixBuffer;


public class TestSymmetricMatrixBuffer {
	@Test
	public void testElementSetting() {
		SymmetricMatrixBuffer m = new SymmetricMatrixBuffer(3);
		m.set(0, 2, 35);
		Assert.assertEquals(35.0, m.get(0,2));
		Assert.assertEquals(35.0, m.get(2,0));
	}
	@Test
	public void testNumOfRows() {
		SymmetricMatrixBuffer m = new SymmetricMatrixBuffer(5);
		Assert.assertEquals(5, m.numOfRows());
	}
	@Test
	public void testNumOfCols() {
		SymmetricMatrixBuffer m = new SymmetricMatrixBuffer(4);
		Assert.assertEquals(4, m.numOfColumns());
	}
	@Test
	public void testCopyOf() {
		SymmetricMatrixBuffer m = new SymmetricMatrixBuffer(3);
		
		m.set(0, 0, 23);

		m.set(1, 0, -1);
		m.set(1, 1, -10);

		m.set(2, 0, 0.5);
		m.set(2, 1, -0.666);
		m.set(2, 2, 0);
		
		Matrix copy = SymmetricMatrixBuffer.copyOf(m);
		Assert.assertEquals(m, copy);
	}
}
