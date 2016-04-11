package com.luigisgro.cqf.linalg;

import java.io.IOException;
import java.io.Writer;

/**
 * Definiton of the basic properties of a vector, with the implementation of some vector operations.
 * The basic implementation is immutable (read-only)
 * Index ranges from 0 to dimension - 1
 * @author Luigi Sgro
 *
 */
public abstract class Vector extends Matrix {
	
	/**
	 * @param i Index of the element (0 to dimension() - 1)
	 * @return The value of the i-th element
	 */
	public abstract double get(int i);
	
	/**
	 * 
	 * @return The dimension of the vector
	 */
	public abstract int dimension();
	
	/**
	 * Transform the vector in a primitive type array
	 * @return An array of double set to the value of the vector elements
	 */
	public double[] toDoubleArray() {
		double[] array = new double[dimension()];
		for (int i = 0; i < dimension(); i++)
			array[i] = get(i);
		return array;
	}
	
	/**
	 * 
	 * @param v A vector
	 * @return The sum of the input vector elements
	 */
	public static double sumOfElements(Vector v) {
		double sum = 0;
		for (int i = 0; i < v.dimension(); i++)
			sum += v.get(i);
		return sum;
	}
	
	/**
	 * 
	 * @param v A vector
	 * @return The mean of the vector elements
	 */
	public static double mean(Vector v) {
		return sumOfElements(v) / v.dimension();
	}
	
	/**
	 * 
	 * @param v1 A vector
	 * @param v2 Another vector
	 * @return The dot-product of the vectors in input
	 */
	public static double dotProduct(Vector v1, Vector v2) {
		if (v1.dimension() != v2.dimension())
			throw new IllegalArgumentException("Vectors with different dimension: " + v1.dimension() + " != " + v2.dimension());
		double result = 0;
		for (int i = 0; i < v1.dimension(); i++) {
			result += v1.get(i) * v2.get(i);
		}
		return result;
	}
	
	/**
	 * 
	 * @param v1 A vector
	 * @param v2 Another vector
	 * @return The covariance of the vectors in input: the sum of element-wise products of the vectors variations
	 */
	public static double covariance(Vector v1, Vector v2) {
		if (v1.dimension() != v2.dimension())
			throw new IllegalArgumentException("Vectors with different dimension: " + v1.dimension() + " != " + v2.dimension());
		double mean1 = mean(v1);
		double mean2 = mean(v2);
		double tmp = 0;
		for (int i = 0; i < v1.dimension(); i++) {
			tmp += (v1.get(i) - mean1) * (v2.get(i) - mean2);
		}
		return tmp / v1.dimension();
	}
	
	public String toString() {
		String desc = "[";
		for (int i = 0; i < dimension() - 1; i++) {
			desc += get(i) + ", ";
		}
		desc += get(dimension() - 1) + "]";
		return desc;
	}

	@Override
	public int numOfRows() {
		return dimension();
	}

	@Override
	public int numOfColumns() {
		return 1;
	}

	@Override
	public double get(int row, int col) {
		return get(row);
	}
	
	@Override
	public int writeLinesToCSV(Writer writer, String separator) throws IOException {
		String line = "";
		for (int i = 0; i < dimension() - 1; i++) {
			line += get(i) + separator;
		}
		line += get(dimension() - 1) + "\n";
		writer.write(line);
		return 1;
	}
}
