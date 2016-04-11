package com.luigisgro.cqf.fdm;

import java.io.PrintStream;

/**
 * Finite Difference Method data structure.
 * It is a grid of floating point values, indexed by stock and time
 * The time step index works backwards: present is the maximum index
 * @author Luigi Sgro
 *
 */
public interface Grid {

	/**
	 * Validates the grid dimension and resolution with regard to the volatility
	 * and the derivative passed.
	 * @param vol Volatility used in the analysis
	 * @param d {@link Derivative} to be priced
	 * @return True if the FDM would converge, false otherwise
	 */
	boolean validate(double vol, Derivative d);

	/**
	 * The number of time steps of the grid
	 * @return Size along the time axis
	 */
	int numT();

	/**
	 * The number of stock steps of the grid
	 * @return Size along the stock axis
	 */
	int numS();

	/**
	 * The time step used within the grid
	 * @return The grid's time step
	 */
	double tStep();

	/**
	 * The stock value step used within the grid
	 * @return The grid's stock value step
	 */
	double sStep();

	/**
	 * Returns the values of a cell in the grid
	 * @param it Time step index
	 * @param is Stock step index
	 * @return The value at it, is
	 */
	double get(int it, int is);

	/**
	 * Returns the value for time = 0
	 * @param si The index of stock steps
	 * @return The grid value at present, is
	 */
	double getPresent(int si);

	/**
	 * Returns the value for time = 0, for any value of s within the stock range.
	 * Values of s that do not correspond to a grid point are calculated via
	 * linear interpolation
	 * @param s The stock value
	 * @return The interpolated grid value at present, s
	 */
	double getPresentInterpolated(double s);

	/**
	 * Sets a value in the grid
	 * @param it Time step index
	 * @param is Stock step index
	 * @param value
	 */
	void set(int it, int is, double value);

	/**
	 * The minimum value of the stock in the grid
	 * @return The minimum value of the stock range
	 */
	double getSMin();

	/**
	 * The maximum value of the stock in the grid
	 * @return The maximum value of the stock range
	 */
	double getSMax();

	/**
	 * Converts between stock step index and stock value
	 * @param is The stock index
	 * @return The stock value
	 */
	double getS(int is);

	/**
	 * Converts between time step index and time value
	 * @param it The time index
	 * @return The time value
	 */
	double getT(int it);
	
	/**
	 * Prints the values for all stock steps for a specific time step index
	 * @param it Time step index
	 * @param ps Print stream
	 */
	void printStep(int it, PrintStream ps);
	
	/**
	 * Resets all grid values to 0
	 */
	void reset();
}