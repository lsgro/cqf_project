package com.luigisgro.cqf.function;

/**
 * Simple interface for scalar functions of scalar variable
 * @author Luigi Sgro
 *
 */
public interface Function {
	/**
	 * Calculates the value of the function at x
	 * @param x The abscissa
	 * @return The value of the function at x
	 */
	double value(double x);
}
