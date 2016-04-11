package com.luigisgro.cqf.curve;

import java.io.PrintWriter;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * An ordered set of {@link Curve} objects with the same {@link TermStructure} each
 * associated to a {@link TimePoint}. It models both the historical interest rate curves
 * or a scenario of future evolution of curves.
 * @author Luigi Sgro
 *
 * @param <T>
 */
public class CurveTimeSeries<T extends TimePoint> {
	private TermStructure termStructure;
	private double timeStep;
	private SortedMap<T, Curve> curves = new TreeMap<T, Curve>();
	
	/**
	 * Constructor
	 * @param termStructure The {@link TermStructure} of this curve series
	 * @param timeStep The fraction of year represented by the {@link TimePoint} used
	 */
	public CurveTimeSeries(TermStructure termStructure, double timeStep) {
		this.termStructure = termStructure;
		this.timeStep = timeStep;
	}
	
	/**
	 * Time step of the curve time series. It is the time period between each subsequent curve
	 * it is expressed as a fraction of one year
	 * @return The curve time series time stamp, as a floating point number
	 */
	public double getTimeStep() {
		return timeStep;
	}
	
	/**
	 * 
	 * Adds a curve to the series, checking for the term structure, which must be
	 * the same of the series. Substitute the existing value if a value is already
	 * contained for this time point
	 * @param timePoint Reference time point for this curve
	 * @param curve {@link Curve} object
	 */
	public void put(T timePoint, Curve curve) {
		if (!termStructure.equals(curve.getTermStructure()))
			throw new IllegalArgumentException("Wrong term structure: " + curve.getTermStructure());
		curves.put(timePoint, curve);
	}
	
	/**
	 * The curve at a specific time point. In case the time point is not available (e.g. holiday)
	 * there is no point associated.
	 * @param timePoint A point in time
	 * @return A {@link Curve} object, or null if the series does not contain a curve for this time point
	 */
	public Curve get(T timePoint) {
		return curves.get(timePoint);
	}
	
	/**
	 * A {@link java.util.SortedMap} containing all the curves, in order of date
	 * @return A SortedMap of curves by time point
	 */
	public SortedMap<T, Curve> getCurves() {
		return curves;
	}

	/**
	 * The term structure of the series, shared by all the curves contained in this series
	 * @return A {@link TermStructure} object
	 */
	public TermStructure getTermStructure() {
		return termStructure;
	}
	
	/**
	 * An utility method to print, one curve per line, the contents of the series
	 * @param printer A {@link java.io.PrintWriter} to be used as output object
	 */
	public void print(PrintWriter printer) {
		for (Map.Entry<T, Curve> item : curves.entrySet()) {
			printer.print(item.getKey() + ":");
			double[] points = item.getValue().getPoints();
			for (int i = 0; i < points.length - 1;  i++) {
				printer.print(" " + points[i] + ";");
			}
			printer.println(" " + points[points.length - 1]);
		}
		printer.flush();
	}
}
