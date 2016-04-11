package com.luigisgro.cqf.fdm;

import java.io.PrintStream;

/**
 * Base class for implementing {@link Grid} objects.
 * It holds the grid parameters, and it implements all the methods to access its contents
 * @author Luigi Sgro
 *
 */
public abstract class BaseGrid implements Grid {
	protected double tStep;
	protected double tMin;
	protected double tMax;
	protected double sStep;
	protected double sMin;
	protected double sMax;
	
	protected int numT;
	protected int numS;

	public BaseGrid(double tStep, double tMin, double tMax, double sStep, double sMin, double sMax) {
		this.tStep = tStep;
		this.tMin = tMin;
		this.tMax = tMax;
		this.sStep = sStep;
		this.sMin = sMin;
		this.sMax = sMax;		

		numT = (int)((this.tMax - this.tMin) / this.tStep) + 1;
		numS = (int)((this.sMax - this.sMin) / this.sStep) + 1;
	}
	@Override
	public boolean validate(double vol, Derivative d) {
		int criticalTPoints = (int) (d.timeToNearestCashflow() / tStep);
		return criticalTPoints > vol * vol * numS * numS;
	}
	@Override
	public int numT() {
		return numT;
	}
	@Override
	public int numS() {
		return numS;
	}
	@Override
	public double tStep() {
		return tStep;
	}
	@Override
	public double sStep() {
		return sStep;
	}
	@Override
	public abstract double get(int it, int is);
	@Override
	public abstract void set(int it, int is, double value);
	@Override
	public double getPresent(int is) {
		return get(numT - 1, is);
	}
	@Override
	public double getPresentInterpolated(double s) {
		double v;
		if (s <= sMin)
			v = getPresent(0);
		else if (s >= sMax)
			v = getPresent(numS - 1);
		else {
			int is = getIndexOfS(s);
			double s1 = getS(is);
			double v1 = getPresent(is);
			double v2 = getPresent(is + 1);
			v = v1 + (v2 - v1) * (s - s1) / sStep;
		}
		return v;
	}
	@Override
	public double getSMin() {
		return sMin;
	}
	@Override
	public double getSMax() {
		return sMax;
	}
	@Override
	public double getS(int is) {
		return sMin + is * sStep;
	}
	@Override
	public double getT(int it) {
		return tMax - it * tStep;
	}
	private int getIndexOfS(double s) {
		for (int is = 0; is < numS; is++) {
			double curS = getS(is);
			if (s >= curS && s < curS + sStep)
				return is;
		}
		return -1;
	}
	@Override
	public void printStep(int it, PrintStream ps) {
		String line = "" + getT(it) + ";";
		for (int is = 0; is < numS - 1; is++) {
			line += get(it, is) + "; ";
		}
		line += get(it, numS - 1);
		ps.println(line);
	}
}
