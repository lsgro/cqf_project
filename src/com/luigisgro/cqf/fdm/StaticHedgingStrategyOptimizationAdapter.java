package com.luigisgro.cqf.fdm;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;

/**
 * An adapter that makes a {@link StaticHedgingStrategy} suitable for optimization
 * by a {@link org.apache.commons.math.optimization.MultivariateRealOptimizer}
 * @author Luigi Sgro
 *
 */
public class StaticHedgingStrategyOptimizationAdapter implements MultivariateRealFunction {
	private StaticHedgingStrategy strategy;
	private double s;
	
	/**
	 * Creates a new adapter that can be used for optimization
	 * @param strategy The {@link StaticHedgingStrategy} that must be optimized
	 * @param s The value of stock at which the optimization must be carried out
	 */
	public StaticHedgingStrategyOptimizationAdapter(StaticHedgingStrategy strategy, double s) {
		this.strategy = strategy;
		this.s = s;
	}
	
	@Override
	public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
		Portfolio.Item[] hedgeItems = strategy.getHedgeItems();
		if (point.length != hedgeItems.length)
			throw new IllegalArgumentException("Wrong dimension: " + point.length + " instead of: " + hedgeItems.length);
		for (int k = 0; k < point.length; k++) {
			hedgeItems[k].position = point[k];
		}
		if (!strategy.runPricing()) {
			throw new FunctionEvaluationException(point, "An error occurred during calculation");
		}
		return strategy.getPnLAt(s);
	}
}
