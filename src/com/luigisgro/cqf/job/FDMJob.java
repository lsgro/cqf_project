package com.luigisgro.cqf.job;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.MathException;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.direct.NelderMead;

import com.luigisgro.cqf.fdm.BinaryCashOrNothingOption;
import com.luigisgro.cqf.fdm.Derivative;
import com.luigisgro.cqf.fdm.ExplicitMethod;
import com.luigisgro.cqf.fdm.ExplicitMethodConstantVolatility;
import com.luigisgro.cqf.fdm.ExplicitMethodUncertainVolatility;
import com.luigisgro.cqf.fdm.Grid;
import com.luigisgro.cqf.fdm.Option.Direction;
import com.luigisgro.cqf.fdm.Portfolio;
import com.luigisgro.cqf.fdm.StaticHedgingStrategy;
import com.luigisgro.cqf.fdm.StaticHedgingStrategyOptimizationAdapter;
import com.luigisgro.cqf.fdm.TwoStepsGrid;
import com.luigisgro.cqf.fdm.VanillaOption;
import com.luigisgro.cqf.util.BlackScholes;

/**
 * This class implements all the steps of the project FDM process
 * @author Luigi Sgro
 *
 */
public class FDMJob implements Job {
	/**
	 * Configuration object for this job
	 * It declares all the values that must be provided to run the job
	 * @author Luigi Sgro
	 *
	 */
	public static interface Configuration {
		/**
		 * @return The direction of the derivative to be priced: "call" or "put"
		 */
		Direction getDerivativeDirection();
		/**
		 * @return The strike of the derivative to be priced
		 */
		Double getDerivativeStrike();
		/**
		 * @return The maturity in years of the derivative to be priced
		 */
		Double getDerivativeMaturity();
		/**
		 * @return The type of the derivative to be priced: "binary" or "vanilla"
		 */
		Object getDerivativeType();
		/**
		 * @return The position (positive or negative) in number of contracts of the derivative to be priced
		 */
		Double getDerivativePosition();
		/**
		 * @return The number of hedge instrument to insert into the strategy
		 */
		Integer getHedgeNumber();
		/**
		 * @return The minimum volatility of the underlying (needed only when volatility type = "uncertain")
		 */
		Double getMinimumVolatility();
		/**
		 * @return The maximum volatility of the underlying
		 */
		Double getMaximumVolatility();
		/**
		 * @return The interest rate
		 */
		Double getInterestRate();
		/**
		 * @return The value of the underlying used for the pricing of the derivative
		 */
		Double getUnderlyingValue();
		/**
		 * @return The step of the underlying used for the calculations
		 */
		Double getUnderlyingStep();
		/**
		 * @return The time step used for the calculations
		 */
		Double getTimeStep();
		/**
		 * @return The type of pricing to perform: "uncertain" for uncertain volatility, "constant" for constant volatility
		 */
		String getVolatilityType();
		/**
		 * @return The strike of the i-th hedge instrument
		 */
		Double getHedgeStrike(int i);
		/**
		 * @return The maturity in years of the i-th hedge instrument
		 */
		Double getHedgeMaturity(int i);
		/**
		 * @return The direction of the i-th hedge instrument: "call" or "put"
		 */
		Direction getHedgeDirection(int i);
		/**
		 * @return The position (positive or negative) of the i-th hedge instrument
		 */
		Double getHedgePosition(int i);
		/**
		 * @return The type of the i-th hedge instrument: "binary" or "vanilla"
		 */
		String getHedgeType(int i);
		/**
		 * @return "automatic" if the price should be calulated automatically from Black-Scholes, "fixed" otherwise
		 */
		Object getHedgePriceType(int i);
		/**
		 * @return The price of the i-th hedge instrument (not needed if hedge price type is "automatic")
		 */
		Double getHedgePrice(int i);
	}
	
	Configuration jobConfiguration;
	
	private double maxMaturity;
	private double maxStrike;
	private double sMin;
	private double sMax;
	
	private Map<String, Object> results;
	
	public FDMJob(Configuration jobConfiguration) {
		this.jobConfiguration = jobConfiguration;
	}
		
	private void updateUnderlyingRange() {
		sMin = 0;
		sMax = maxStrike * 2;		
	}
	
