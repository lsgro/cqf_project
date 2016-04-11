package com.luigisgro.cqf.fdm;


/**
 * Implementation of a binary "cash-or-nothing" option. The payoff of this option
 * is 1 if the stock value at maturity is greater or equal to the strike, zero
 * otherwise
 * @author Luigi Sgro
 *
 */
public class BinaryCashOrNothingOption extends Option implements Derivative {
	/**
	 * Class constructor. It takes option direction, strike and maturity (fraction of year)
	 * @param direction Either {@link Option.Direction#CALL} or {@link Option.Direction#PUT}
	 * @param strike The strike of the option
	 * @param maturity Maturity, expressed as fraction of year
	 */
	public BinaryCashOrNothingOption(Direction direction, double strike, double maturity) {
		this.direction = direction;
		this.strike = strike;
		this.maturity = maturity;
	}

	@Override
	public double cashflow(double t, double tStep, double s) {
		if (t > maturity - tStep / 2 && t < maturity + tStep / 2) {
			if (Direction.CALL.equals(direction))
				return s >= strike ? 1 : 0;
			else
				return s < strike ? 1 : 0;
		} else
			return 0;
	}

	@Override
	public double boundaryValue(double t, double tStep, double s, double r) {
		if (t > maturity + tStep / 2)
			return 0;
		if (Direction.CALL.equals(direction) && s >= strike * 1.5 || Direction.PUT.equals(direction) && s <= strike / 2) {
			return Math.exp(r * (t - maturity));
		} else if (Direction.PUT.equals(direction) && s >= strike * 1.5 || Direction.CALL.equals(direction) && s <= strike / 2) {
			return 0.0;
		} else {
			throw new IllegalArgumentException("Boundary not valid: " + s);
		}
	}

	@Override
	public double boundaryValueOpt(int it, int is, Grid g) {
		if (g.getT(it) > maturity + g.tStep() / 2)
			return 0;
		if (Direction.CALL.equals(direction) && g.getS(is) >= strike * 1.5 || Direction.PUT.equals(direction) && g.getS(is) <= strike / 2) {
			return g.get(it, is - 1) + (g.get(it, is - 1) - g.get(it, is - 2));
		} else if (Direction.PUT.equals(direction) && g.getS(is) >= strike * 1.5 || Direction.CALL.equals(direction) && g.getS(is) <= strike / 2) {
			return 0.0;
		} else {
			throw new IllegalArgumentException("Boundary not valid: " + g.getS(is));
		}
	}

	@Override
	public double timeToMaturity() {
		return maturity;
	}

	@Override
	public double timeToNearestCashflow() {
		return maturity;
	}
}
