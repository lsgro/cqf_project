package com.luigisgro.cqf.hjm;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.CurveTimeSeriesOperator;
import com.luigisgro.cqf.curve.TimePoint;

/**
 * This class calculates the forward rate for a given tenor,
 * based on a curve time series of instantaneous interest rates.
 * The rate is calculated by selecting from the scenario the curve corresponding to the
 * evaluation date, then by integrating the instantaneous rates on the curve from spot to the desired tenor.
 * The forward rate curve is assumed piecewise constant, therefore the integration is simply a sum
 * of product of rate * (next_tenor - tenor)
 * 
 * @author Luigi Sgro
 *
 * @param <T>
 */
public class ForwardInterestRate<T extends TimePoint> implements CurveTimeSeriesOperator<T> {
	private T future;
	private double tenor;
	
	/**
	 * Creates a new forward interest rate operator
	 * @param future The time when the rate is to be calculated
	 * @param tenor The tenor of the rate
	 */
	public ForwardInterestRate(T future, double tenor) {
		this.future = future;
		this.tenor = tenor;
	}
	@Override
	public double evaluate(CurveTimeSeries<T> scenario) {
		Curve curve = scenario.getCurves().get(future);
		if (curve == null)
			throw new IllegalArgumentException("Future time point: " + future + " not found in scenario: " + scenario.getCurves().firstKey() + " - " + scenario.getCurves().lastKey());

		double[] curveTenors = curve.getTermStructure().getTenors();
		double[] rates = curve.getPoints();
		
		double accumulator = 0.0;
		
		double currentTenor;
		double previousTenor = 0.0;
		
		int tenorIndex = 0;
		do {
			currentTenor = curveTenors[tenorIndex];
			accumulator += rates[tenorIndex] * (Math.min(tenor, currentTenor) - previousTenor); // integration of piecewise constant
			previousTenor = currentTenor;
			tenorIndex++;
		} while (currentTenor < tenor);
		
		return accumulator / tenor;
	}
}
