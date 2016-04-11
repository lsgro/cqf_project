package com.luigisgro.cqf.job;

import static com.luigisgro.cqf.util.PropertiesAdapter.Type.REAL;
import static com.luigisgro.cqf.util.PropertiesAdapter.Type.STRING;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.luigisgro.cqf.fdm.Option.Direction;
import com.luigisgro.cqf.util.PropertiesAdapter;
import com.luigisgro.cqf.util.PropertyLoader;

/**
 * Implementation of {@link FDMJob.Configuration} that can read a configuration
 * from {@link java.util.Properties} object
 * @author Luigi Sgro
 *
 */
public class PropertiesFDMJobConfiguration implements FDMJob.Configuration, PropertyLoader {
	private PropertiesAdapter adapter;
	
	private static final String TIME_STEP = "fdm.timestep";
	private static final String UNDERLYING_VOL_MIN = "fdm.underlying.volmin";
	private static final String UNDERLYING_VOL_MAX = "fdm.underlying.volmax";
	private static final String UNDERLYING_VALUE = "fdm.underlying.value";
	private static final String UNDERLYING_STEP = "fdm.underlying.step";
	private static final String INTEREST_RATE = "fdm.ir";
	private static final String VOLATILITY_TYPE = "fdm.volatility.type";
	
	private static final String DERIVATIVE_TYPE = "fdm.option.type";
	private static final String DERIVATIVE_MATURITY = "fdm.option.maturity";
	private static final String DERIVATIVE_POSITION = "fdm.option.position";
	private static final String DERIVATIVE_DIRECTION = "fdm.option.direction";
	private static final String DERIVATIVE_STRIKE = "fdm.option.strike";

	private static final String HEDGE_TYPE = "fdm.hedge.type";
	private static final String HEDGE_MATURITY = "fdm.hedge.maturity";
	private static final String HEDGE_POSITION = "fdm.hedge.position";
	private static final String HEDGE_DIRECTION = "fdm.hedge.direction";
	private static final String HEDGE_STRIKE = "fdm.hedge.strike";
	private static final String HEDGE_PRICE_TYPE = "fdm.hedge.price.type";
	private static final String HEDGE_PRICE = "fdm.hedge.price";
	
	private static final Object[][] parameterConfiguration = {
		{ TIME_STEP, REAL, true },
		{ UNDERLYING_VOL_MIN, REAL, false },
		{ UNDERLYING_VOL_MAX, REAL, true },
		{ UNDERLYING_VALUE, REAL, true },
		{ UNDERLYING_STEP, REAL, true },
		{ INTEREST_RATE, REAL, true },
		{ VOLATILITY_TYPE, STRING, true },
		{ DERIVATIVE_TYPE, STRING, true },
		{ DERIVATIVE_MATURITY, REAL, true },
		{ DERIVATIVE_POSITION, REAL, true },
		{ DERIVATIVE_DIRECTION, STRING, true },
		{ DERIVATIVE_STRIKE, REAL, true }
	};
	private static final Object[][] multiParameterConfiguration = {
		{ HEDGE_TYPE, STRING, true },
		{ HEDGE_MATURITY, REAL, true },
		{ HEDGE_POSITION, REAL, true },
		{ HEDGE_DIRECTION, STRING, false },
		{ HEDGE_STRIKE, REAL, true },
		{ HEDGE_PRICE_TYPE, STRING, true },
		{ HEDGE_PRICE, REAL, true }
	};
	
	private Double timeStep;
	private Double underlyingVolMin;
	private Double underlyingVolMax;
	private Double underlyingValue;
	private Double underlyingStep;
	private Double interestRate;
	private String volatilityType;
	private String derivativeType;
	private Double derivativeMaturity;
	private Double derivativePosition;
	private Direction derivativeDirection;
	private Double derivativeStrike;
	
	private List<String> hedgeType = new ArrayList<String>();
	private List<Double> hedgeMaturity = new ArrayList<Double>();
	private List<Double> hedgePosition = new ArrayList<Double>();
	private List<Direction> hedgeDirection= new ArrayList<Direction>();
	private List<Double> hedgeStrike = new ArrayList<Double>();
	private List<String> hedgePriceType = new ArrayList<String>();
	private List<Double> hedgePrice = new ArrayList<Double>();
	
