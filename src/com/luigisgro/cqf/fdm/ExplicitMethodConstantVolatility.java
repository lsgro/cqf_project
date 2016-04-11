package com.luigisgro.cqf.fdm;

/**
 * Implementation of {@link ExplicitMethod} with constant volatility.
 * The theta is simply a function of volatility, interest rate, and the other partial derivatives.
 * @author Luigi Sgro
 *
 */
public class ExplicitMethodConstantVolatility extends ExplicitMethod {
	private double vol;
	public ExplicitMethodConstantVolatility(double vol) {
		this.vol = vol;
	}

	@Override
	public double theta(double s, double value, double delta, double gamma, double r) {
		return - 0.5 * vol * vol * s * s * gamma - r * s * delta + r * value;
	}

	@Override
	public double maxVol() {
		return vol;
	}
}
