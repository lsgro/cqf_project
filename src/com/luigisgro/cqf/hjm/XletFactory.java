package com.luigisgro.cqf.hjm;

import com.luigisgro.cqf.curve.TimePoint;

/**
 * Factory interface for creation of caplets and floorlets
 * @author Luigi Sgro
 *
 * @param <T>
 */
public interface XletFactory<T extends TimePoint> {
	/**
	 * Create a caplet or floorlet
	 * @param present Present time for discounting
	 * @param evaluationTime Time of evaluation of forward rate and payoff
	 * @param cashflowTime Time of cashflow, to be discounted to present
	 * @param tenor Tenor of the caplet/floorlet
	 * @param timeStep Time step used for calculating discount
	 * @return A concrete {@link Xlet}: either a caplet of floorlet
	 */
	Xlet<T> createXlet(T present, T evaluationTime, T cashflowTime, double tenor, double timeStep);
}
