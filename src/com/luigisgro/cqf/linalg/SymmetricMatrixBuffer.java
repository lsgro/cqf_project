package com.luigisgro.cqf.linalg;

/**
 * Symmetric implementation of {@link MatrixBuffer}
 * @author Luigi Sgro
 *
 */
public class SymmetricMatrixBuffer extends Matrix implements SymmetricMatrix {
	private double[][] values;
	
	/**
	 * Creates a new symmetric modifiable matrix
	 * @param dimension The number of rows/columns
	 */
	public SymmetricMatrixBuffer(int dimension) {
		values = new double[dimension][];
		for (int i = 0; i < dimension; i++) {
			values[i] = new double[i + 1];
		}
	}

	@Override
	public int numOfRows() {
		return dimension();
	}

	@Override
	public int numOfColumns() {
		return dimension();
	}

	@Override
	public double get(int row, int col) {
		if (row < col)
			return values[col][row];
		else
			return values[row][col];
	}
	
	/**
	 * Setter method for the matrix elements
	 * @param row Row of the element
	 * @param col Column of the element
	 * @param value New value for the element in row, column
	 */
	public void set(int row, int col, double value) {
		if (row < col)
			values[col][row] = value;
		else
			values[row][col] = value;
	}
	
	/**
	 * Creates a cloned matrix
	 * @param m Original matrix
	 * @return A clone of the original matrix
	 */
	public static SymmetricMatrixBuffer copyOf(Matrix m) {
		if (! (m instanceof SymmetricMatrix))
			throw new IllegalArgumentException("Copy argument must implement SymmetricMatrix");
		SymmetricMatrixBuffer x = new SymmetricMatrixBuffer(m.numOfRows());
		for (int row = 0; row < m.numOfRows(); row++)
			for (int col = 0; col <= row; col++)
				x.set(row, col, m.get(row, col));
		return x;
	}

	public Matrix scale(double k) {
		SymmetricMatrixBuffer result = new SymmetricMatrixBuffer(dimension());
		for (int i = 0; i < result.numOfRows(); i++)
			for (int j = 0; j <= i; j++)
				result.set(i, j, get(i, j) * k);
		return result;
	}

	@Override
	public int dimension() {
		return values.length;
	}

	@Override
	public Vector getDiagonal() {
		return new Vector() {
			@Override
			public double get(int i) {
				return SymmetricMatrixBuffer.this.get(i, i);
			}
			@Override
			public int dimension() {
				return SymmetricMatrixBuffer.this.dimension();
			}};
	}

	@Override
	public double trace() {
		double trace = 0;
		for (int i = 0; i < dimension(); i++)
			trace += get(i, i);
		return trace;
	}	
}
