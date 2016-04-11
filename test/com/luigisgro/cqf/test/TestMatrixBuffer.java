package com.luigisgro.cqf.test;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.linalg.Matrix;
import com.luigisgro.cqf.linalg.MatrixBuffer;


public class TestMatrixBuffer {
	@Test
	public void testElementSetting() {
		MatrixBuffer m = new MatrixBuffer(3, 3);
		m.set(0, 0, 35);
		Assert.assertEquals(35.0, m.get(0,0));
	}
	@Test
	public void testNumOfRows() {
		MatrixBuffer m = new MatrixBuffer(12, 2);
		Assert.assertEquals(12, m.numOfRows());
	}
	@Test
	public void testNumOfCols() {
		MatrixBuffer m = new MatrixBuffer(12, 2);
		Assert.assertEquals(2, m.numOfColumns());
	}
	@Test
	public void testCopyOf() {
		MatrixBuffer m = new MatrixBuffer(3, 3);
		
		m.set(0, 0, 23);
		m.set(0, 1, 56);
		m.set(0, 2, 11);

		m.set(1, 0, -1);
		m.set(1, 1, -10);
		m.set(1, 2, 22);

		m.set(2, 0, 0.5);
		m.set(2, 1, -0.666);
		m.set(2, 2, 0);
		
		Matrix copy = MatrixBuffer.copyOf(m);
		Assert.assertEquals(m, copy);
	}
	@Test
	public void testSwapColumns() {
		MatrixBuffer m = new MatrixBuffer(3, 3);
		
		m.set(0, 0, 23);
		m.set(0, 1, 56);
		m.set(0, 2, 11);

		m.set(1, 0, -1);
		m.set(1, 1, -10);
		m.set(1, 2, 22);

		m.set(2, 0, 0.5);
		m.set(2, 1, -0.666);
		m.set(2, 2, 0);
		
		m.swapColumns(0, 2);
		Assert.assertEquals(11.0, m.get(0, 0));
		Assert.assertEquals(0.5, m.get(2, 2));
		Assert.assertEquals(56.0, m.get(0, 1));
	}
}
