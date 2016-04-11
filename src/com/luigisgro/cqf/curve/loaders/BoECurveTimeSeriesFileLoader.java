package com.luigisgro.cqf.curve.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.TermStructure;

// http://www.bankofengland.co.uk/statistics/yieldcurve/index.htm

/**
 * Loads forward interest rate historical curves in the daily format from Bank Of England
 * More information at <a href="http://www.bankofengland.co.uk/statistics/yieldcurve/index.htm">
 * Bank Of England</a>
 */
public class BoECurveTimeSeriesFileLoader implements CurveTimeSeriesLoader {
	public static final String FIELD_SEPARATOR = ";";
	public static final String DATE_POINT_FORMAT = "dd MMM yy";
	private TermStructure termStructure;
	private SimpleDateFormat sdf = new SimpleDateFormat(DATE_POINT_FORMAT);

	private Date parsePointDate(String date) {
		Date pointDate = null;
		try {
			pointDate = sdf.parse(date);
		} catch (ParseException e) {}
		return pointDate;
	}

	public double parseTenor(String code) {
		return Double.parseDouble(code);
	}

	/* (non-Javadoc)
	 * @see com.luigisgro.cqf.curve.loaders.CurveTimeSeriesLoader#load()
	 */
	@Override
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
