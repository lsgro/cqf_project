package com.luigisgro.cqf.fdm;


/**
 * Implementation of the Explicit method for Finite Difference Method.
 * It performs calculation of partial derivatives, and return the value of the next
 * time step, using the three grid point in the previous time step, and the stock
 * boundary value at the highest and lowest stock step index
 * @author Luigi Sgro
 *
 */
public abstract class ExplicitMethod implements FiniteDifferenceModel {
	
	/**
	 * Calculates the delta of the instrument
	 * @param grid The grid
	 * @param it The time step index
	 * @param is The stock step index
	 * @return The approximated value of the partial derivative with respect to the stock value
	 */
	public static double delta(Grid grid, int it, int is) {
		return (grid.get(it, is + 1) - grid.get(it, is - 1)) / (grid.sStep() * 2);
	}
	
	/**
	 * Calculates the gamma of the instrument
	 * @param grid The grid
	 * @param it The time step index
	 * @param is The stock step index
	 * @return The approximated value of the second partial derivative with respect to the stock value
	 */
	public static double gamma(Grid grid, int it, int is) {
		return (grid.get(it, is + 1) - 2 * grid.get(it, is) + grid.get(it, is - 1)) / (grid.sStep() * grid.sStep());
	}
	
	/**
	 * Calculates the theta of the instrument. This method is abstract since it will be
	 * implemented in a different way in constant volatility method and uncertain volatility
	 * method.
	 * @param s The value of the stock 
	 * @param value The value of the grid at the point of calculation
	 * @param delta The value of delta at the point of calculation
	 * @param gamma The value of gamma at the point of calculation
	 * @param r The interest rate
	 * @return The approximated partial derivative with respect to time
	 */
	public abstract double theta(double s, double value, double delta, double gamma, double r);

	@Override
	public void calculateNextStep(int it, Grid grid, Derivative derivative, double r) {
		grid.set(it + 1, 0, derivative.boundaryValue(grid.getT(it + 1), grid.tStep(), grid.getSMin(), r));		
		//grid.set(it + 1, 0, derivative.boundaryValueOpt(it + 1, 0, grid));		
		for (int is = 1; is < grid.numS() - 1; is++) {
			double value = grid.get(it, is);
			double delta = delta(grid, it, is);
			double gamma = gamma(grid, it, is);
			double cf = derivative.cashflow(grid.getT(it + 1), grid.tStep(), grid.getS(is));
			double theta = theta(grid.getS(is), value, delta, gamma, r);
			double nextValue = value - theta * grid.tStep() + cf;
			grid.set(it + 1, is, nextValue);
		}
		grid.set(it + 1, grid.numS() - 1, derivative.boundaryValue(grid.getT(it + 1), grid.tStep(), grid.getSMax(), r));
		//grid.set(it + 1, grid.numS() - 1, derivative.boundaryValueOpt(it + 1, grid.numS() - 1, grid));
	}
}
