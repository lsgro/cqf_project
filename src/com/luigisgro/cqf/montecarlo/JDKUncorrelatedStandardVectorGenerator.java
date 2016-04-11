package com.luigisgro.cqf.montecarlo;

import org.apache.commons.math.random.GaussianRandomGenerator;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.UncorrelatedRandomVectorGenerator;

/**
 * Basic implementation of {@link RandomVectorGenerator} that uses the native random
 * generator of the Java Virtual Machine
 * @author Luigi Sgro
 *
 */
public class JDKUncorrelatedStandardVectorGenerator implements	RandomVectorGenerator {
	int dimension = 1;
	UncorrelatedRandomVectorGenerator generator;

	/**
	 * Returns the dimension of the model, i.e. the number of stochastic components
	 * @param dimension The dimension of the model
	 */
	public JDKUncorrelatedStandardVectorGenerator(Integer dimension) {
		this.dimension = dimension;
	}

	@Override
	public double[] generateNextVector() {
		if (generator == null)
			generator = new UncorrelatedRandomVectorGenerator(dimension, new GaussianRandomGenerator(new JDKRandomGenerator()));
		return generator.nextVector();
	}
}
