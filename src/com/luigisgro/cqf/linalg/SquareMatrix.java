package com.luigisgro.cqf.linalg;

/**
 * A marking interface for a square matrix
 * @author Luigi Sgro
 *
 */
public interface SquareMatrix {
	/**
	 * @return The number of columns, equal to the number or rows
	 */
	int dimension();
	
	/**
	 * @return A {@link Vector} containing the diagonal elements, ordered from left to right
	 */
	Vector getDiagonal();
	
	/**
	 * @return The sum of the diagonal elements
	 */
	double trace();
}
