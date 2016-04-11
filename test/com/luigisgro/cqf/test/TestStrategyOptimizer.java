package com.luigisgro.cqf.test;

import junit.framework.Assert;

import org.apache.commons.math.MathException;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.direct.NelderMead;
import org.junit.Test;

import com.luigisgro.cqf.fdm.BinaryCashOrNothingOption;
import com.luigisgro.cqf.fdm.ExplicitMethodUncertainVolatility;
import com.luigisgro.cqf.fdm.FullGrid;
import com.luigisgro.cqf.fdm.Grid;
import com.luigisgro.cqf.fdm.FiniteDifferenceModel;
import com.luigisgro.cqf.fdm.Option.Direction;
import com.luigisgro.cqf.fdm.Portfolio;
import com.luigisgro.cqf.fdm.StaticHedgingStrategy;
import com.luigisgro.cqf.fdm.StaticHedgingStrategyOptimizationAdapter;
import com.luigisgro.cqf.fdm.VanillaOption;
import com.luigisgro.cqf.util.BlackScholes;


public class TestStrategyOptimizer {
	double tStep = 0.0002;
	double tMin = 0;
	double tMax = 1.0002;
	double sStep = 1;
	double sMin = 0;
	double sMax = 220;
	
	double strike = 100;
	double strikeLo = 90;
	double strikeHi = 110;
	double stock = 100;
	double maturity = 1;
	
	double volMin = 0.1;
	double volMax = 0.3;
	double r = 0.04;
	
