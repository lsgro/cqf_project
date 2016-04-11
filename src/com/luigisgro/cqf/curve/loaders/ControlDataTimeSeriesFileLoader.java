package com.luigisgro.cqf.curve.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.TermStructure;

// CQF control data
/**
 * Loads forward rate curve historical data from a control sample from a CQF lecture
 */
public class ControlDataTimeSeriesFileLoader implements CurveTimeSeriesLoader {
	public static final String FIELD_SEPARATOR = ";";
	public static final String DATE_POINT_FORMAT = "dd MMM yy";
	private TermStructure termStructure;
	private Calendar cal = Calendar.getInstance();

	private Date parsePointDate(String date) {
		cal.setTime(new Date());
		cal.add(Calendar.DATE, Integer.parseInt(date));
		return cal.getTime();
	}

	public double parseTenor(String code) {
		return Double.parseDouble(code);
	}

	public CurveTimeSeries<DayTimePoint> load(File inputFile, double timeStep) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		
		String line;

		// skip lines until header is found
		do {
			line = reader.readLine();
		} while ("".equals(line.trim()) || !line.startsWith("\"years:\""));
		
		// process tenors
		String[] tokens = line.split(FIELD_SEPARATOR);
		double[] tenors = new double[tokens.length - 1];
		for (int i = 1; i < tokens.length; i++) {
			tenors[i - 1] = parseTenor(tokens[i].trim());
		}
		termStructure = new TermStructure(tenors);
		
		// skip lines
		do {
			line = reader.readLine();
			tokens = line.split(FIELD_SEPARATOR);
		} while (tokens.length - 1 != tenors.length || parsePointDate(tokens[0]) == null);
	
		// process point data
		CurveTimeSeries<DayTimePoint> curveTimeSeries = new CurveTimeSeries<DayTimePoint>(termStructure, timeStep);
		POINT_DATE:
		do {
			Date time = parsePointDate(tokens[0]);
			double[] points = new double[tenors.length];
			for (int i = 1; i <= tenors.length; i++) {
				if (tokens.length < tenors.length || "".equals(tokens[i].trim())) {
					line = reader.readLine();
					tokens = line.split(FIELD_SEPARATOR);
					continue POINT_DATE;
				}
				points[i - 1] = Double.valueOf(tokens[i]) / 100;
			}
			curveTimeSeries.put(new DayTimePoint(time), new Curve(points, termStructure));
			
			line = reader.readLine();
			if (line == null)
				break;
			
			tokens = line.split(FIELD_SEPARATOR);
		} while (parsePointDate(tokens[0]) != null);
		
		return curveTimeSeries;
	}	
}
