package com.luigisgro.cqf.job;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveApproximatingPolynomial;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.CurveTimeSeriesOperator;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.TermStructure;
import com.luigisgro.cqf.curve.loaders.CurveTimeSeriesLoader;
import com.luigisgro.cqf.function.Polynomial;
import com.luigisgro.cqf.hjm.MultiFactorHJM;
import com.luigisgro.cqf.montecarlo.MonteCarloEvaluator;
import com.luigisgro.cqf.montecarlo.MonteCarloSimulation;
import com.luigisgro.cqf.montecarlo.RandomVectorGenerator;
import com.luigisgro.cqf.montecarlo.StandardErrorEvaluator;
import com.luigisgro.cqf.pca.PCA;

/**
 * This class implements a Monte Carlo simulation based on HJM/Musiela model
 * @author Luigi Sgro
 *
 */
public class HJMJob implements Job {
	/**
	 * Configuration object for this job
	 * It declares all the values that must be provided to run the job
	 * @author Luigi Sgro
	 *
	 */
	public static interface Configuration {
		/**
		 * @return The time step to be used for the simulation
		 */
		Double getTimeStep();
		/**
		 * @return The {@link com.luigisgro.cqf.montecarlo.MonteCarloEvaluator} object
		 * representing the calculations for the price of the derivative based on a
		 * Monte Carlo scenario 
		 */
		MonteCarloEvaluator<DayTimePoint> getEvaluator();
		/**
		 * @return The input file containing the historical curve time series
		 */
		File getInputFile();
		/**
		 * @return The input file loader
		 * @see com.luigisgro.cqf.curve.loaders
		 */
		CurveTimeSeriesLoader getInputDataLoader();
		/**
		 * @return The time step used in the historical curve time series
		 */
		Double getInputTimeStep();
		/**
		 * @return The optional output file for PCA results
		 */
		File getOutputFile();
		/**
		 * @return Field separator to be used for the output file (optional)
		 */
		String getOutputFieldSeparator();
		/**
		 * @return Minimum share of variance requested to PCA. It is used to determine the number
		 * of components to be used to drive the Monte Carlo simulation
		 */
		Double getMinPCAVariance();
		/**
		 * @return Degree of the polynomials used to build the approximate volatility continuous curves
		 */
		Integer getPCAVariancePolyDegree();
		/**
		 * @return The class of the stochastic number generator for Monte Carlo simulation
		 * @see com.luigisgro.cqf.montecarlo
		 */
		Class<? extends RandomVectorGenerator> getStochasticGeneratorClass();
		/**
		 * @return An array of {@link com.luigisgro.cqf.curve.DayTimePoint}. It defines
		 * the calendar of the scenario in the Monte Carlo simulation
		 * @see com.luigisgro.cqf.montecarlo
		 */
		DayTimePoint[] getScenarioCalendar();
		/**
		 * @return The derivative to be priced
		 * @see com.luigisgro.cqf.hjm
		 */
		CurveTimeSeriesOperator<DayTimePoint> getDerivative();
	}

	private Configuration jobConfiguration;
	
	private Map<String, Object> results;

	public HJMJob(Configuration jobConfiguration) {
		this.jobConfiguration = jobConfiguration;
	}
	
	private void exportEigenvectorsToFile(PCA<DayTimePoint> pca) {
		File outputFile = jobConfiguration.getOutputFile();
		String fieldSeparator = jobConfiguration.getOutputFieldSeparator();
		
		if (outputFile != null) {
			if (fieldSeparator == null) {
				fieldSeparator = ";";
			}
			System.out.println("\nExporting eigenvectors to file: " + outputFile.getName());
			try {
				FileWriter writer = new FileWriter(outputFile);
				writer.write("eigenvectors\n");
				pca.getComponentEigenvectors().writeLinesToCSV(writer, fieldSeparator);
				System.out.println("Exporting eigenvalues to file: " + outputFile.getName());
				writer.write("eigenvalues\n");
				pca.getComponentEigenvalues().writeLinesToCSV(writer, fieldSeparator);
				writer.close();			
			} catch (IOException e) {
				System.err.println("An error occurred while writing to file: " + outputFile.getName() + "[" + e.getMessage() + "]");
			}
		}
	}
	
