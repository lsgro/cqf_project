package com.luigisgro.cqf.job;

import static com.luigisgro.cqf.util.PropertiesAdapter.Type.CLASS;
import static com.luigisgro.cqf.util.PropertiesAdapter.Type.DATE;
import static com.luigisgro.cqf.util.PropertiesAdapter.Type.FILE;
import static com.luigisgro.cqf.util.PropertiesAdapter.Type.INT;
import static com.luigisgro.cqf.util.PropertiesAdapter.Type.REAL;
import static com.luigisgro.cqf.util.PropertiesAdapter.Type.STRING;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.luigisgro.cqf.curve.CurveTimeSeriesOperator;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.loaders.CurveTimeSeriesLoader;
import com.luigisgro.cqf.hjm.Cap;
import com.luigisgro.cqf.hjm.Floor;
import com.luigisgro.cqf.hjm.ForwardInterestRate;
import com.luigisgro.cqf.hjm.ZCB;
import com.luigisgro.cqf.montecarlo.CurveTimeSeriesOperatorEvaluator;
import com.luigisgro.cqf.montecarlo.MonteCarloEvaluator;
import com.luigisgro.cqf.montecarlo.RandomVectorGenerator;
import com.luigisgro.cqf.montecarlo.StandardErrorEvaluator;
import com.luigisgro.cqf.util.PropertiesAdapter;
import com.luigisgro.cqf.util.PropertyLoader;

/**
 * Implementation of {@link HJMJob.Configuration} that can read a configuration
 * from {@link java.util.Properties} object
 * @author Luigi Sgro
 *
 */
public class PropertiesHJMJobConfiguration implements HJMJob.Configuration, PropertyLoader {
	private PropertiesAdapter adapter;
	
	private static final String INPUT_FILE_KEY = "hjm.input.file";
	private static final String INPUT_LOADER_CLASS_KEY = "hjm.input.loader.class";
	private static final String INPUT_TIMESTEP_KEY = "hjm.input.timestep";
	
	private static final String PCA_VARIANCE_THRESHOLD_KEY = "hjm.pca.variance.threshold";
	private static final String PCA_EIGENVECTORS_OUTPUT_FILE_KEY = "hjm.pca.eigenvectors.output.file";
	private static final String PCA_EIGENVECTORS_OUTPUT_FILE_SEPARATOR_KEY = "hjm.pca.eigenvectors.output.file.separator";

	private static final String HJM_VOLATILITY_POLYNOMIAL_DEGREE_KEY = "hjm.volatility.polynomial.degree";
	private static final String HJM_DERIVATIVE_TYPE_KEY = "hjm.derivative.type";
	private static final String HJM_DERIVATIVE_STRIKE_KEY = "hjm.derivative.strike";
	private static final String HJM_DERIVATIVE_TENOR_KEY = "hjm.derivative.tenor";
	private static final String HJM_DERIVATIVE_MATURITY_KEY = "hjm.derivative.maturity";

	private static final String MC_RANDOM_GENERATOR_CLASS_KEY = "hjm.mc.random.generator.class";
	private static final String MC_CALENDAR_MONTHS_KEY = "hjm.mc.calendar.months";
	private static final String MC_CALENDAR_START_DATE_KEY = "hjm.mc.calendar.start";
	private static final String MC_EVALUATOR_TYPE_KEY = "hjm.mc.evaluator.type";
	private static final String MC_EVALUATOR_STDERR_KEY = "hjm.mc.evaluator.stderr";
	private static final String MC_ITERATIONS_KEY = "hjm.mc.iterations.max";
	private static final String MC_TIMESTEP_KEY = "hjm.mc.timestep";
	
