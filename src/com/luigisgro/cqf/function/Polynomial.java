package com.luigisgro.cqf.function;

/**
 * Scalar polynomial function
 * @author Luigi Sgro
 *
 */
public class Polynomial implements IntegrableFunction, DerivableFunction {
	private final double[] coefficients;
	
	/**
	 * Creates a new polynomial with the given coefficients
	 * @param coefficients The array of polynomial coefficients, ordered from degree 0 (constant term)
	 * to the coefficient of the highest power of the polynomial
	 */
	public Polynomial(double[] coefficients) {
		this.coefficients = coefficients;
	}
	
	/**
	 * 
	 * @return The polynomial coefficients: coefficient of the element of degree N: getCoefficients()[N]
	 */
	public double[] getCoefficients() {
		return coefficients;
	}

	public double value(double x) {
		double y = 0;
		for (int i = 0; i < coefficients.length; i++) {
			y += intPower(x, i) * coefficients[i];
		}
		return y;
	}
	
	@Override
	public Polynomial integrate() {
		double[] coeff = new double[coefficients.length + 1];
		for (int i = 0; i < coefficients.length; i++)
			coeff[i + 1] = coefficients[i] / (i + 1);
		return new Polynomial(coeff);
	}

	@Override
	public Polynomial derivate() {
		double[] coeff = new double[coefficients.length - 1];
		for (int i = 1; i < coefficients.length; i++)
			coeff[i - 1] = coefficients[i] * i;
		return new Polynomial(coeff);
	}
	
	private static double intPower(double x, int exponent) {
		double power = 1;
		for (int j = 1; j <= exponent; j++)
			power *= x;
		return power;
	}
	
	/**
	 * This method is to be used for linear regression of a curve. It creates the regressors
	 * that can be used in the Ordinary Least Square method
	 * @param abscissa The abscissa where the regressors have to be calculated
	 * @param degree The maximum degree of the regression
	 * @return An array of regressors of the given degree, for the given value of abscissa
	 * @see <a href="http://commons.apache.org/math/api-2.2/org/apache/commons/math/stat/regression/OLSMultipleLinearRegression.html">org.apache.commons.math.stat.regression.OLSMultipleLinearRegression</a>
	 */
	public static double[] createRegressors(double abscissa, int degree) {
		double regressors[] = new double[degree + 1];
		for (int i = 0; i <= degree; i++)
			regressors[i] = intPower(abscissa, i);
		return regressors;
	}
	
	/**
	 * Utility method to create a new polynomial with the coefficient scaled
	 * by a given factor
	 * @param p The original polynomial
	 * @param factor The factor
	 * @return A new polynomial, scaled according to factor
	 */
	public static Polynomial scale(Polynomial p, double factor) {
		double[] scaledCoefficients = new double[p.getCoefficients().length];
		for (int i = 0; i < scaledCoefficients.length; i++)
			scaledCoefficients[i] = p.getCoefficients()[i] * factor;
		Polynomial pScaled = new Polynomial(scaledCoefficients);
		return pScaled;
	}
	
	public boolean equals(Object o) {
		if (! (o instanceof Polynomial))
			return false;
		return coefficients.equals(((Polynomial)o).coefficients);
	}
	
	public int hashCode() {
		return coefficients.hashCode();
	}
}
