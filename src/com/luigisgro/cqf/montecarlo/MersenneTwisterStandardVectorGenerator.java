package com.luigisgro.cqf.montecarlo;

import org.apache.commons.math.random.GaussianRandomGenerator;
import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.random.UncorrelatedRandomVectorGenerator;

/**
 * Quasi random generator using the Commons Math library to generate Mersenne Twister sequences
 * @author Luigi Sgro
 * @see <a href="http://commons.apache.org/math/api-2.2/org/apache/commons/math/random/MersenneTwister.html">org.apache.commons.math.random.MersenneTwister</a>
 */
public class MersenneTwisterStandardVectorGenerator implements	RandomVectorGenerator {
	int dimension = 1;
	UncorrelatedRandomVectorGenerator generator;

	/**
	 * 
	 * @param dimension The dimension of the model
	 */
	public MersenneTwisterStandardVectorGenerator(Integer dimension) {
		this.dimension = dimension;
	}

	@Override
	public double[] generateNextVector() {
		if (generator == null)
			generator = new UncorrelatedRandomVectorGenerator(dimension, new GaussianRandomGenerator(new MersenneTwister()));
		return generator.nextVector();
	}
}
