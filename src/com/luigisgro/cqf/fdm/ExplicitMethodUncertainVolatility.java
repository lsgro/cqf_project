package com.luigisgro.cqf.fdm;

/**
 * Implementation of the {@link ExplicitMethod} for uncertain volatility.
 * The theta is calculated using either the minimum or maximum volatility, depending
 * on the gamma provided, giving always the minimum value of theta possible with
 * the volatility values provided, thus minimizing the value of the derivative to price.
 * @author Luigi Sgro
 *
 */
public class ExplicitMethodUncertainVolatility extends ExplicitMethod {
	private double volMin;
	private double volMax;
	/**
	 * 
	 * @param volMin The minimum volatility to use for pricing
	 * @param volMax The maximum volatility to use for pricing
	 */
	public ExplicitMethodUncertainVolatility(double volMin, double volMax) {
		this.volMin = volMin;
		this.volMax = volMax;
	}

	@Override
	public double theta(double s, double value, double delta, double gamma, double r) {
		double vol = gamma > 0.0 ? volMin : volMax;
		return r * value - r * s * delta - 0.5 * vol * vol * s * s * gamma;
	}

	@Override
	public double maxVol() {
		return volMax;
	}
}