	public boolean execute() {
		// Load variables from configuration
		double timeStep = jobConfiguration.getTimeStep();
		File inputFile = jobConfiguration.getInputFile();
		CurveTimeSeriesLoader inputDataLoader = jobConfiguration.getInputDataLoader();
		double inputTimeStep = jobConfiguration.getInputTimeStep();
		double pcaVarianceRatio = jobConfiguration.getMinPCAVariance();
		int polyDegree = jobConfiguration.getPCAVariancePolyDegree();
		Class< ? extends RandomVectorGenerator> stochasticGeneratorClass = jobConfiguration.getStochasticGeneratorClass();
		MonteCarloEvaluator<DayTimePoint> evaluator = jobConfiguration.getEvaluator();
		DayTimePoint[] scenarioCalendar = jobConfiguration.getScenarioCalendar();
		
		// Create a new results map
		results = new HashMap<String, Object>();
		
		// Input data loading
		System.out.println("\nImport forward curve data from: " + inputFile.getName());
		CurveTimeSeries<DayTimePoint> curveTimeSeries;
		try {
			curveTimeSeries = inputDataLoader.load(inputFile, inputTimeStep);
		} catch (IOException e) {
			System.err.println("An error occurred while loading file: " + inputFile + "[" + e.getMessage() + "]");
			return false;
		}
		TermStructure termStructure = curveTimeSeries.getTermStructure();
		SortedMap<DayTimePoint, Curve> curves = curveTimeSeries.getCurves();
		System.out.println("Loaded " + curves.size() + " records [" + curves.firstKey() + " to " + curves.lastKey() + "]");

		// PCA
		System.out.println("\nExecuting PCA...");
		long start = System.currentTimeMillis();
		PCA<DayTimePoint> pca = new PCA<DayTimePoint>(curveTimeSeries);
		int iterations = pca.process();	
		System.out.println("Finished in: " + iterations + " iterations. Time elapsed: " + (System.currentTimeMillis() - start) + "ms");
		
		System.out.println("\nSelecting first components accounting for at least " + pcaVarianceRatio * 100 + "% of total variance..");
		int numOfComponents;
		double cumulativeRatioOfVariance = 0;
		for (numOfComponents = 1; numOfComponents <= pca.getComponentEigenvalues().dimension(); numOfComponents++) {
			cumulativeRatioOfVariance = pca.cumulativeRatioOfVariance(numOfComponents);
			System.out.println("" + numOfComponents + " -> " + (cumulativeRatioOfVariance * 100) + " variance");
			// Store ration of variance in results map
			results.put("pcaRatioOfVariance", cumulativeRatioOfVariance);
			if (cumulativeRatioOfVariance > pcaVarianceRatio)
				break;
		}
		System.out.println("Selected " + numOfComponents + " components.");
		
		// Store result in results map
		results.put("pcaNumberOfComponents", numOfComponents);

		// Export of PCA result
		exportEigenvectorsToFile(pca);

		// Polynomial fitting of volatility functions
		System.out.println("\nApproximating volatility functions with polynomials of degree: " + polyDegree);
		Polynomial[] volatilityFunctions = new Polynomial[numOfComponents];
		for (int i = 0; i < numOfComponents; i++) {
			Polynomial normalizedVolatility = new CurveApproximatingPolynomial(pca.getComponentEigenvectors().getColumn(i).toDoubleArray(), termStructure, polyDegree);
			// volatility function: a polynomial approximation of the eigenvector multiplied by the square root of the corresponding eigenvalue
			volatilityFunctions[i] = Polynomial.scale(normalizedVolatility, Math.sqrt(pca.getComponentEigenvalues().get(i)));
			System.out.print("" + (i + 1) + " ->");
			for (int degree = polyDegree; degree >= 0; degree--) {
				System.out.print(" x^" + degree + ": " + normalizedVolatility.getCoefficients()[degree]);
			}
			System.out.println();
		}
		
		// Creating base scenario for Monte Carlo simulation
		Date baseDate = curveTimeSeries.getCurves().lastKey().getTime();
		Curve baseScenario = curveTimeSeries.getCurves().get(new DayTimePoint(baseDate));
				
		// Create model for the generation of scenarios
		MultiFactorHJM hjm = new MultiFactorHJM(volatilityFunctions, termStructure);

		// Create the stochastic generator
		Constructor<? extends RandomVectorGenerator> constructor;
		RandomVectorGenerator stochasticGenerator;
		try {
			constructor = stochasticGeneratorClass.getDeclaredConstructor(Integer.class);
			stochasticGenerator = constructor.newInstance(numOfComponents);
		} catch (Exception e) {
			System.err.println("An error occurred while trying to istantiate random generator class [" + e.getMessage() + "]");
			return false;
		}
		
		// Create the Monte Carlo simulation infrastructure
		MonteCarloSimulation<DayTimePoint> mc = new MonteCarloSimulation<DayTimePoint>(hjm, evaluator, stochasticGenerator, baseScenario, scenarioCalendar, timeStep);

		// Perform Monte Carlo simulation
		System.out.println("\nExecuting Monte Carlo simulation...");
		start = System.currentTimeMillis();
		int numberOfSimulations = mc.simulate();
		System.out.println("Finished in " + numberOfSimulations + " iterations. Time elapsed: " + (System.currentTimeMillis() - start) + "ms");
		
		// Presentation of results
		if (evaluator instanceof StandardErrorEvaluator) {
			System.out.println("Standard error estimate: " + Math.sqrt(((StandardErrorEvaluator<DayTimePoint>)evaluator).squareStdErr()));
		}
		System.out.println("\nDerivative value: " + evaluator.getResult());	
		
		// Store result in results map
		results.put("derivativeValue", evaluator.getResult());
		results.put("montecarloIterations", numberOfSimulations);

		return true;
	}

	@Override
	public Map<String, Object> getResults() {
		return results;
	}
}