	private static final Object[][] parameterConfiguration = {
		{ INPUT_FILE_KEY, FILE, true },
		{ INPUT_LOADER_CLASS_KEY, CLASS, true },
		{ INPUT_TIMESTEP_KEY, REAL, true },
		{ PCA_VARIANCE_THRESHOLD_KEY, REAL, true },
		{ PCA_EIGENVECTORS_OUTPUT_FILE_KEY, FILE, false },
		{ PCA_EIGENVECTORS_OUTPUT_FILE_SEPARATOR_KEY, STRING, false },
		{ HJM_VOLATILITY_POLYNOMIAL_DEGREE_KEY, INT, true },
		{ HJM_DERIVATIVE_TYPE_KEY, STRING, true },
		{ HJM_DERIVATIVE_STRIKE_KEY, REAL, false },
		{ HJM_DERIVATIVE_TENOR_KEY, REAL, false },
		{ HJM_DERIVATIVE_MATURITY_KEY, REAL, true },
		{ MC_RANDOM_GENERATOR_CLASS_KEY, CLASS, true },
		{ MC_CALENDAR_START_DATE_KEY, DATE, false },
		{ MC_CALENDAR_MONTHS_KEY, INT, true },
		{ MC_EVALUATOR_TYPE_KEY, STRING, true },
		{ MC_EVALUATOR_STDERR_KEY, REAL, false },
		{ MC_ITERATIONS_KEY, INT, true },
		{ MC_TIMESTEP_KEY, REAL, true }
	};
	
	private File inputFile;
	private double inputTimeStep;
	private CurveTimeSeriesLoader inputDataLoader;
	private int numberOfScenarios;
	private double strike;
	private double tenor;
	private double maturity;
	private double timeStep;
	private File outputFile;
	private String outputFieldSeparator;
	private double minVariance;
	private int polyDegree;
	private Class<RandomVectorGenerator> stochasticGeneratorClass;
	private MonteCarloEvaluator<DayTimePoint> evaluator;
	private DayTimePoint[] scenarioCalendar;
	private CurveTimeSeriesOperator<DayTimePoint> derivative;

