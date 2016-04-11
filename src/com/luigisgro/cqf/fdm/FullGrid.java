package com.luigisgro.cqf.fdm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Memory-intensive implementation of {@link Grid}. All the grid points are retained.
 * At the end of the pricing process it can export all the steps to CVS
 * @author Luigi Sgro
 *
 */
public class FullGrid extends BaseGrid {
	private double[] nodes;
	
	/**
	 * Instantiation of a FullGrid
	 * @param tStep Time step
	 * @param tMin Time range minimum value
	 * @param tMax Time range maximum value
	 * @param sStep Stock value step
	 * @param sMin Stock value range minimum value
	 * @param sMax Stock value range maximum value
	 */
	public FullGrid(double tStep, double tMin, double tMax, double sStep, double sMin, double sMax) {
		super(tStep, tMin, tMax, sStep, sMin, sMax);
		nodes = new double[numT * numS];
	}
	@Override
	public double get(int it, int is) {
		return nodes[it + numT * is];
	}
	@Override
	public void set(int it, int is, double value) {
		nodes[it + numT * is] = value;
	}
	
	/**
	 * Exports in CVS format all the step of the pricing analysis
	 * @param oStream The {@link java.io.OutputStream} to be used for printing
	 * @return The number of lines exported
	 * @throws IOException
	 */
	public int exportToCVS(OutputStream oStream) throws IOException {
		PrintStream printer = new PrintStream(oStream);
		String line = "time \\ stock;";
		for (int s = 0; s < numS - 1; s++) {
			line += getS(s) + "; ";
		}
		line += getS(numS - 1);
		printer.println(line);
		int lines = 0;
		for (int it = 0; it < numT; it++) {
			printStep(it, printer);
			lines++;
		}			
		return lines;
	}
	@Override
	public void reset() {
		for (int i = 0; i < nodes.length; i++)
			nodes[i] = 0;		
	}
}
