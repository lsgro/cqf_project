package com.luigisgro.cqf.fdm;


/**
 * A memory optimized implementation of {@link Grid} that can run any number of time step
 * using the same amount of memory, by discarding the old steps.
 * @author Luigi Sgro
 *
 */
public class TwoStepsGrid extends BaseGrid {
	private double[] nodes;
	private boolean[] valid;
	private int minIndexOfT;
	private final int NUM_OF_NODES;
	
	/**
	 * Creates a memory-optimized grid.
	 * @param tStep The time step
	 * @param tMin The time range minimum value
	 * @param tMax The time range maximum value
	 * @param sStep The step of the stock value
	 * @param sMin The minimum value of the stock range
	 * @param sMax The maximum value of the stock range
	 */
	public TwoStepsGrid(double tStep, double tMin, double tMax, double sStep, double sMin, double sMax) {
		super(tStep, tMin, tMax, sStep, sMin, sMax);
		NUM_OF_NODES = 2 * numS;
		nodes = new double[NUM_OF_NODES];
		valid = new boolean[NUM_OF_NODES];
		reset();
	}
	@Override
	public double get(int it, int is) {
		int containerIndex = containerIndex(it, is);
		if (!valid[containerIndex])
			throw new IllegalArgumentException("Element (" + it + "," + is + ") not set!");
		return nodes[containerIndex];
	}
	@Override
	public void set(int it, int is, double value) {
		int containerIndex = containerIndex(it, is);
		nodes[containerIndex] = value;
		valid[containerIndex] = true;
	}
	@Override
	public void reset() {
		for (int i = 0; i < NUM_OF_NODES; i++) {
			nodes[i] = 0;
			valid[i] = true;
			minIndexOfT = 0;
		}
	}
	private int containerIndex(int it, int is) {
		if (it < minIndexOfT)
			throw new IllegalArgumentException("Low T index: " + minIndexOfT + " requested: " + it);
		if (it > minIndexOfT + 2)
			throw new IllegalArgumentException("Low T index: " + minIndexOfT + " requested: " + it);
		int internalIndexOfT = it % 2;
		if (it == minIndexOfT + 2) {
			minIndexOfT++;
			for (int tis = 0; tis < numS; tis++){
				valid[internalIndexOfT * numS + tis] = false;
			}
		}
		return internalIndexOfT * numS + is;
	}
}
