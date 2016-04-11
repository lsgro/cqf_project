package com.luigisgro.cqf.montecarlo;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.TimePoint;

/**
 * Skeleton of a Monte Carlo simulation. It implements the logic for creating scenarios based
 * on a {@link MultiFactorModel} and running iteratively until the {@link MonteCarloEvaluator}
 * provided doesn't stop the process
 * @author Luigi Sgro
 *
 * @param <T>
 */
public class MonteCarloSimulation<T extends TimePoint> {
	private double timeStep;
	private int numberOfSteps;
	private Curve baseScenario;
	private T[] pointDates;
	private RandomVectorGenerator stochasticVectorGenerator;
	private MultiFactorModel multiFactorModel;
	private MonteCarloEvaluator<T> evaluator;
	
	/**
	 * Creates a new Monte Carlo simulation framework
	 * @param multiFactorModel The model that provides all the calculation to generate scenarios
	 * @param evaluator The object responsible for evaluating the derivative value and stop the simulation
	 * @param randomVectorGenerator A generator to feed the model with suitably distributed vectors
	 * @param baseScenario The starting point for the simulation
	 * @param pointDates The points that define the time horizon of the simulation
	 * @param timeStep The time step (fraction of year) to be used in the simulation
	 */
	public MonteCarloSimulation(MultiFactorModel multiFactorModel,
			MonteCarloEvaluator<T> evaluator,
			RandomVectorGenerator randomVectorGenerator,
			Curve baseScenario,
			T[] pointDates,
			double timeStep) {
		this.multiFactorModel = multiFactorModel;
		this.stochasticVectorGenerator = randomVectorGenerator;
		this.pointDates = pointDates;
		numberOfSteps = pointDates.length - 1;
		this.timeStep = timeStep;
		this.baseScenario = baseScenario;
		this.evaluator = evaluator;
	}
	
	private CurveTimeSeries<T> createScenario() {
		CurveTimeSeries<T> scenario = new CurveTimeSeries<T>(baseScenario.getTermStructure(), timeStep);
		scenario.put(pointDates[0], baseScenario);
		Curve currentCurve = baseScenario;
		for (int step = 1; step <= numberOfSteps; step++) {
			double[] randomVector = stochasticVectorGenerator.generateNextVector();
			currentCurve = multiFactorModel.nextCurve(currentCurve, randomVector, timeStep);
			scenario.put(pointDates[step], currentCurve);
		}
		return scenario;
	}
	
	/**
	 * This method executes a complete Monte Carlo simulation. It runs up to a maximum number of
	 * iterations by creating a new scenario, feeding it to the evaluator, and letting the evaluator
	 * determine if the simulation can stop
	 * @return The number of completed iterations
	 */
	public int simulate() {
		int numOfIterations = 0;
		while (evaluator.moreIterationsNeeded()) {
			CurveTimeSeries<T> scenario = createScenario();
			evaluator.evaluateAndAccumulate(scenario);
			if ((numOfIterations + 1) % 1000 == 0) {
				evaluator.printProgress();
			}
			numOfIterations++;
		}
		return numOfIterations;
	}
}