	private boolean addDerivative(Portfolio portfolio) {
		Derivative derivative;
		Direction derivativeDirection = jobConfiguration.getDerivativeDirection();
		double derivativeStrike = jobConfiguration.getDerivativeStrike();
		double derivativeMaturity = jobConfiguration.getDerivativeMaturity();
		
		if ("binary".equals(jobConfiguration.getDerivativeType())) {
			System.out.println("Add " + derivativeDirection + " binary option e: " + derivativeStrike + ", maturity: " + derivativeMaturity);
			derivative = new BinaryCashOrNothingOption(derivativeDirection, derivativeStrike, derivativeMaturity);
		} else if ("vanilla".equals(jobConfiguration.getDerivativeType())) {
			System.out.println("Add " + derivativeDirection + " vanilla option e: " + derivativeStrike + ", maturity: " + derivativeMaturity);
			derivative = new VanillaOption(derivativeDirection, derivativeStrike, derivativeMaturity);
		} else {
			System.err.println("Unknown derivative type: " + jobConfiguration.getDerivativeType());
			return false;
		}
		portfolio.getItems().put(derivative, portfolio.new Item(jobConfiguration.getDerivativePosition(), 0, false));
		
		if (derivativeMaturity > maxMaturity)
			maxMaturity = derivativeMaturity;
		
		if (derivativeStrike > maxStrike)
			maxStrike = derivativeStrike;
		
		return true;
	}
	
	private boolean addHedge(Portfolio portfolio, double underlyingValue, double interestRate, double hedgePriceVolatility) throws MathException {
		Derivative hedge;
		double hedgeStrike;
		double hedgeMaturity;
		double blackScholesPrice;
		double hedgePosition;
		Direction hedgeDirection;
		for (int i = 1; i <= jobConfiguration.getHedgeNumber(); i++) {
			
			hedgeStrike = jobConfiguration.getHedgeStrike(i);
			hedgeMaturity = jobConfiguration.getHedgeMaturity(i);
			
			if (hedgeMaturity > maxMaturity)
				maxMaturity = hedgeMaturity;
			
			if (hedgeStrike > maxStrike)
				maxStrike = hedgeStrike;
			
			hedgeDirection = jobConfiguration.getHedgeDirection(i);
			hedgePosition = jobConfiguration.getHedgePosition(i);
			if ("binary".equals(jobConfiguration.getHedgeType(i))) {
				System.out.println("Add " + hedgeDirection + " binary option e: " + hedgeStrike + ", maturity: " + hedgeMaturity + " as hedge");
				blackScholesPrice = BlackScholes.binaryCashOrNothingValue(underlyingValue, hedgeStrike, hedgeMaturity, hedgePriceVolatility, interestRate, hedgeDirection);
				hedge = new BinaryCashOrNothingOption(hedgeDirection, hedgeStrike, hedgeMaturity);
			} else if ("vanilla".equals(jobConfiguration.getHedgeType(i))) {
				System.out.println("Add " + hedgeDirection + " vanilla option e: " + hedgeStrike + ", maturity: " + hedgeMaturity + " as hedge");
				hedge = new VanillaOption(hedgeDirection, hedgeStrike, hedgeMaturity);
				blackScholesPrice = BlackScholes.vanillaOptionValue(underlyingValue, hedgeStrike, hedgeMaturity, hedgePriceVolatility, interestRate, hedgeDirection);
			} else {
				System.err.println("Hedge type " + jobConfiguration.getHedgeType(i) + " for hedge " + i + " is unknown");
				return false;
			}
			
			double hedgePrice;
			if ("automatic".equals(jobConfiguration.getHedgePriceType(i))) {
				hedgePrice = blackScholesPrice;
				System.out.println("	set hedge price to Black Scholes theoretical price: " + hedgePrice);
			} else {
				hedgePrice = jobConfiguration.getHedgePrice(i);
				System.out.println("	set hedge price to: " + hedgePrice);
			}
			
			System.out.println("	set hedge position to: " + hedgePosition);
			portfolio.getItems().put(hedge, portfolio.new Item(hedgePosition, hedgePrice, true));
		}
		return true;
	}
	
