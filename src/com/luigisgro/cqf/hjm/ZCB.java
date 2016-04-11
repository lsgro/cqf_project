package com.luigisgro.cqf.hjm;

import java.util.Iterator;
import java.util.Map;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.CurveTimeSeriesOperator;
import com.luigisgro.cqf.curve.TimePoint;

/**
 * Discount operator. It calculates the price of a Zero Coupon Bond evaluated at time present
 * with maturity at time future, by using the Monte Carlo scenario provided.
 * The pricing implementation discounts along the instantaneous spot interest rates of the given
 * scenario by calculating the weighted average of the spot rate (integration of piecewise
 * constant evolution of the spot rate) and discounting a unit price with the resulting rate.
 * @author Luigi Sgro
 *
 * @param <T>
 */
public class ZCB<T extends TimePoint> implements CurveTimeSeriesOperator<T> {
	private T present;
	private T future;
	private double timeStep;
	
	/**
	 * Creates a new discount operator
	 * @param present Present time for discounting
	 * @param future Maturity of the ZCB
	 * @param timeStep Fraction of year corresponding by a calendar day
	 */
	public ZCB(T present, T future, double timeStep) {
		this.present = present;
		this.future = future;
		this.timeStep = timeStep;
	}
	@Override
	public double evaluate(CurveTimeSeries<T> scenario) {
		if (scenario.getCurves().firstKey().compareTo(present) > 0 || scenario.getCurves().lastKey().compareTo(future) < 0)
			throw new IllegalArgumentException("Time arguments outside range of scenario: " + scenario.getCurves().firstKey() + " - " + scenario.getCurves().lastKey());

		double yield = 0.0;
		Iterator<Map.Entry<T, Curve>> curveIterator = scenario.getCurves().entrySet().iterator();
		Map.Entry<T, Curve> item = null;
		
		// reach the curve corresponding to present time
		while (curveIterator.hasNext()) {
			item = curveIterator.next();
			if (item.getKey().compareTo(present) >= 0) {
				break;
			}
		}
		
		// start accumulating spot rates * timeStep until maturity
		do {
			yield += item.getValue().getPoints()[0] * timeStep;
			if (!curveIterator.hasNext())
				break;
			item = curveIterator.next();
		} while(item.getKey().compareTo(future) < 0);
		
		// discount with the yield just found
		return Math.exp(-yield);
	}
}
