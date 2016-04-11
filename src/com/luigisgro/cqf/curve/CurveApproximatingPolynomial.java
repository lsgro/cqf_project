package com.luigisgro.cqf.curve;

import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression;

import com.luigisgro.cqf.function.Function;
import com.luigisgro.cqf.function.Polynomial;

/**
 * A hybrid object with {@link Curve} nature, that can be used as a
 * continuous scalar {@link Function}.
 * It is initialised with a Curve, and it generates a polynomial that
 * approximates by least square method the values of the curve corresponding
 * to each tenor.
 * It is used to provide a smooth function in place of a discrete set of points.
 * @author Luigi Sgro
 *
 */
public class CurveApproximatingPolynomial extends Polynomial {
	double[] points;
	TermStructure termStructure;
	int degree;
	
	/**
	 * Constructor
	 * @param points The values to be approximated
	 * @param termStructure The term structure (abscissas) to be associated to each point
	 * @param degree The degree of the polynomial to be used for the approximation
	 */
	public CurveApproximatingPolynomial(double[] points, TermStructure termStructure, int degree) {
		super(estimatePolyCoefficients(points, termStructure, degree));
		this.termStructure = termStructure;
		this.points = points;
		this.degree = degree;
	}

	private static double[] estimatePolyCoefficients(double[] points, TermStructure termStructure, int degree) {
		double[][] regressors = new double[termStructure.getTenors().length][];
		for (int i = 0; i < regressors.length; i++) {
			regressors[i] = Polynomial.createRegressors(termStructure.getTenors()[i], degree);
		}
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.setNoIntercept(true);
		regression.newSampleData(points, regressors);
		return regression.estimateRegressionParameters();
	}
}
