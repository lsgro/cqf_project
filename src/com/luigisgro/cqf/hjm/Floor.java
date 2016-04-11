package com.luigisgro.cqf.hjm;

import com.luigisgro.cqf.curve.TimePoint;

/**
 * Implements a Floor by providing the floorlet payoff
 * @author Luigi Sgro
 *
 * @param <T>
 */
public class Floor<T extends TimePoint> extends SeriesOfOptions<T> {

	/**
	 * Creates a new Floor
	 * @param strike Floor strike
	 * @param tenor Tenor of the floorlets
	 * @param maturity Maturity of the instrument
	 * @param calendar Calendar used for the pricing
	 * @param timeStep Fraction of year corresponding to a calendar day
	 */
	public Floor(double strike, double tenor, double maturity, T[] calendar, double timeStep) {
		super(strike, tenor, maturity, calendar, timeStep);
	}

	@Override
	public Xlet<T> createXlet(T present, T evaluationTime, T cashflowTime, double tenor, double timeStep) {
		return new Xlet<T>(present, evaluationTime, cashflowTime, tenor, timeStep){
			@Override
			protected double payoff(double forwardInterestRate) {
				return Math.max(strike - forwardInterestRate, 0);
			}};
	}
}
