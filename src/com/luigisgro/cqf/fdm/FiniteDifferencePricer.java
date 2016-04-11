package com.luigisgro.cqf.fdm;

import java.io.PrintStream;

/**
 * Skeleton of the Finite Difference Method
 * @author Luigi Sgro
 *
 */
public class FiniteDifferencePricer {
	
	/**
	 * Performs a complete Finite Difference Method pricing, starting from the maturity of the
	 * instrument evaluated to the present time
	 * @param grid The grid
	 * @param derivative The derivative to be priced
	 * @param method A {@link FiniteDifferenceModel} implementation
	 * @param r The interest rate
	 * @param printProgress True if the method should print hash signs at each 1% progress in the pricing process
	 * @param ps A {@link java.io.PrintStream} to be used in case hash signs should be printed to
	 * signal progress
	 * @return True if the pricing was successful, false otherwise
	 */
	public static boolean evaluate(Grid grid, Derivative derivative, FiniteDifferenceModel method, double r, boolean printProgress, PrintStream ps) {
		if (!grid.validate(method.maxVol(), derivative))
			return false;
		grid.reset();
		int lastProgressStep = 0;
		for (int it = 0; it < grid.numT() - 1; it++) {
			if (printProgress) {
				int progressStep = (int)(((double)it / grid.numT()) * 100);
				if (progressStep > lastProgressStep) {
					System.out.print("#");
					lastProgressStep = progressStep;
				}
			}
			method.calculateNextStep(it, grid, derivative, r);
			if (ps != null)
				grid.printStep(it, ps);
		}
		if (printProgress)
			System.out.println();
		return true;
	}
}
