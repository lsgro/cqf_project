package com.luigisgro.cqf.fdm;

/**
 * An implementation of plain vanilla option
 * @author Luigi Sgro
 *
 */
public class VanillaOption extends Option implements Derivative {
	public VanillaOption(Direction direction, double strike, double maturity) {
		this.direction = direction;
		this.strike = strike;
		this.maturity = maturity;
	}

	@Override
	public double cashflow(double t, double tStep, double s) {
		if (t > maturity - tStep / 2 && t < maturity + tStep / 2) {
			if (Direction.CALL.equals(direction))
				return Math.max(s - strike, 0);
			else
				return Math.max(strike - s, 0);
		} else
			return 0;
	}

	@Override
	public double boundaryValue(double t, double tStep, double s, double r) {
		if (t > maturity + tStep / 2)
			return 0;
		if (Direction.CALL.equals(direction) && s >= strike * 2.0) {
			return s - strike * Math.exp(r * (t - maturity));
		} else if (Direction.PUT.equals(direction) && s <= strike / 10.0) {
			return strike - s * Math.exp(r * (t - maturity));
		} else if (Direction.PUT.equals(direction) && s >= strike * 2.0 || Direction.CALL.equals(direction) && s <= strike / 10.0) {
			return 0.0;
		} else {
			throw new IllegalArgumentException("Boundary not valid: " + s);
		}
	}

	@Override
	public double boundaryValueOpt(int it, int is, Grid g) {
		if (g.getT(it) > maturity + g.tStep() / 2)
			return 0;
		if (Direction.CALL.equals(direction) && g.getS(is) >= strike * 2.0) {
			return g.get(it, is - 1) + (g.get(it, is - 1) - g.get(it, is - 2));
		} else if (Direction.PUT.equals(direction) && g.getS(is) <= strike / 10.0) {
			return g.get(it, is + 1) + (g.get(it, is + 1) - g.get(it, is + 2));
		} else if (Direction.PUT.equals(direction) && g.getS(is) >= strike * 2.0 || Direction.CALL.equals(direction) && g.getS(is) <= strike / 10.0) {
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
