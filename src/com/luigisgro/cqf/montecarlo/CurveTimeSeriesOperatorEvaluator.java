package com.luigisgro.cqf.montecarlo;

import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.CurveTimeSeriesOperator;
import com.luigisgro.cqf.curve.TimePoint;

/**
 * A simple evaluator that always stop at the given number of iterations
 * @author Luigi Sgro
 *
 * @param <T>
 */
public class CurveTimeSeriesOperatorEvaluator<T extends TimePoint> implements MonteCarloEvaluator<T> {
	private final CurveTimeSeriesOperator<T> operator;
	private final int maxNumberOfIterations;
	protected int numberOfIterations = 0;
	protected double accumulator = 0.0;
	
	/**
	 * Creates a new evaluator
	 * @param operator The derivative to be priced
	 * @param maxNumberOfIterations The number of iterations to be performed, regardless of accuracy
	 */
	public CurveTimeSeriesOperatorEvaluator(CurveTimeSeriesOperator<T> operator, int maxNumberOfIterations) {
		this.operator = operator;
		this.maxNumberOfIterations = maxNumberOfIterations;
	}
	
	@Override
	public double evaluateAndAccumulate(CurveTimeSeries<T> scenario) {
		numberOfIterations++;
		double scenarioEvaluation = operator.evaluate(scenario);
		accumulator += scenarioEvaluation;
		return scenarioEvaluation;
	}

	@Override
	public boolean moreIterationsNeeded() {
		return numberOfIterations < maxNumberOfIterations;
	}

	@Override
	public double getResult() {
		return accumulator / numberOfIterations;
	}

	@Override
	public void printProgress() {
		System.out.println("Iteration: " + numberOfIterations + "; currentValue: " + getResult());
	}
}
