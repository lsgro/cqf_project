package com.luigisgro.cqf.montecarlo;

/**
 * Generator of vectors with a suitable distribution for Monte Carlo integration
 * @author Luigi Sgro
 *
 */
public interface RandomVectorGenerator {
	
	/**
	 * @return The next vector from a specific random or quasi-random distribution
	 */
	double[] generateNextVector();
}
