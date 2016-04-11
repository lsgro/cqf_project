package com.luigisgro.cqf.fdm;

/**
 * The core of the Finite Difference Method calculation is performed by
 * classes implementing this interface.
 * The {@link FiniteDifferencePricer} uses it to calculate the values at
 * each step of the analysis.
 * This interface provides the main method responsible to calculate the
 * next iteration of values in the grid, based on the current state of
 * the grid and the derivative being evaluated.
 * @author Luigi Sgro
 *
 */
public interface FiniteDifferenceModel {
	/**
	 * Calculates the next step of the analysis
	 * @param ti The time step index
	 * @param grid The {@link Grid} object
	 * @param derivative The {@link Derivative} to be priced
	 * @param r The interest rate
	 */
	void calculateNextStep(int ti, Grid grid, Derivative derivative, double r);
	
	/**
	 * Returns the vo
	 * @return The maximum volatility used by the method
	 */
	double maxVol();
}
