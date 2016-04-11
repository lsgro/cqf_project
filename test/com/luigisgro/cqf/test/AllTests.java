package com.luigisgro.cqf.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	com.luigisgro.cqf.test.TestBoECurveTimeSeriesFileLoader.class,
	com.luigisgro.cqf.test.TestBS.class,
	com.luigisgro.cqf.test.TestDiscount.class,
	com.luigisgro.cqf.test.TestECBCurveTimeSeriesFileLoader.class,
	com.luigisgro.cqf.test.TestForwardInterestRate.class,
	com.luigisgro.cqf.test.TestJacobiDiagonalization.class,
	com.luigisgro.cqf.test.TestMatrix.class,
	com.luigisgro.cqf.test.TestMatrixBuffer.class,
	com.luigisgro.cqf.test.TestMonteCarloSimulation.class,
	com.luigisgro.cqf.test.TestMultiFactorHJM.class,
	com.luigisgro.cqf.test.TestOptimize.class,
	com.luigisgro.cqf.test.TestPolynomial.class,
	com.luigisgro.cqf.test.TestPolynomialApproximatedCurve.class,
	com.luigisgro.cqf.test.TestRegression.class,
	com.luigisgro.cqf.test.TestSeriesOfOptions.class,
	com.luigisgro.cqf.test.TestStrategyOptimizer.class,
	com.luigisgro.cqf.test.TestSymmetricMatrixBuffer.class,
	com.luigisgro.cqf.test.TestVanillas.class,
	com.luigisgro.cqf.test.TestVector.class,
	com.luigisgro.cqf.test.TestVectorBuffer.class,
	com.luigisgro.cqf.test.TestVolatilityFunction.class,
	com.luigisgro.cqf.test.TestXlet.class
})
public class AllTests {}
