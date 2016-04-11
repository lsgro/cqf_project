package com.luigisgro.cqf.montecarlo;

import com.luigisgro.cqf.curve.Curve;

/**
 * This interface isolates the model-depending features of a Monte Carlo simulation: the generation
 * of a scenario for the underlying. There is no knowledge of the calculation of derivative
 * value based on this scenario, or of the criteria to be satisfied for the simulation to be
 * complete
 * @author Luigi Sgro
 *
 */
public interface MultiFactorModel {
	/**
	 * Based on a current status of the underlying and a vector from a suitable distribution,
	 * calculates a next step of the underlying
	 * @param currentCurve The current curve representing a state of the underlying today
	 * @param randomVector The vector to drive the evolution to the next state of the underlying
	 * @param timeStep The fraction of year from one state to the next
	 * @return A new curve representing a next state of the underlying
	 */
	Curve nextCurve(Curve currentCurve, double[] randomVector, double timeStep);
	
	/**
	 * @return The dimension of the model, i.e. the dimension of randomVector to be provided
	 */
	int getDimension();
}
