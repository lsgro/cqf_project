package com.luigisgro.cqf.fdm;

/**
 * Base class for options
 * @author Luigi Sgro
 *
 */
public abstract class Option {
	/**
	 * The direction of an option: CALL or PUT
	 * @author Luigi Sgro
	 *
	 */
	public static enum Direction { CALL, PUT }
	
	protected Direction direction;
	protected double strike;
	protected double maturity;
	
	/**
	 * Accessor method for option direction
	 * @return {@link Direction#CALL} or {@link Direction#PUT}
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Accessor method for the strike price of the option
	 * @return The option strike
	 */
	public double getStrike() {
		return strike;
	}
	
	/**
	 * Accessor method for the maturity of the option
	 * @return The option maturity, expressed as a fraction of one year
	 */
	public double getMaturity() {
		return maturity;
	}
}
