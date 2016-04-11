package com.luigisgro.cqf.pca;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

import com.luigisgro.cqf.linalg.Matrix;
import com.luigisgro.cqf.linalg.MatrixBuffer;
import com.luigisgro.cqf.linalg.SymmetricMatrix;
import com.luigisgro.cqf.linalg.SymmetricMatrixBuffer;
import com.luigisgro.cqf.linalg.Vector;
import com.luigisgro.cqf.linalg.VectorBuffer;

/**
 * This stand-alone class implements the Jacobi rotation diagonalization of symmetric matrices.
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Jacobi_rotation">Jacobi Rotation Wikipedia article</a>
 * @author Luigi Sgro
 *
 */
public class JacobiDiagonalization {
	private SymmetricMatrixBuffer x;
	private Matrix eigenvectors;
	private VectorBuffer eigenvalues;
	private double epsilon;
	
	/**
	 * Creates a new Jacobi framework
	 * @param m Matrix to be diagonalized
	 * @param epsilon Maximum error to be tolerated for non-diagonal elements
	 */
	public JacobiDiagonalization(Matrix m, double epsilon) {
		if (! (m instanceof SymmetricMatrix))
			throw new IllegalArgumentException("Argument must implement SymmetricMatrix");
		this.x = SymmetricMatrixBuffer.copyOf(m);
		this.eigenvectors = Matrix.identity(m.numOfColumns());
		this.epsilon = epsilon;
	}
	
	/**
	 * 
	 * @return A Matrix containing the eigenvectors in the columns. Ordered if 
	 * sortEigenvaluesDesc() has been called
	 */
	public Matrix getEigenvectors() {
		return eigenvectors;
	}
	
	/**
	 * 
	 * @return A vector of the eigenvalues.Ordered if 
	 * sortEigenvaluesDesc() has been called
	 */
	public Vector getEigenvalues() {
		return eigenvalues;
	}
	
	/**
	 * @return The (approximately) diagonalized matrix
	 */
	public Matrix getTransformed() {
		return x;
	}
	
	/**
	 * After the diagonalization this method can be used to sort the eigenvector columns
	 * based on the descending order of the norm of their eigenvalues
	 */
	public void sortEigenvaluesDesc() {
		for (int i = 1; i < eigenvalues.dimension(); i++) {
			int j = i - 1;
			while (j >= 0 && Math.abs(eigenvalues.get(j)) < Math.abs(eigenvalues.get(j + 1))) {
				eigenvalues.swapElements(j, j + 1);
				((MatrixBuffer)eigenvectors).swapColumns(j, j + 1);
				j--;
			}
		}
	}
	
	/**
	 * Performs the diagonalization
	 * @param maxNumberOfIterations A maximum number of iterations to be carried out
	 * @return The actual number of iterations performed
	 */
	public int process(int maxNumberOfIterations) {
		int numberOfIterations = 0;
		while (numberOfIterations++ <= maxNumberOfIterations) {
			if (!sweep()) {
				eigenvalues = VectorBuffer.copyOf(x.getDiagonal());
				return numberOfIterations;
			}
		}
		throw new MathRuntimeException(LocalizedFormats.CONVERGENCE_FAILED);
	}
	
	private boolean sweep() {
		boolean anyElementProcessed = false;
		for (int row = 1; row < x.numOfRows(); row++) {
			for (int col = 0; col < row; col++) {
				if (Math.abs(x.get(row, col)) > epsilon) {
					anyElementProcessed = true;
					rotate(row, col);
				} else {
					x.set(row, col, 0);
				}
			}
		}
		return anyElementProcessed;
	}
	
	/**
	 * One rotation of the matrix. Not to be used in normal circumstances
	 * @param diag1 Subscript 1 of the off-diagonal cell to delete
	 * @param diag2 Subscript 2 of the off-diagonal cell to delete
	 */
	public void rotate(int diag1, int diag2) {
		if (diag1 == diag2)
			throw new IllegalArgumentException("The two subscripts are identical: " + diag1);
		int j, k;
		if (diag1 < diag2) {
			j = diag1;
			k = diag2;
		} else {
			j = diag2;
			k = diag1;
		}
		double beta = (x.get(k, k) - x.get(j, j)) / x.get(j, k) / 2;
		double t = (beta >= 0 ? 1 : -1) / (Math.abs(beta) + Math.sqrt(beta * beta + 1));
		double c = 1 / Math.sqrt(t * t + 1);
		double s = c * t;
		double rho = s / (1 + c);
		VectorBuffer newColJ = new VectorBuffer(x.numOfRows());
		VectorBuffer newColK = new VectorBuffer(x.numOfRows());
		Vector colJ = x.getColumn(j);
		Vector colK = x.getColumn(k);
		for (int l = 0; l < colJ.dimension(); l++) {
			if (l != j && l != k) { // skip diagonal and cross elements - calculated below
				newColJ.set(l, colJ.get(l) - s * (colK.get(l) + rho * colJ.get(l)));
				newColK.set(l, colK.get(l) + s * (colJ.get(l) - rho * colK.get(l)));
			}
		}
		double newElementJJ = x.get(j, j) - t * x.get(j, k);
		double newElementKK = x.get(k, k) + t * x.get(j, k);
		// apply changes
		for (int l = 0; l < newColJ.dimension(); l++) {
			if (l != j && l != k) {
				x.set(l, j, newColJ.get(l));
				x.set(l, k, newColK.get(l));
			}
		}
		x.set(j, j, newElementJJ);
		x.set(k, k, newElementKK);
		x.set(j, k, 0);
		// accumulate eigenvectors
		MatrixBuffer p = MatrixBuffer.copyOf(Matrix.identity(x.numOfColumns()));
		p.set(j, j, c);
		p.set(k, k, c);
		p.set(j, k, s);
		p.set(k, j, -s);
		eigenvectors = Matrix.product(eigenvectors, p);
	}
}