	@Override
	public boolean execute() throws MathException {
		// Load parameters from configuration
		Double minVol = jobConfiguration.getMinimumVolatility();
		Double maxVol = jobConfiguration.getMaximumVolatility();
		Double interestRate = jobConfiguration.getInterestRate();		
		Double underlyingValue = jobConfiguration.getUnderlyingValue();
		Double underlyingStep = jobConfiguration.getUnderlyingStep();
		Double timeStep = jobConfiguration.getTimeStep();
		String volatilityType = jobConfiguration.getVolatilityType();
		
		// Create a new results map
		results = new HashMap<String, Object>();
		
		// calculationMethod is responsible for calculating the next step of Finite Difference Method
		ExplicitMethod calculationMethod;
		double hedgePriceVolatility;
		if ("uncertain".equals(volatilityType)) {
			calculationMethod = new ExplicitMethodUncertainVolatility(minVol, maxVol);
			hedgePriceVolatility = (minVol + maxVol) / 2;
		} else {
			calculationMethod = new ExplicitMethodConstantVolatility(maxVol);
			hedgePriceVolatility = maxVol;
		}
		
		// Dump parameters to screen
		System.out.println("Vol type: " + volatilityType);
		if ("uncertain".equals(volatilityType))
			System.out.println("Min vol: " + minVol);
		System.out.println("Max vol: " + maxVol);
		System.out.println("Interest rate:" + interestRate);
		System.out.println("Underlying value: " + underlyingValue);
		System.out.println("Underlying increment step: " + underlyingStep);
		System.out.println("Time increment step: " + timeStep);
		System.out.println();

		// Create the empty portfolio
		Portfolio portfolio = new Portfolio();
		
		//------------------------------------------------------------------------------------------------//
		// First stage: pricing the naked derivative
		//------------------------------------------------------------------------------------------------//

		System.out.println("Derivative pricing with no hedge:");
		
		if (!addDerivative(portfolio))
			return false;
		updateUnderlyingRange(); // Update the range of the underlying to suit boundary criteria

		// Create and validate the FDM grid
		Grid gridNakedDerivative = new TwoStepsGrid(timeStep, 0, maxMaturity + 1, underlyingStep, sMin, sMax);
		if (!gridNakedDerivative.validate(maxVol, portfolio)) {
			System.err.println("underlying step: " + underlyingStep + ", timeStep: " + timeStep + ", volatility: " + maxVol + " will not converge");
			return false;
		}

		// Create the empty hedging strategy
		StaticHedgingStrategy strategyNakedDerivative = new StaticHedgingStrategy(gridNakedDerivative, portfolio, calculationMethod, interestRate);
		
		// Run the FDM pricing
		strategyNakedDerivative.runPricing();
		
		// Get and print FDM result
		double longNoHedge = strategyNakedDerivative.getPnLAt(underlyingValue);
		System.out.println("Long derivative value: " + longNoHedge);
		
		// Store result in results map
		results.put("nakedLongDerivativeValue", longNoHedge);
		
		// Invert positions in the portfolio to calculate the short value
		portfolio.invertPositions();
		
		// Run the FDM pricing
		strategyNakedDerivative.runPricing();
		
		// Get and print FDM result
		double shortNoHedge = strategyNakedDerivative.getPnLAt(underlyingValue);
		System.out.println("Short derivative value: " + shortNoHedge);		

		// Store result in results map
		results.put("nakedShortDerivativeValue", shortNoHedge);

		// Consolidate long and short values in a bid-ask quote
		System.out.println("Proposed bid: " + longNoHedge + ", ask: " + (-shortNoHedge) + "\n");
		
		//------------------------------------------------------------------------------------------------//
		// Second stage: pricing the derivative with static hedge - not optimized
		//------------------------------------------------------------------------------------------------//

		portfolio.invertPositions();

		System.out.println("Derivative pricing with hedge (not optimized):");
		
		// Add hedge instruments
		if (!addHedge(portfolio, underlyingValue, interestRate, hedgePriceVolatility))
			return false;
		updateUnderlyingRange(); // Update the range of the underlying to suit boundary criteria

		// Create and validate a new FDM grid
		Grid gridDerivativePlusHedge = new TwoStepsGrid(timeStep, 0, maxMaturity + 1, underlyingStep, sMin, sMax);		
		if (!gridDerivativePlusHedge.validate(maxVol, portfolio)) {
			System.err.println("underlying step: " + underlyingStep + ", timeStep: " + timeStep + ", volatility: " + maxVol + " will not converge");
			return false;
		}

		// Create the hedging strategy
		StaticHedgingStrategy strategyDerivativePlusHedge = new StaticHedgingStrategy(gridDerivativePlusHedge, portfolio, calculationMethod, interestRate);

		// Run the FDM pricing
		strategyDerivativePlusHedge.runPricing();

		// Get and print FDM result
		double longHedge = strategyDerivativePlusHedge.getPnLAt(underlyingValue);
		System.out.println("Long derivative, hedge: " + strategyDerivativePlusHedge.hedgePositionsDesc() + " Value: " + longHedge);
		
		// Store result in results map
		results.put("fixedHedgeLongDerivativeValue", longHedge);
		
		// Invert positions in the portfolio to calculate the short value
		portfolio.invertPositions();
		
		// Run the FDM pricing
		strategyDerivativePlusHedge.runPricing();

		// Get and print FDM result
		double shortHedge = strategyDerivativePlusHedge.getPnLAt(underlyingValue);
		System.out.println("Short derivative, hedge: " + strategyDerivativePlusHedge.hedgePositionsDesc() + " Value: " + shortHedge);
		
		// Store result in results map
		results.put("fixedHedgeShortDerivativeValue", shortHedge);

		// Consolidate long and short values in a bid-ask quote
		System.out.println("Proposed bid: " + longHedge + ", ask: " + (-shortHedge) + "\n");

		//------------------------------------------------------------------------------------------------//
		// Third stage: pricing the derivative with static hedge - optimized
		//------------------------------------------------------------------------------------------------//

		portfolio.invertPositions();
		
		System.out.println("Derivative pricing with optimized hedge:\n");
		
		// Create an adapter to use the Commons Math standard optimized against the static hedging strategy
		StaticHedgingStrategyOptimizationAdapter adapter = new StaticHedgingStrategyOptimizationAdapter(strategyDerivativePlusHedge, underlyingValue);
		
		// Create the Commons Math optimizer
		NelderMead optimizer = new NelderMead();
		
		// Run the optimization for the long position
		long startOptimize = System.currentTimeMillis();
		System.out.println("Long derivative: running optimizer on hedge positions...");
		try {
			optimizer.optimize(adapter, GoalType.MAXIMIZE, strategyDerivativePlusHedge.hedgePositions());
		} catch (Exception e) {
			System.err.println("An error occurred during the optimization [" + e.getMessage() + "]");
			e.printStackTrace();
			return false;
		} 

		// Output optimization results for the long position
		System.out.println("Long derivative: optimizer converged in " + optimizer.getIterations() + " iterations, time elapsed: " + (System.currentTimeMillis() - startOptimize) + "ms");
		double longOptimizedHedge = strategyDerivativePlusHedge.getPnLAt(underlyingValue);
		System.out.println("Long derivative, hedge: " + strategyDerivativePlusHedge.hedgePositionsDesc() + " Value: " + longOptimizedHedge);
		
		// Store result in results map
		results.put("optimizedHedgeLongDerivativeValue", longOptimizedHedge);
		results.put("optimizedHedgeLongIterations", optimizer.getIterations());

		// Invert positions in the portfolio to calculate the short value
		portfolio.invertPositions();

		// Run the optimization for the short position
		startOptimize = System.currentTimeMillis();
		System.out.println("\nShort derivative: running optimizer on hedge positions...");
		try {
			optimizer.optimize(adapter, GoalType.MAXIMIZE, strategyDerivativePlusHedge.hedgePositions());
		} catch (Exception e) {
			System.err.println("An error occurred during the optimization [" + e.getMessage() + "]");
			return false;
		} 

		// Output optimization results for the long position
		System.out.println("Short derivative: optimizer converged in " + optimizer.getIterations() + " iterations, time elapsed: " + (System.currentTimeMillis() - startOptimize) + "ms");
		double shortOptimizedHedge = strategyDerivativePlusHedge.getPnLAt(underlyingValue);
		System.out.println("Short derivative, hedge: " + strategyDerivativePlusHedge.hedgePositionsDesc() + " Value: " + shortOptimizedHedge);

		// Store result in results map
		results.put("optimizedHedgeShortDerivativeValue", shortOptimizedHedge);
		results.put("optimizedHedgeShortIterations", optimizer.getIterations());

		// Consolidate long and short values in a bid-ask quote
		System.out.println("\nProposed bid: " + longOptimizedHedge + ", ask: " + (-shortOptimizedHedge) + "\n");

		return true;
	}

	@Override
	public Map<String, Object> getResults() {
		return results;
	}
}
