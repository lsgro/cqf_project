package com.luigisgro.cqf.test;

import org.junit.Assert;
import org.junit.Test;

import com.luigisgro.cqf.linalg.Matrix;
import com.luigisgro.cqf.linalg.SymmetricMatrixBuffer;
import com.luigisgro.cqf.pca.JacobiDiagonalization;


public class TestJacobiDiagonalization {
	@Test
	public void testRotation() {
		SymmetricMatrixBuffer m = new SymmetricMatrixBuffer(5);
		
		m.set(0, 0, 1);
		
		m.set(1, 0, 12);
		m.set(1, 1, 4);
		
		m.set(2, 0, 1);
		m.set(2, 1, 12);
		m.set(2, 2, -1);
		
		m.set(3, 0, -.05);
		m.set(3, 1, 0);
		m.set(3, 2, -10);
		m.set(3, 3, 9);
		
		m.set(4, 0, 1);
		m.set(4, 1, 12);
		m.set(4, 2, -7);
		m.set(4, 3, -59);
		m.set(4, 4, 3);
		
		JacobiDiagonalization jd = new JacobiDiagonalization(m, 0);
		jd.rotate(2, 4);
		System.out.println(jd.getTransformed());
	}
	@Test
	public void testDiagonalization() {
		SymmetricMatrixBuffer m = new SymmetricMatrixBuffer(3);
		
		m.set(0, 0, 3);
		
		m.set(1, 0, 1);
		m.set(1, 1, 3);
		
		m.set(2, 0, -1);
		m.set(2, 1, -1);
		m.set(2, 2, 5);
		
		JacobiDiagonalization jd = new JacobiDiagonalization(m, 0.001);
		
		int numIterations = jd.process(1000);
		
		System.out.println("Finished in: " + numIterations + " iterations");
		System.out.println(jd.getTransformed());
		System.out.println(jd.getEigenvectors());
		
		Matrix eigenTransform1 = Matrix.product(m, jd.getEigenvectors().getColumn(0));
		Matrix eigenTransform2 = Matrix.product(m, jd.getEigenvectors().getColumn(1));
		Matrix eigenTransform3 = Matrix.product(m, jd.getEigenvectors().getColumn(2));
		
		Matrix scaledVector1 = jd.getEigenvectors().getColumn(0).scale(jd.getTransformed().get(0, 0));
		Matrix scaledVector2 = jd.getEigenvectors().getColumn(1).scale(jd.getTransformed().get(1, 1));
		Matrix scaledVector3 = jd.getEigenvectors().getColumn(2).scale(jd.getTransformed().get(2, 2));
		
		for (int i = 0; i < eigenTransform1.numOfRows(); i++) {
			Assert.assertEquals(scaledVector1.get(i, 0), eigenTransform1.get(i, 0), 0.0001);
			Assert.assertEquals(scaledVector2.get(i, 0), eigenTransform2.get(i, 0), 0.0001);
			Assert.assertEquals(scaledVector3.get(i, 0), eigenTransform3.get(i, 0), 0.0001);
		}
		
		jd.sortEigenvaluesDesc();

		System.out.println(jd.getEigenvalues());
		System.out.println(jd.getEigenvectors());

		for (int i = 1; i < jd.getEigenvalues().dimension(); i++)
			Assert.assertTrue(jd.getEigenvalues().get(i - 1) >= jd.getEigenvalues().get(i));
	}
}