	@SuppressWarnings("unchecked")
	@Override
	public boolean loadProperties(Map<Object, Object> properties) {
		adapter = new PropertiesAdapter(properties);	
		boolean success = adapter.loadParameters(parameterConfiguration);
		if (!success) {
			System.err.println(adapter.getMessage());
			return false;
		}
		
		Class<CurveTimeSeriesLoader> loaderClass;
		try {
			loaderClass = (Class<CurveTimeSeriesLoader>) adapter.getParameters().get(INPUT_LOADER_CLASS_KEY);
			inputDataLoader = loaderClass.newInstance();
		} catch (Exception e) {
			System.err.println("An error occurred while trying to istantiate loader class [" + e.getMessage() + "]");
			return false;
		}
		System.out.println("Loader class: " + loaderClass.getName());
		
		inputFile = (File)adapter.getParameters().get(INPUT_FILE_KEY);
		try {
			System.out.println("Input file name: " + inputFile.getCanonicalPath());
		} catch (IOException e) {
			System.err.println("Problem with file: " + inputFile.getName() + "[" + e.getMessage() + "]");
			return false;
		}
		
		inputTimeStep = (Double)adapter.getParameters().get(INPUT_TIMESTEP_KEY);
		System.out.println("Input time step: " + inputTimeStep);

		maturity = (Double)adapter.getParameters().get(HJM_DERIVATIVE_MATURITY_KEY);
		System.out.println("Maturity: " + maturity);
		
		timeStep = (Double)adapter.getParameters().get(MC_TIMESTEP_KEY);
		System.out.println("TimeStep: " + timeStep);
		
		minVariance = (Double)adapter.getParameters().get(PCA_VARIANCE_THRESHOLD_KEY);
		System.out.println("Min PCA variance: " + minVariance * 100 + "%");
		
		polyDegree = (Integer)adapter.getParameters().get(HJM_VOLATILITY_POLYNOMIAL_DEGREE_KEY);
		System.out.println("PCA volatility polynomial degree: " + polyDegree);

		outputFile = (File)adapter.getParameters().get(PCA_EIGENVECTORS_OUTPUT_FILE_KEY);
		outputFieldSeparator = (String)adapter.getParameters().get(PCA_EIGENVECTORS_OUTPUT_FILE_SEPARATOR_KEY);
				
		int calendarMonths = (Integer)adapter.getParameters().get(MC_CALENDAR_MONTHS_KEY);
		System.out.println("Length of Monte Carlo scenarios: " + calendarMonths + " months");

		Date calendarStartDate = (Date)adapter.getParameters().get(MC_CALENDAR_START_DATE_KEY);
		if (calendarStartDate == null)
			calendarStartDate = new Date();
		System.out.println("Start of Monte Carlo scenarios: " + calendarStartDate);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(calendarStartDate);
		endCalendar.add(Calendar.MONTH, calendarMonths);
		scenarioCalendar = DayTimePoint.createDayTimePointCalendar(calendarStartDate, endCalendar.getTime());

		String derivativeType = (String)adapter.getParameters().get(HJM_DERIVATIVE_TYPE_KEY);
		System.out.println("Derivative: " + derivativeType);
		
		if ("cap".equals(derivativeType) || "floor".equals(derivativeType)) {
			strike = (Double)adapter.getParameters().get(HJM_DERIVATIVE_STRIKE_KEY);
			System.out.println("Strike: " + strike);
		}
		
		if (!"zcb".equals(derivativeType)) {
			tenor = (Double)adapter.getParameters().get(HJM_DERIVATIVE_TENOR_KEY);
			System.out.println("Tenor: " + tenor);
		}

		CurveTimeSeriesOperator<DayTimePoint> derivative = null;
		if ("cap".equals(derivativeType)) {
			derivative = new Cap<DayTimePoint>(strike, tenor, maturity, scenarioCalendar, timeStep);
		} else if ("floor".equals(derivativeType)) {
			derivative = new Floor<DayTimePoint>(strike, tenor, maturity, scenarioCalendar, timeStep);
		} else if ("zcb".equals(derivativeType)) {
			int cashflowTimeIndex = (int)(maturity / timeStep);
			derivative = new ZCB<DayTimePoint>(scenarioCalendar[0], scenarioCalendar[cashflowTimeIndex], timeStep);
		} else if ("fwd".equals(derivativeType)) {
			int cashflowTimeIndex = (int)(maturity / timeStep);
			derivative = new ForwardInterestRate<DayTimePoint>(scenarioCalendar[cashflowTimeIndex], tenor);
		} else {
			System.err.println("Unknown derivative type: " + derivativeType);
			return false;
		}
		
		String evaluatorType = (String)adapter.getParameters().get(MC_EVALUATOR_TYPE_KEY);
		
		double maxStdError = 0;
		if ("stderr".equals(evaluatorType)) {
			maxStdError = (Double)adapter.getParameters().get(MC_EVALUATOR_STDERR_KEY);
		}
		
		numberOfScenarios = (Integer)adapter.getParameters().get(MC_ITERATIONS_KEY);
		if ("stderr".equals(evaluatorType)) {
			evaluator = new StandardErrorEvaluator<DayTimePoint>(derivative, maxStdError, numberOfScenarios);
			System.out.println("Standard error evaluator - max error: " + maxStdError + ", max number of scenarios: " + numberOfScenarios) ;
		} else {
			evaluator = new CurveTimeSeriesOperatorEvaluator<DayTimePoint>(derivative, numberOfScenarios);
			System.out.println("Fixed scenarios evaluator - max number of scenarios: " + numberOfScenarios) ;
		}
		
		stochasticGeneratorClass = (Class<RandomVectorGenerator>)adapter.getParameters().get(MC_RANDOM_GENERATOR_CLASS_KEY);
		System.out.println("Stochastic generator class: " + stochasticGeneratorClass.getName());
		
		return true;
	}
	
	@Override
	public Double getTimeStep() {
		return timeStep;
	}

	@Override
	public MonteCarloEvaluator<DayTimePoint> getEvaluator() {
		return evaluator;
	}

	@Override
	public File getInputFile() {
		return inputFile;
	}

	@Override
	public CurveTimeSeriesLoader getInputDataLoader() {
		return inputDataLoader;
	}

	@Override
	public Double getInputTimeStep() {
		return inputTimeStep;
	}

	@Override
	public File getOutputFile() {
		return outputFile;
	}

	@Override
	public String getOutputFieldSeparator() {
		return outputFieldSeparator;
	}

	@Override
	public Double getMinPCAVariance() {
		return minVariance;
	}

	@Override
	public Integer getPCAVariancePolyDegree() {
		return polyDegree;
	}

	@Override
	public DayTimePoint[] getScenarioCalendar() {
		return scenarioCalendar;
	}

	@Override
	public Class<? extends RandomVectorGenerator> getStochasticGeneratorClass() {
		return stochasticGeneratorClass;
	}

	@Override
	public CurveTimeSeriesOperator<DayTimePoint> getDerivative() {
		return derivative;
	}
}