	@Test
	public void testAllVanilla() throws IllegalArgumentException, MathException {
		Portfolio p = new Portfolio();
		p.getItems().put(new VanillaOption(Direction.CALL, strike, maturity), p.new Item(1, 0, false));
		p.getItems().put(new VanillaOption(Direction.CALL, strike, maturity), p.new Item(-1, BlackScholes.vanillaOptionValue(stock, strike, maturity, (volMin+volMax)/2, r, Direction.CALL), true));
		
		Grid g = new FullGrid(tStep, tMin, tMax, sStep, sMin, sMax);
		
		FiniteDifferenceModel method = new ExplicitMethodUncertainVolatility(volMin, volMax);
		
		StaticHedgingStrategy strategy = new StaticHedgingStrategy(g, p, method, r);
		StaticHedgingStrategyOptimizationAdapter adapter = new StaticHedgingStrategyOptimizationAdapter(strategy, stock);
		
		NelderMead optimizer = new NelderMead();
		
		RealPointValuePair result = optimizer.optimize(adapter, GoalType.MAXIMIZE, strategy.hedgePositions());

		System.out.println("Optimizer converged in " + optimizer.getIterations() + " iterations");
		System.out.println("Hedge: " + strategy.hedgePositionsDesc() + " Value: " + result.getValue());
		Assert.assertEquals(9.9250, result.getValue(), 0.0001);
		
		p.invertPositions();

		result = optimizer.optimize(adapter, GoalType.MAXIMIZE, strategy.hedgePositions());

		System.out.println("Optimizer converged in " + optimizer.getIterations() + " iterations");
		System.out.println("Hedge: " + strategy.hedgePositionsDesc() + " Value: " + result.getValue());
		Assert.assertEquals(-9.9250, result.getValue(), 0.0001);
	}
	@Test
	public void testAllVanillaDifferentMaturities() throws IllegalArgumentException, MathException {
		Portfolio p = new Portfolio();
		p.getItems().put(new VanillaOption(Direction.CALL, strike, maturity), p.new Item(1, 0, false));
		p.getItems().put(new VanillaOption(Direction.CALL, strikeLo, maturity), p.new Item(-0.5, BlackScholes.vanillaOptionValue(stock, strikeLo, maturity, (volMin+volMax)/2, r, Direction.CALL), true));
		p.getItems().put(new VanillaOption(Direction.CALL, strikeHi, maturity), p.new Item(-0.5, BlackScholes.vanillaOptionValue(stock, strikeHi, maturity, (volMin+volMax)/2, r, Direction.CALL), true));
		
		Grid g = new FullGrid(tStep, tMin, tMax, sStep, sMin, sMax);
		
		FiniteDifferenceModel method = new ExplicitMethodUncertainVolatility(volMin, volMax);
		
		StaticHedgingStrategy strategy = new StaticHedgingStrategy(g, p, method, r);
		StaticHedgingStrategyOptimizationAdapter adapter = new StaticHedgingStrategyOptimizationAdapter(strategy, stock);
		
		NelderMead optimizer = new NelderMead();
		
		RealPointValuePair result = optimizer.optimize(adapter, GoalType.MAXIMIZE, strategy.hedgePositions());

		System.out.println("Optimizer converged in " + optimizer.getIterations() + " iterations");
		System.out.println("Hedge: " + strategy.hedgePositionsDesc() + " Value: " + result.getValue());
		Assert.assertEquals(8.6434, result.getValue(), 0.0001);
		
		p.invertPositions();

		result = optimizer.optimize(adapter, GoalType.MAXIMIZE, strategy.hedgePositions());

		System.out.println("Optimizer converged in " + optimizer.getIterations() + " iterations");
		System.out.println("Hedge: " + strategy.hedgePositionsDesc() + " Value: " + result.getValue());
		Assert.assertEquals(-10.7151, result.getValue(), 0.0001);
	}
	@Test
	public void testBinaryAlone() throws Exception {
		Portfolio p = new Portfolio();
		p.getItems().put(new BinaryCashOrNothingOption(Direction.CALL, strike, maturity), p.new Item(1, 0, false));
		
		Grid g = new FullGrid(tStep, tMin, tMax, sStep, sMin, sMax);
		
		FiniteDifferenceModel method = new ExplicitMethodUncertainVolatility(volMin, volMax);
		
		StaticHedgingStrategy strategy = new StaticHedgingStrategy(g, p, method, r);
		
		strategy.runPricing();
		System.out.println("Hedge: " + strategy.hedgePositionsDesc() + " Value: " + strategy.getPnLAt(stock));
		Assert.assertEquals(0.2886, strategy.getPnLAt(stock), 0.0001);
		
		p.invertPositions();

		strategy.runPricing();
		System.out.println("HedgBinaryCashOrNothingOptione: " + strategy.hedgePositionsDesc() + " Value: " + strategy.getPnLAt(stock));
		Assert.assertEquals(-0.7845, strategy.getPnLAt(stock), 0.0001);
	}
	@Test
	public void testBinaryPlusHedge() throws IllegalArgumentException, MathException {
		Portfolio p = new Portfolio();
		p.getItems().put(new BinaryCashOrNothingOption(Direction.CALL, strike, maturity), p.new Item(1, 0, false));
		p.getItems().put(new VanillaOption(Direction.CALL, strikeLo, maturity), p.new Item(-0.05, BlackScholes.vanillaOptionValue(stock, strikeLo, maturity, (volMin+volMax)/2, r, Direction.CALL), true));
		p.getItems().put(new VanillaOption(Direction.CALL, strikeHi, maturity), p.new Item(0.05, BlackScholes.vanillaOptionValue(stock, strikeHi, maturity, (volMin+volMax)/2, r, Direction.CALL), true));
		
		Grid g = new FullGrid(tStep, tMin, tMax, sStep, sMin, sMax);
		
		FiniteDifferenceModel method = new ExplicitMethodUncertainVolatility(volMin, volMax);
		
		StaticHedgingStrategy strategy = new StaticHedgingStrategy(g, p, method, r);
		
		strategy.runPricing();
		System.out.println("No optimization. Hedge: " + strategy.hedgePositionsDesc() + " Value: " + strategy.getPnLAt(stock));
		Assert.assertEquals(0.3666, strategy.getPnLAt(stock), 0.0001);
		
		p.invertPositions();
		
		strategy.runPricing();
		System.out.println("No optimization. Hedge: " + strategy.hedgePositionsDesc() + " Value: " + strategy.getPnLAt(stock));
		Assert.assertEquals(-0.7112, strategy.getPnLAt(stock), 0.0001);
		
		p.invertPositions();
		
		StaticHedgingStrategyOptimizationAdapter adapter = new StaticHedgingStrategyOptimizationAdapter(strategy, stock);
		
		NelderMead optimizer = new NelderMead();
		
		RealPointValuePair result = optimizer.optimize(adapter, GoalType.MAXIMIZE, strategy.hedgePositions());

		System.out.println("Optimizer converged in " + optimizer.getIterations() + " iterations");
		System.out.println("Hedge: " + strategy.hedgePositionsDesc() + " Value: " + result.getValue());
		Assert.assertEquals(0.3962, result.getValue(), 0.0001);
		
		p.invertPositions();

		result = optimizer.optimize(adapter, GoalType.MAXIMIZE, strategy.hedgePositions());

		System.out.println("Optimizer converged in " + optimizer.getIterations() + " iterations");
		System.out.println("Hedge: " + strategy.hedgePositionsDesc() + " Value: " + result.getValue());
		Assert.assertEquals(-0.6834, result.getValue(), 0.0001);
	}
}
