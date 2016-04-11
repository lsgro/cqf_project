package com.luigisgro.cqf.curve;

/**
 * An abstract operator extracting a scalar from a curve time series
 * @author Luigi Sgro
 *
 * @param <T>
 */
public interface CurveTimeSeriesOperator<T extends TimePoint> {
	/**
	 * The evaluation of the operator, taking a full time series as input, returning a scalar
	 * The input parameter is intended as a constant object, therefore implementations
	 * should not modify it
	 * @param curveTimeSeries The input curveTimeSeries
	 * @return A floating point value
	 */
	public double evaluate(CurveTimeSeries<T> curveTimeSeries);
}
