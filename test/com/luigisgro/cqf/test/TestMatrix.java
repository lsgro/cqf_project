package com.luigisgro.cqf.test;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.linalg.Matrix;
import com.luigisgro.cqf.linalg.MatrixBuffer;
import com.luigisgro.cqf.linalg.Vector;
import com.luigisgro.cqf.linalg.VectorBuffer;


public class TestMatrix {
	@Test
	public void testRow() {
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
		
		Vector row = new VectorBuffer(new double[]{ -1, -10, 22});
		Assert.assertEquals(row, m.getRow(1));
	}
	@Test
	public void testColumn() {
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
		
		Vector col = new VectorBuffer(new double[]{ 11, 22, 0});
		Assert.assertEquals(col, m.getColumn(2));
	}
	@Test
	public void testHashCode() {
		MatrixBuffer m1 = new MatrixBuffer(3, 3);
		
		m1.set(0, 0, 23);
		m1.set(0, 1, 56);
		m1.set(0, 2, 11);

		m1.set(1, 0, -1);
		m1.set(1, 1, -10);
		m1.set(1, 2, 22);

		m1.set(2, 0, 0.5);
		m1.set(2, 1, -0.666);
		m1.set(2, 2, 0);
		
		MatrixBuffer m2 = new MatrixBuffer(3, 3);
		
		m2.set(0, 0, 23);
		m2.set(0, 1, 56);
		m2.set(0, 2, 11);

		m2.set(1, 0, -1);
		m2.set(1, 1, -10);
		m2.set(1, 2, 22);

		m2.set(2, 0, 0.5);
		m2.set(2, 1, -0.666);
		m2.set(2, 2, 0);
	
		Assert.assertEquals(m1.hashCode(), m2.hashCode());
	}
	@Test
	public void testEqualsOfEquals() {
		MatrixBuffer m1 = new MatrixBuffer(3, 3);
		
		m1.set(0, 0, 23);
		m1.set(0, 1, 56);
		m1.set(0, 2, 11);

		m1.set(1, 0, -1);
		m1.set(1, 1, -10);
		m1.set(1, 2, 22);

		m1.set(2, 0, 0.5);
		m1.set(2, 1, -0.666);
		m1.set(2, 2, 0);
		
		MatrixBuffer m2 = new MatrixBuffer(3, 3);
		
		m2.set(0, 0, 23);
		m2.set(0, 1, 56);
		m2.set(0, 2, 11);

		m2.set(1, 0, -1);
		m2.set(1, 1, -10);
		m2.set(1, 2, 22);

		m2.set(2, 0, 0.5);
		m2.set(2, 1, -0.666);
		m2.set(2, 2, 0);
	
		Assert.assertEquals(m1, m2);
		Assert.assertEquals(m2, m1);
	}
	@Test
	public void testEqualsOfDifferent() {
		MatrixBuffer m1 = new MatrixBuffer(3, 3);
		
		m1.set(0, 0, 23);
		m1.set(0, 1, 56);
		m1.set(0, 2, 11);

		m1.set(1, 0, -1);
		m1.set(1, 1, -10);
		m1.set(1, 2, 22);

		m1.set(2, 0, 0.5);
		m1.set(2, 1, -0.666);
		m1.set(2, 2, 0);
		
		MatrixBuffer m2 = new MatrixBuffer(3, 3);
		
		m2.set(0, 0, 23);
		m2.set(0, 1, 56);
		m2.set(0, 2, 11);

		m2.set(1, 0, -1);
		m2.set(1, 1, -10);
		m2.set(1, 2, 22);

		m2.set(2, 0, 0.5);
		m2.set(2, 1, -0.666);
		m2.set(2, 2, 1);
	
		Assert.assertFalse(m1.equals(m2));
		Assert.assertFalse(m2.equals(m1));
	}
	@Test
	public void testRowDifference() {
		MatrixBuffer m1 = new MatrixBuffer(4, 3);
		
		m1.set(0, 0, 23);
		m1.set(0, 1, 56);
		m1.set(0, 2, 11);

		m1.set(1, 0, -1);
		m1.set(1, 1, -10);
		m1.set(1, 2, 22);

		m1.set(2, 0, 0.5);
		m1.set(2, 1, -0.666);
		m1.set(2, 2, 0);
		
		m1.set(3, 0, 3);
		m1.set(3, 1, 2);
		m1.set(3, 2, 1);
		
		Matrix m2 = Matrix.rowDifference(m1);
		
		Assert.assertEquals(new VectorBuffer(new double[] { -24, -66, 11 }), m2.getRow(0));
	}
	@Test
	public void testScale() {
		MatrixBuffer m1 = new MatrixBuffer(4, 3);
		
		m1.set(0, 0, 23);
		m1.set(0, 1, 56);
		m1.set(0, 2, 11);

		m1.set(1, 0, -1);
		m1.set(1, 1, -10);
		m1.set(1, 2, 22);

		m1.set(2, 0, 0.5);
		m1.set(2, 1, -0.666);
		m1.set(2, 2, 0);
		
		m1.set(3, 0, 3);
		m1.set(3, 1, 2);
		m1.set(3, 2, 1);
		
		Matrix m2 = m1.scale(2.5);
		
		Assert.assertEquals(new VectorBuffer(new double[] { 23 * 2.5, 56 * 2.5, 11 * 2.5 }), m2.getRow(0));
	}
	@Test
	public void testColumnCovariance() {
		MatrixBuffer m1 = new MatrixBuffer(3, 4);
		
		m1.set(0, 0, 1);
		m1.set(0, 1, -1);
		m1.set(0, 2, 2);
		m1.set(0, 3, -2);
		
		m1.set(1, 0, 1);
		m1.set(1, 1, -1);
		m1.set(1, 2, 2);
		m1.set(1, 3, -2);
		
		m1.set(2, 0, 1);
		m1.set(2, 1, -1);
		m1.set(2, 2, 2);
		m1.set(2, 3, -2);
		
		Matrix m2 = Matrix.columnCovariance(m1);
		
		Assert.assertEquals(4, m2.numOfColumns());
		Assert.assertEquals(4, m2.numOfRows());
		Assert.assertEquals(Vector.covariance(m1.getColumn(0), m1.getColumn(1)), m2.get(0, 1));
	}
	@Test
	public void testProduct1() {
		MatrixBuffer m1 = new MatrixBuffer(3, 3);
		
		m1.set(0, 0, 23);
		m1.set(0, 1, 56);
		m1.set(0, 2, 11);

		m1.set(1, 0, -1);
		m1.set(1, 1, -10);
		m1.set(1, 2, 22);

		m1.set(2, 0, 0.5);
		m1.set(2, 1, -0.666);
		m1.set(2, 2, 0);
		
		MatrixBuffer m2 = new MatrixBuffer(3, 3);
		
		m2.set(0, 0, 23);
		m2.set(0, 1, 56);
		m2.set(0, 2, 11);

		m2.set(1, 0, -1);
		m2.set(1, 1, -10);
		m2.set(1, 2, 22);

		m2.set(2, 0, 0.5);
		m2.set(2, 1, -0.666);
		m2.set(2, 2, 1);
		
		Matrix m3 = Matrix.product(m1, m2);
	
		Assert.assertEquals(m3.get(1, 2), Vector.dotProduct(m1.getRow(1), m2.getColumn(2)));
	}
	@Test
	public void testProduct2() {
		Matrix m1 = Matrix.identity(4);
		Matrix m2 = Matrix.identity(4);
		Assert.assertEquals(Matrix.identity(4), Matrix.product(m1, m2));
	}
	@Test
	public void testProduct3() {
		MatrixBuffer m1 = new MatrixBuffer(1, 1);
		MatrixBuffer m2 = new MatrixBuffer(1, 1);
		m1.set(0, 0, 2);
		m2.set(0, 0, -12);
		Assert.assertEquals(-24.0, Matrix.product(m1, m2).get(0,0));
	}
	@Test
	public void testTransposed() {
		MatrixBuffer m1 = new MatrixBuffer(4, 3);
		
		m1.set(0, 0, 23);
		m1.set(0, 1, 56);
		m1.set(0, 2, 11);

		m1.set(1, 0, -1);
		m1.set(1, 1, -10);
		m1.set(1, 2, 22);

		m1.set(2, 0, 0.5);
		m1.set(2, 1, -0.666);
		m1.set(2, 2, 0);
		
		m1.set(3, 0, 25);
		m1.set(3, 1, -1);
		m1.set(3, 2, 4);
		
		Matrix m2 = m1.transposed();
		
		Assert.assertEquals(m1.numOfColumns(), m2.numOfRows());
		Assert.assertEquals(m1.getColumn(2), m2.getRow(2));
	}	
}
