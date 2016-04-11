package com.luigisgro.cqf.hjm;

import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.CurveTimeSeriesOperator;
import com.luigisgro.cqf.curve.TimePoint;

/**
 * This class embeds the common logic of a caplet and floorlet.
 * The evaluation uses a {@link ForwardInterestRate} operator and a {@link ZCB} discount operator.
 * @author Luigi Sgro
 *
 * @param <T>
 */
public abstract class Xlet<T extends TimePoint> implements CurveTimeSeriesOperator<T> {
	private ForwardInterestRate<T> forwardInterestRateOp;
	private ZCB<T> discountOp;
	private double tenor;
	
	/**
	 * Creates a new caplet or floorlet
	 * @param present The present time, for discounting
	 * @param evaluationTime The evaluation time, where the interest rate is evaluated and the payoff calculated
	 * @param cashflowTime The time of the corresponding cashflow
	 * @param tenor The tenor of the caplet/floorlet
	 * @param timeStep The fraction of year corresponding to a calendar day
	 */
	public Xlet(T present, T evaluationTime, T cashflowTime, double tenor, double timeStep) {
		forwardInterestRateOp = new ForwardInterestRate<T>(evaluationTime, tenor);
		discountOp = new ZCB<T>(present, cashflowTime, timeStep);
		this.tenor = tenor;
	}
	
	/**
	 * The caplet/floorlet un-discounted, unscaled payoff:
	 * i.e. max(rate - strike, 0) for caplet; max(strike - rate, 0) for floorlet
	 * @param forwardInterestRate
	 * @return The caplet/floorlet payoff
	 */
	protected abstract double payoff(double forwardInterestRate);
	
	@Override
	public double evaluate(CurveTimeSeries<T> scenario) {
		double forwardInterestRate = forwardInterestRateOp.evaluate(scenario);
		double payoff = payoff(forwardInterestRate);
		if (payoff > 0)
			return payoff * discountOp.evaluate(scenario) * tenor;
		else
			return 0; 
	}
}
