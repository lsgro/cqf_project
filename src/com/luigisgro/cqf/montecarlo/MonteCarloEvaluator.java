package com.luigisgro.cqf.montecarlo;

import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.TimePoint;

/**
 * Objects responsible for calculating the derivative value corresponding to a given
 * iteration of the Monte Carlo simulation, from a state of the underlying (scenario).
 * It also defines the criteria to stop simulation.
 * @author Luigi Sgro
 *
 * @param <T>
 */
public interface MonteCarloEvaluator<T extends TimePoint> {
	
	/**
	 * At each iteration, the algorithm feeds the evaluator with a new scenario, which
	 * describes a complete time series for the underlying. Each time this method
	 * is called, the evaluator performs an evaluation of the value of the
	 * derivative, it updates its internal value, and decides if the simulation can stop
	 * or if it must carry on for another iteration
	 * @param scenario The complete time series describing a possible scenario for the underlying
	 * @return the evaluation of the current scenario
	 */
	double evaluateAndAccumulate(CurveTimeSeries<T> scenario);
	
	/**
	 * This method must be called at each iteration to test the need for more iterations
	 * @return True if the simulation must continue, false if it reached a suitable result
	 */
	boolean moreIterationsNeeded();
	
	/**
	 * This method returns the running result of the simulation. When the simulation is finished, it
	 * returns the final value.
	 * @return The result of the simulation
	 */
	double getResult();
	
	/**
	 * Print a user-friendly summary of the status
	 * of the simulation
	 */
	void printProgress();
}
