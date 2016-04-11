package com.luigisgro.cqf.test;

import junit.framework.Assert;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleRealPointChecker;
import org.apache.commons.math.optimization.direct.NelderMead;
import org.apache.commons.math.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer;
import org.junit.Test;


public class TestOptimize {
	static abstract class AFunction implements DifferentiableMultivariateRealFunction {
		private int dim;
		private double pdStep;
		private MultivariateRealFunction[] partialDerivatives;
		private AFunctionGradient gradient;
		public AFunction(int dim, double pdStep) {
			this.dim = dim;
			this.pdStep = pdStep;
			partialDerivatives = new MultivariateRealFunction[dim];
			for (int k = 0; k < dim; k++) {
				partialDerivatives[k] = new AFunctionPartialDerivative(k);
			}
			gradient = new AFunctionGradient();
		}
		
		class AFunctionPartialDerivative implements MultivariateRealFunction {
			int k;
			public AFunctionPartialDerivative(int k) {
				this.k = k;
			}
			@Override
			public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
				double[] x1 = point.clone();
				double[] x2 = point.clone();
				x1[k] -= pdStep;
				x2[k] += pdStep;
				return (AFunction.this.value(x2) - AFunction.this.value(x1)) / (pdStep * 2);
			}
		}
		
		class AFunctionGradient implements MultivariateVectorialFunction {
			@Override
			public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
				double[] v = new double[dim];
				for (int k = 0; k < dim; k++)
					v[k] = partialDerivatives[k].value(point);
				return v;
			}
		}
		
		@Override
		public abstract double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException;

		@Override
		public MultivariateRealFunction partialDerivative(int k) {
			return partialDerivatives[k];
		}

		@Override
		public MultivariateVectorialFunction gradient() {
			return gradient;
		}
	}
	
	@Test
	public void test() throws Exception {
		MultivariateRealFunction  f = new MultivariateRealFunction () {
			@Override
			public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
				return point[0] * point[0] + point[1] * point[1];
			}
		};
		NelderMead optimizer = new NelderMead();
		optimizer.setConvergenceChecker(new SimpleRealPointChecker());
		RealPointValuePair result = optimizer.optimize(f, GoalType.MINIMIZE, new double[] {10, -3});
		double[] resultPoint = result.getPoint();
		System.out.println("Opt converged in " + optimizer.getIterations() + " iterations [" + resultPoint[0] + "," + resultPoint[1] +"] value: " + result.getValue());
		Assert.assertEquals(0, resultPoint[0], 0.05);
		Assert.assertEquals(0, resultPoint[1], 0.05);
	}
	@Test
	public void test2() throws Exception {
		DifferentiableMultivariateRealFunction  f = new AFunction (2, 0.001) {
			@Override
			public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
				return point[0] * point[0] + point[1] * point[1];
			}
		};
		NonLinearConjugateGradientOptimizer optimizer = new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.FLETCHER_REEVES);
		RealPointValuePair result = optimizer.optimize(f, GoalType.MINIMIZE, new double[] {10, -3});
		double[] resultPoint = result.getPoint();
		System.out.println("Opt converged in " + optimizer.getIterations() + " iterations [" + resultPoint[0] + "," + resultPoint[1] +"] value: " + result.getValue());
		Assert.assertEquals(0, resultPoint[0], 0.05);
		Assert.assertEquals(0, resultPoint[1], 0.05);
	}

}
