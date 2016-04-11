package com.luigisgro.cqf.hjm;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.TermStructure;
import com.luigisgro.cqf.function.Function;
import com.luigisgro.cqf.function.IntegrableFunction;
import com.luigisgro.cqf.montecarlo.MultiFactorModel;

/**
 * An implementation of {@link MultiFactorModel} providing the Heath Jarrow Morton / Musiela model
 * for evolution of interest rates
 * @author Luigi Sgro
 *
 */
public class MultiFactorHJM implements MultiFactorModel {
	private IntegrableFunction[] volatilityFunctions;
	private Function[] volatilityIntegrals;
	private double[] tenors;

	/**
	 * Creates a new HJM model
	 * @param volatilityFunctions The array of integrable volatility function of the model
	 * @param termStructure The term structure for this implementation
	 */
	public MultiFactorHJM(IntegrableFunction[] volatilityFunctions, TermStructure termStructure) {
		this.volatilityFunctions = volatilityFunctions;
		volatilityIntegrals = new Function[volatilityFunctions.length];
		for (int i = 0; i < volatilityFunctions.length; i++)
			volatilityIntegrals[i] = volatilityFunctions[i].integrate();
		tenors = termStructure.getTenors();
	}

	@Override
	public Curve nextCurve(Curve currentCurve, double[] randomVector, double timeStep) {
		double points[] = new double[tenors.length];
		
		for (int i = 0; i < tenors.length; i++) {
			
			// calculate the first term of the drift, and the stochastic increment, from volatility
			double drift = 0.0;
			double stochasticIncrement = 0.0;
			for (int j = 0; j < volatilityFunctions.length; j++) {
				double factorVolatility = volatilityFunctions[j].value(tenors[i]);
				double integralOfFactorVolatility = volatilityIntegrals[j].value(tenors[i]); // definite integral: integral of polynomial at 0 always 0
				drift += factorVolatility * integralOfFactorVolatility;
				stochasticIncrement += randomVector[j] * factorVolatility;
			}
			
			// add the second term of the drift from the derivative of curve values w.r.t. maturity (term due to Musiela parameterization)
			double dFdTau;
			if (i + 1 < tenors.length) {
				dFdTau = (currentCurve.getPoints()[i + 1] - currentCurve.getPoints()[i]) / (tenors[i + 1] - tenors[i]);
			} else {
				dFdTau = 0;
			}
			drift += dFdTau;
			
			// calculate the next step in time for the curve point
			points[i] = currentCurve.getPoints()[i] + drift * timeStep + stochasticIncrement * Math.sqrt(timeStep);
		}
		return new Curve(points, currentCurve.getTermStructure());
	}

	@Override
	public int getDimension() {
		return volatilityFunctions.length;
	}
}
