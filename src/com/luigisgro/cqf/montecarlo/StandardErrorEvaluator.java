package com.luigisgro.cqf.montecarlo;

import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.CurveTimeSeriesOperator;
import com.luigisgro.cqf.curve.TimePoint;

/**
 * An implementation of {@link MonteCarloEvaluator} based on the notion of "standard error".
 * At each iteration an approximation of the standard error is calculated, and the simulation
 * is deemed completed if the approximated standard error falls below a given threshold.
 * A maximum number of iterations is enforced thanks to the parent class: {@link CurveTimeSeriesOperatorEvaluator}
 * @author Luigi Sgro
 *
 * @param <T>
 */
public class StandardErrorEvaluator<T extends TimePoint> extends CurveTimeSeriesOperatorEvaluator<T> {
	private final double squareStdErrdThreshold;
	private double accumulatorOfSquares = 0.0;
	
	/**
	 * Creates a new evaluator
	 * @param operator The derivative to be priced
	 * @param stdErrdThreshold The threshold of standard error considered acceptable for terminating the simulation
	 * @param maxNumberOfIterations The maximum number of iteration to be performed
	 */
	public StandardErrorEvaluator(CurveTimeSeriesOperator<T> operator, Double stdErrdThreshold, Integer maxNumberOfIterations) {
		super(operator, maxNumberOfIterations);
		this.squareStdErrdThreshold = stdErrdThreshold * stdErrdThreshold;
	}
	
	/**
	 * Calculates the estimated standard error at the present stage of the simulation
	 * This value is used internally by the method {@link MonteCarloEvaluator#moreIterationsNeeded()}
	 * to determine if the simulation has reached a suitable level of accuracy
	 * @return An estimate of the current standard error of the simulation
	 */
	public double squareStdErr() {
		return Math.abs(accumulatorOfSquares / numberOfIterations  - accumulator * accumulator / numberOfIterations / numberOfIterations) / numberOfIterations;
	}
	
	@Override
	public double evaluateAndAccumulate(CurveTimeSeries<T> scenario) {
		double scenarioEvaluation = super.evaluateAndAccumulate(scenario);
		accumulatorOfSquares += scenarioEvaluation * scenarioEvaluation;
		return scenarioEvaluation;
	}

	@Override
	public boolean moreIterationsNeeded() {
		if (super.moreIterationsNeeded()) {
			if (numberOfIterations < 100) // let the results wander around for a while, building an error > 0
				return true;
			else
				return squareStdErr() > squareStdErrdThreshold;
		} else {
			return false;
		}
	}	

	@Override
	public void printProgress() {
		System.out.println("Iteration: " + numberOfIterations + "; currentValue: " + getResult() + "; std error: " + Math.sqrt(squareStdErr()));
	}
}
