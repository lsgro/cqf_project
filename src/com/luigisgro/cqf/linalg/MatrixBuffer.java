package com.luigisgro.cqf.linalg;

/**
 * Modifiable {@link Matrix}
 * @author Luigi Sgro
 *
 */
public class MatrixBuffer extends Matrix {
	private double[][] values;
	
	/**
	 * 
	 * @param numOfRows Number of rows
	 * @param numOfCols Number of columns
	 */
	public MatrixBuffer(int numOfRows, int numOfCols) {
		values = new double[numOfRows][];
		for (int i = 0; i < numOfRows; i++) {
			values[i] = new double[numOfCols];
		}
	}

	@Override
	public int numOfRows() {
		return values.length;
	}

	@Override
	public int numOfColumns() {
		return values[0].length;
	}

	@Override
	public double get(int row, int col) {
		return values[row][col];
	}
	
	public void set(int row, int col, double value) {
		values[row][col] = value;
	}
	
	/**
	 * Utility method to be used for sorting purposes. It swaps columns A and B
	 * @param k1 Index of column A to be swapped
	 * @param k2 Index of column B to be swapped
	 */
	public void swapColumns(int k1, int k2) {
		VectorBuffer tmpCol = VectorBuffer.copyOf(getColumn(k1));
		for (int j = 0; j < numOfRows(); j++) {
			set(j, k1, get(j, k2));
			set(j, k2, tmpCol.get(j));
		}
	}
	
	/**
	 * Copy method. It generates a clone of matrix m
	 * @param m Matrix to be cloned
	 * @return A new MatrixBuffer object
	 */
	public static MatrixBuffer copyOf(Matrix m) {
		MatrixBuffer x = new MatrixBuffer(m.numOfRows(), m.numOfColumns());
		for (int row = 0; row < m.numOfRows(); row++)
			for (int col = 0; col < m.numOfColumns(); col++)
				x.set(row, col, m.get(row, col));
		return x;
	}	
}
