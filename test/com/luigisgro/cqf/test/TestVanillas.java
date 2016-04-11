package com.luigisgro.cqf.test;

import org.apache.commons.math.MathException;
import org.junit.Assert;
import org.junit.Test;

import com.luigisgro.cqf.fdm.ExplicitMethodConstantVolatility;
import com.luigisgro.cqf.fdm.FiniteDifferencePricer;
import com.luigisgro.cqf.fdm.Grid;
import com.luigisgro.cqf.fdm.FiniteDifferenceModel;
import com.luigisgro.cqf.fdm.Option.Direction;
import com.luigisgro.cqf.fdm.Portfolio;
import com.luigisgro.cqf.fdm.TwoStepsGrid;
import com.luigisgro.cqf.fdm.VanillaOption;
import com.luigisgro.cqf.util.BlackScholes;


public class TestVanillas {
	double tMin = 0;
	double sStep = 1;
	double sMin = 0;
	double sMax = 220;

	double volMin = 0.18;
	double volMax = 0.22;
	double r = 0.04;
	double maturity = 1;
	double position = 1;
	double tStep = 0.00025; //sStep * sStep / volMin / volMin / (4 * strike * strike);

	double tMax = 1 + tStep;

	@Test
	public void testVanilla1() throws MathException {
		Direction direction = Direction.CALL;	
		double strike = 100;
		double stock = 100;

		Portfolio portfolio = new Portfolio(); 
		portfolio.setPosition(new VanillaOption(direction, strike, maturity), position);
		
		Grid g = new TwoStepsGrid(tStep, tMin, tMax, sStep, sMin, sMax);

		if (!g.validate(volMax, portfolio)) {
			throw new IllegalArgumentException();
		}

		FiniteDifferenceModel method = new ExplicitMethodConstantVolatility(volMax);
	
		FiniteDifferencePricer.evaluate(g, portfolio, method, r, false, null);
		
		double v = g.getPresentInterpolated(stock);
		double bsv = BlackScholes.vanillaOptionValue(stock, strike, maturity, volMax, r, direction);
		System.out.println("Direction: " + direction.name() + ", strike: " + strike + ", Black-Scholes: " + bsv + ", FDM: " + v);
		
		Assert.assertEquals(bsv, v, 0.005);
	}
	@Test
	public void testVanilla2() throws MathException {
		Direction direction = Direction.CALL;	
		double strike = 80;
		double stock = 100;

		Portfolio portfolio = new Portfolio(); 
		portfolio.setPosition(new VanillaOption(direction, strike, maturity), position);
		
		Grid g = new TwoStepsGrid(tStep, tMin, tMax, sStep, sMin, sMax);

		if (!g.validate(volMax, portfolio)) {
			throw new IllegalArgumentException();
		}

		FiniteDifferenceModel method = new ExplicitMethodConstantVolatility(volMax);
	
		FiniteDifferencePricer.evaluate(g, portfolio, method, r, false, null);
		
		double v = g.getPresentInterpolated(stock);
		double bsv = BlackScholes.vanillaOptionValue(stock, strike, maturity, volMax, r, direction);
		System.out.println("Direction: " + direction.name() + ", strike: " + strike + ", Black-Scholes: " + bsv + ", FDM: " + v);
		
		Assert.assertEquals(bsv, v, 0.005);
	}

	@Test
	public void testVanilla3() throws MathException {
		Direction direction = Direction.PUT;	
		double strike = 100;
		double stock = 90;

		Portfolio portfolio = new Portfolio(); 
		portfolio.setPosition(new VanillaOption(direction, strike, maturity), position);
		
		Grid g = new TwoStepsGrid(tStep, tMin, tMax, sStep, sMin, sMax);

		if (!g.validate(volMax, portfolio)) {
			throw new IllegalArgumentException();
		}

		FiniteDifferenceModel method = new ExplicitMethodConstantVolatility(volMax);
	
		FiniteDifferencePricer.evaluate(g, portfolio, method, r, false, null);
		
		double v = g.getPresentInterpolated(stock);
		double bsv = BlackScholes.vanillaOptionValue(stock, strike, maturity, volMax, r, direction);
		System.out.println("Direction: " + direction.name() + ", strike: " + strike + ", Black-Scholes: " + bsv + ", FDM: " + v);
		
		Assert.assertEquals(bsv, v, 0.005);
	}

	@Test
	public void testVanilla4() throws MathException {
		Direction direction = Direction.PUT;	
		double strike = 110;
		double stock = 100;

		Portfolio portfolio = new Portfolio(); 
		portfolio.setPosition(new VanillaOption(direction, strike, maturity), position);
		
		Grid g = new TwoStepsGrid(tStep, tMin, tMax, sStep, sMin, sMax);

		if (!g.validate(volMax, portfolio)) {
			throw new IllegalArgumentException();
		}

		FiniteDifferenceModel method = new ExplicitMethodConstantVolatility(volMax);
	
		FiniteDifferencePricer.evaluate(g, portfolio, method, r, false, null);
		
		double v = g.getPresentInterpolated(stock);
		double bsv = BlackScholes.vanillaOptionValue(stock, strike, maturity, volMax, r, direction);
		System.out.println("Direction: " + direction.name() + ", strike: " + strike + ", Black-Scholes: " + bsv + ", FDM: " + v);
		
		Assert.assertEquals(bsv, v, 0.005);
	}

}
