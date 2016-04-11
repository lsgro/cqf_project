package com.luigisgro.cqf.hjm;

import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.CurveTimeSeriesOperator;
import com.luigisgro.cqf.curve.TimePoint;

/**
 * Structure common to Cap and Floor, which uses simple caplets and floorlets
 * to implement the complex evaluation of these derivatives
 * @author Luigi Sgro
 * @see Xlet
 * @param <T>
 */
public abstract class SeriesOfOptions<T extends TimePoint> implements CurveTimeSeriesOperator<T>, XletFactory<T> {
	private int numberOfPeriods;
	private int numberOfTimePointsPerPeriod;
	private Xlet<T>[] xlets;
	protected double strike;
	
	/**
	 * Creates a new Cap or Floor
	 * @param strike The strike of the instrument
	 * @param tenor The tenor of the caplets/floorlets
	 * @param maturity Maturity of the instrument
	 * @param calendar The calendar used for the evaluation
	 * @param timeStep The fraction of year corresponding to one calendar day
	 */
	@SuppressWarnings("unchecked")
	public SeriesOfOptions(double strike, double tenor, double maturity, T[] calendar, double timeStep) {
		this.strike = strike;
		numberOfPeriods = (int)(maturity / tenor);
		numberOfTimePointsPerPeriod = (int)(tenor / timeStep);
		xlets = (Xlet<T>[])(new Xlet[numberOfPeriods]);
		// Preparation of all the xlets, relying on the abstract factory method XletFactory.createXlet, implemented by concrete classes
		for (int period = 0; period < numberOfPeriods; period++) {
			int evaluationTimeIndex = numberOfTimePointsPerPeriod * (period);
			int cashflowTimeIndex = numberOfTimePointsPerPeriod * (period + 1);
			xlets[period] = createXlet(calendar[0], calendar[evaluationTimeIndex], calendar[cashflowTimeIndex], tenor, timeStep);
		}
	}
	
	@Override
	public double evaluate(CurveTimeSeries<T> scenario) {
		double value = 0.0;
		for (int period = 0; period < numberOfPeriods; period++) {
			value += xlets[period].evaluate(scenario);
		}
		return value;
	}
}
