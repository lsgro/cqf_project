package com.luigisgro.cqf.fdm;

/**
 * This interface defines the features of a derivative to be priced
 * in the Finite Difference Method.
 * To be able to handle derivatives of different maturities within the same
 * analysis, a generic approach is taken with respect to time-boundary condition
 * at maturity: the value at maturity is considered as any cash flow produced
 * by the derivative. When a derivative generates a cash flow in a specific
 * grid point, the cash flow is added to the grid point value during the analysis.
 * @author Luigi Sgro
 *
 */
public interface Derivative {
	/**
	 * 
	 * @return The maturity of the instrument
	 */
	double timeToMaturity();
	
	/**
	 * If a derivative generates many cash flows
	 * during its life, this method must return the time of the last one.
	 * Otherwise it will return the time to maturity.
	 * This method is used to validate the grid steps for convergence.
	 * In fact there is a
	 * constraint on the minimum time span in the analysis with respect
	 * to volatility and number of stock steps.
	 * @return The last cash flow in time
	 */
	double timeToNearestCashflow();
	
	/**
	 * The cashflow generated in a specific time period, for a given stock value
	 * @param t The time of cashflow
	 * @param tStep The time step
	 * @param s The stock value
	 * @return A cash flow amount if the derivative generates a cash flow at this
	 * point in time +- tStep / 2. Zero otherwise
	 */
	double cashflow(double t, double tStep, double s);
	
	/**
	 * The boundary value for extreme values of the stock. This method must be called with
	 * parameters which satisfy the criteria for boundary value (high or low stock value).
	 * Otherwise the method generates a runtime exception. In fact if the algorithm calls
	 * this method from a point in the grid that do not satisfy the requirements for a boundary
	 * point, there is no way to handle it, and the algorithm must be terminated.
	 * @param t The point in time
	 * @param tStep The time step
	 * @param s The value of stock
	 * @param r The interest rate
	 * @return The boundary value, or raise an IllegalArgumentException if the parameters are
	 * within the range
	 */
	double boundaryValue(double t, double tStep, double s, double r);
	
	/**
	 * An alternative method to calculate the boundary value
	 * @param it Index of time step
	 * @param is Index of stock step
	 * @param g The {@link Grid} object
	 * @return The boundary value, or raise an IllegalArgumentException if the parameters are
	 * within the range
	 */
	double boundaryValueOpt(int it, int is, Grid g);
}
