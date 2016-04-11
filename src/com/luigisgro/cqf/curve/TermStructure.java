package com.luigisgro.cqf.curve;

import java.text.NumberFormat;

/**
 * A set of maturities (tenors) that define a family of curves
 * @author Luigi Sgro
 *
 */
public class TermStructure {
	private double[] tenors;
	
	/**
	 * Class constructor
	 * @param tenors An array of maturities
	 */
	public TermStructure(double[] tenors) {
		this.tenors = tenors;
	}

	public double[] getTenors() {
		return tenors;
	}
	
	public String toString() {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(3);
		String desc = "";
		for (int k = 0; k < tenors.length - 1; k++) {
			desc += nf.format(tenors[k]) + "; ";
		}
		desc += nf.format(tenors[tenors.length - 1]);
		return desc;
	}
	
	public int hashCode() {
		return tenors.hashCode();
	}
	
	public boolean equals(Object other) {
		if (! (other instanceof TermStructure))
			return false;
		return tenors.equals(((TermStructure)other).tenors);
	}
}
