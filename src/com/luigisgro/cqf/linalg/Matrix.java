package com.luigisgro.cqf.linalg;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import com.luigisgro.cqf.util.CSVExportable;

/**
 * Definiton of the basic properties of a matrix, with the implementation of some matrix operations.
 * The basic implementation is immutable (read-only)
 * Lazy evaluation is used when convenient (transpose, getRow, getColumn)
 * All dimension range to 0 to size - 1
 * @author Luigi Sgro
 *
 */
public abstract class Matrix implements CSVExportable {
	
	/**
	 * 
	 * @return The number of rows of the matrix
	 */
	public abstract int numOfRows();
	
	/**
	 * 
	 * @return The number of columns of the matrix
	 */
	public abstract int numOfColumns();
	
	/**
	 * 
	 * @param row Row of the element
	 * @param col Column of the element
	 * @return The value of the elements at row, column
	 */
	public abstract double get(int row, int col);
	
	/**
	 * Return a lazily evaluated {@link Vector} corresponding to a matrix row
	 * @param row The index of the row (0 to numOfRows() - 1)
	 * @return An immutable {@link Vector}
	 */
	public Vector getRow(final int row) {
		return new Vector() {
			@Override
			public double get(int i) {
				return Matrix.this.get(row, i);
			}
			@Override
			public int dimension() {
				return Matrix.this.numOfColumns();
			}};
	}

	/**
	 * Return a lazily evaluated {@link Vector} corresponding to a matrix column
	 * @param col The index of the column (0 to numOfColumns() - 1)
	 * @return An immutable {@link Vector}
	 */
	public Vector getColumn(final int col) {
		return new Vector() {
			@Override
			public double get(int i) {
				return Matrix.this.get(i, col);
			}
			@Override
			public int dimension() {
				return Matrix.this.numOfRows();
			}};
	}

	/**
	 * Returns a new matrix with all elements multiplied by factor k
	 * @param k The scaling factor
	 * @return A new Matrix
	 */
	public Matrix scale(double k) {
		MatrixBuffer result = new MatrixBuffer(numOfRows(), numOfColumns());
		for (int i = 0; i < result.numOfRows(); i++)
			for (int j = 0; j < result.numOfColumns(); j++)
				result.set(i, j, get(i, j) * k);
		return result;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Matrix))
			return false;
		Matrix m = (Matrix)o;
		if (m.numOfRows() != numOfRows())
			return false;
		for (int j = 0; j < numOfRows(); j++)
			for (int k = 0; k < numOfColumns(); k++)
				if (! (m.get(j, k) == get(j, k)))
					return false;
		return true;
	}
	
	public int hashCode() {
		int tmp = 0;
		for (int j = 0; j < numOfRows(); j++) {
			tmp *= 37;
			for (int k = 0; k < numOfColumns(); k++) {
				tmp += get(j, k) * 37;
			}
		}
		return (tmp * 37 + numOfRows());
	}
	
	public String toString() {
		String desc = "[";
		for (int i = 0; i < numOfRows() - 1; i++)
			desc += getRow(i) + ", ";
		desc += getRow(numOfRows() - 1) + "]";
		return desc;
	}
	
	/**
	 * @return An immutable Matrix which is the transposed of the original
	 */
	public Matrix transposed() {
		return new Matrix() {
			@Override
			public int numOfRows() {
				return Matrix.this.numOfColumns();
			}
			@Override
			public int numOfColumns() {
				return Matrix.this.numOfRows();
			}
			@Override
			public double get(int row, int col) {
				return Matrix.this.get(col, row);
			}};
	}
	
	/**
	 * Utility method returning an immutable square identity matrix of specific dimension
	 * @param size The size of the matrix
	 * @return An immutable identity matrix (Axx = 1, Axy = 0 for x != y)
	 */
	public static Matrix identity(final int size) {
		return new Matrix() {
			@Override
			public int numOfRows() {
				return size;
			}
			@Override
			public int numOfColumns() {
				return size;
			}
			@Override
			public double get(int row, int col) {
				return row == col ? 1 : 0;
			}};	
	}

	/**
	 * Utility method calculating difference between rows
	 * @param matrix The original matrix
	 * @return A new matrix with numOfRows() - 1 rows, each the difference of two subsequent rows of the
	 * original matrix
	 */
	public static Matrix rowDifference(Matrix matrix) {
		MatrixBuffer result = new MatrixBuffer(matrix.numOfRows() - 1, matrix.numOfColumns());
		for (int i = 0; i < result.numOfRows(); i++)
			for (int j = 0; j < result.numOfColumns(); j++)
				result.set(i, j, matrix.get(i + 1, j) - matrix.get(i, j));
		return result;
	}
	
	/**
	 * Product of matrices
	 * @param m1 Matrix 1
	 * @param m2 Matrix 2
	 * @return A new matrix which is the product of m1 and m2
	 */
	public static Matrix product(Matrix m1, Matrix m2) {
		if (m1.numOfColumns() != m2.numOfRows())
			throw new IllegalArgumentException("# of columns in m1 must equal # of rows in m2");
		MatrixBuffer result = new MatrixBuffer(m1.numOfRows(), m2.numOfColumns());
		for (int row = 0; row < result.numOfRows(); row++) {
			for (int col = 0; col < result.numOfColumns(); col++) {
				result.set(row, col, Vector.dotProduct(m1.getRow(row), m2.getColumn(col)));
			}
		}
		return result;
	}
	
	/**
	 * Calculates the covariance matrix of the columns of the input matrix
	 * @param matrix The columns
	 * @return The covariance matrix of the columns in input
	 */
	public static Matrix columnCovariance(Matrix matrix) {
		SymmetricMatrixBuffer result = new SymmetricMatrixBuffer(matrix.numOfColumns());
		for (int i = 0; i < result.numOfColumns(); i++)
			for (int j = 0; j <= i; j++)
				result.set(i, j, Vector.covariance(matrix.getColumn(i), matrix.getColumn(j)));
		return result;
	}
	
	/**
	 * Utility method to pretty print the matrix contents
	 * @param os The {@link java.io.PrintWriter} to be used
	 */
	public void print(OutputStream os) {
		PrintWriter printer = new PrintWriter(os);
		for (int j = 0; j < numOfRows(); j++)
			printer.println(getRow(j));
		printer.flush();
	}
	
	@Override
	public int writeLinesToCSV(Writer writer, String separator) throws IOException {
		int noOfLines = 0;
		for (int i = 0; i < numOfRows(); i++) {
			noOfLines += getRow(i).writeLinesToCSV(writer, separator);
		}
		return noOfLines;
	}
}
