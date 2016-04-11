package com.luigisgro.cqf.curve;

/**
 * Represents a curve of values distributed on a {@link TermStructure}
 * for example a curve of interest rates
 * @author Luigi Sgro
 *
 */
public class Curve {
	private TermStructure termStructure;
	private double[] points;
	/**
	 * Class constructor
	 * @param points The values of the curve
	 * @param termStructure A {@link TermStructure} object
	 */
	public Curve(double[] points, TermStructure termStructure) {
		this.termStructure = termStructure;
		this.points = points;
	}
	/**
	 * The values of the curve points, from the spot to the highest tenor
	 * @return An array of double
	 */
	public double[] getPoints() {
		return points;
	}
	/**
	 * The term structure for this curve
	 * @return A {@link TermStructure} object
	 */
	public TermStructure getTermStructure() {
		return termStructure;
	}
}
