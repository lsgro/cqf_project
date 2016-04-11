package com.luigisgro.cqf.linalg;

/**
 * Modifiable implementation of {@link Vector}
 * @author Luigi Sgro
 *
 */
public class VectorBuffer extends Vector {
	private double[] values;
	
	/**
	 * Creates an empty modifiable vector
	 * @param dimension Dimension of the vector
	 */
	public VectorBuffer(int dimension) {
		values = new double[dimension];
	}
	
	/**
	 * Creates a modifiable vector, with initial values
	 * @param values An array of values to initialize the vector
	 */
	public VectorBuffer(double[] values) {
		this.values = values;
	}
	
	@Override
	public double get(int i) {
		return values[i];
	}
	
	@Override
	public int dimension() {
		return values.length;
	}
	
	/**
	 * Sets the i-th value of the vector
	 * @param i Index of the element to modify
	 * @param value New value of the i-th element
	 */
	public void set(int i, double value) {
		values[i] = value;
	}
	
	/**
	 * Utility method to swap two elements in the vector. To be used for sorting algorithms.
	 * @param j1 The index of the first element
	 * @param j2 The index of the second element
	 */
	public void swapElements(int j1, int j2) {
		double tmp = get(j2);
		set(j2, get(j1));
		set(j1, tmp);
	}
	public static VectorBuffer copyOf(Vector v) {
		VectorBuffer x = new VectorBuffer(v.dimension());
		for (int i = 0; i < v.dimension(); i++)
			x.set(i, v.get(i));
		return x;
	}
}