	@Override
	public boolean loadProperties(Map<Object, Object> properties) {
		adapter = new PropertiesAdapter(properties);
		
		boolean success = adapter.loadParameters(parameterConfiguration);
		if (!success) {
			System.err.println(adapter.getMessage());
			return false;
		}
		
		success = adapter.loadMultiParameters(multiParameterConfiguration);
		if (!success) {
			System.err.println(adapter.getMessage());
			return false;
		}
		
		timeStep = (Double)adapter.getParameters().get(TIME_STEP);
		underlyingVolMin = (Double)adapter.getParameters().get(UNDERLYING_VOL_MIN);
		underlyingVolMax = (Double)adapter.getParameters().get(UNDERLYING_VOL_MAX);
		underlyingValue = (Double)adapter.getParameters().get(UNDERLYING_VALUE);
		underlyingStep = (Double)adapter.getParameters().get(UNDERLYING_STEP);
		interestRate = (Double)adapter.getParameters().get(INTEREST_RATE);
		volatilityType = (String)adapter.getParameters().get(VOLATILITY_TYPE);
		derivativeType = (String)adapter.getParameters().get(DERIVATIVE_TYPE);
		derivativeMaturity = (Double)adapter.getParameters().get(DERIVATIVE_MATURITY);
		derivativePosition = (Double)adapter.getParameters().get(DERIVATIVE_POSITION);
		derivativeDirection = "call".equals((String)adapter.getParameters().get(DERIVATIVE_DIRECTION)) ? Direction.CALL : Direction.PUT;
		derivativeStrike = (Double)adapter.getParameters().get(DERIVATIVE_STRIKE);
		
		for (int group = 1; group <= adapter.getNumberOfGroups(); group++) {
			hedgeType.add((String)adapter.getParameters().get(HEDGE_TYPE + "." + group));
			hedgeMaturity.add((Double)adapter.getParameters().get(HEDGE_MATURITY + "." + group));
			hedgePosition.add((Double)adapter.getParameters().get(HEDGE_POSITION + "." + group));
			hedgeDirection.add("call".equals((String)adapter.getParameters().get(HEDGE_DIRECTION + "." + group)) ? Direction.CALL : Direction.PUT);
			hedgeStrike.add((Double)adapter.getParameters().get(HEDGE_STRIKE + "." + group));
			hedgePriceType.add((String)adapter.getParameters().get(HEDGE_PRICE_TYPE + "." + group));
			hedgePrice.add((Double)adapter.getParameters().get(HEDGE_PRICE + "." + group));
		}
		return true;
	}
	
	@Override
	public Double getTimeStep() {
		return timeStep;
	}

	@Override
	public Direction getDerivativeDirection() {
		return derivativeDirection;
	}

	@Override
	public Double getDerivativeStrike() {
		return derivativeStrike;
	}

	@Override
	public Double getDerivativeMaturity() {
		return derivativeMaturity;
	}

	@Override
	public Object getDerivativeType() {
		return derivativeType;
	}

	@Override
	public Double getDerivativePosition() {
		return derivativePosition;
	}

	@Override
	public Integer getHedgeNumber() {
		return adapter.getNumberOfGroups();
	}

	@Override
	public Double getMinimumVolatility() {
		return underlyingVolMin;
	}

	@Override
	public Double getMaximumVolatility() {
		return underlyingVolMax;
	}

	@Override
	public Double getInterestRate() {
		return interestRate;
	}

	@Override
	public Double getUnderlyingValue() {
		return underlyingValue;
	}

	@Override
	public Double getUnderlyingStep() {
		return underlyingStep;
	}

	@Override
	public String getVolatilityType() {
		return volatilityType;
	}

	@Override
	public Double getHedgeStrike(int i) {
		return hedgeStrike.get(i - 1);
	}

	@Override
	public Double getHedgeMaturity(int i) {
		return hedgeMaturity.get(i - 1);
	}

	@Override
	public Direction getHedgeDirection(int i) {
		return hedgeDirection.get(i - 1);
	}

	@Override
	public Double getHedgePosition(int i) {
		return hedgePosition.get(i - 1);
	}

	@Override
	public String getHedgeType(int i) {
		return hedgeType.get(i - 1);
	}

	@Override
	public Object getHedgePriceType(int i) {
		return hedgePriceType.get(i - 1);
	}

	@Override
	public Double getHedgePrice(int i) {
		return hedgePrice.get(i - 1);
	}
}
