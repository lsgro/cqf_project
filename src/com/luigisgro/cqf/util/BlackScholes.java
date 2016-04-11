package com.luigisgro.cqf.util;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

import com.luigisgro.cqf.fdm.Option.Direction;


/**
 * Utility class that calculates some basic results from Black-Scholes model.
 * Used for validating the results of the FDM.
 * @author Luigi Sgro
 *
 */
public class BlackScholes {
	private static final NormalDistribution N = new NormalDistributionImpl();
	public static double d1(double s, double e, double ttm, double vol, double r) {
		return (Math.log(s/e)+(r+0.5*vol*vol)*ttm)/(vol*Math.sqrt(ttm));
	}
	public static double d2(double s, double e, double ttm, double vol, double r) {
		return (Math.log(s/e)+(r-0.5*vol*vol)*ttm)/(vol*Math.sqrt(ttm));
	}
	public static double vanillaOptionValue(double s, double e, double ttm, double vol, double r, Direction direction) throws MathException {
		if (Direction.CALL.equals(direction))
			return s*N.cumulativeProbability(d1(s, e, ttm, vol, r)) - Math.exp(-r*ttm)*e*N.cumulativeProbability(d2(s, e, ttm, vol, r));
		else
			return -s*N.cumulativeProbability(-d1(s, e, ttm, vol, r)) + Math.exp(-r*ttm)*e*N.cumulativeProbability(-d2(s, e, ttm, vol, r));
	}
	public static double vanillaOptionDelta(double s, double e, double ttm, double vol, double r, Direction direction) throws MathException {
		if (Direction.CALL.equals(direction))
			return N.cumulativeProbability(d1(s, e, ttm, vol, r));
		else
			return -N.cumulativeProbability(-d1(s, e, ttm, vol, r));			
	}
	public static double binaryCashOrNothingValue(double s, double e, double ttm, double vol, double r, Direction direction) throws MathException {
		if (Direction.CALL.equals(direction))
			return Math.exp(-r*ttm)*N.cumulativeProbability(d2(s, e, ttm, vol, r));
		else
			return Math.exp(-r*ttm)*N.cumulativeProbability(-d2(s, e, ttm, vol, r));
	}
}
