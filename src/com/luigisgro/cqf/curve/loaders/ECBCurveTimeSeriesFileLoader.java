package com.luigisgro.cqf.curve.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.TermStructure;

//http://www.ecb.int/stats/money/yc/data/fmd/download/yc_historical.zip

/**
 * Loads forward interest rate historical curves in the daily format from European Central Bank
 * The updated file can be loaded from
 * <a href="http://www.ecb.int/stats/money/yc/data/fmd/download/yc_historical.zip">Historical Yield Curve</a>
 */
public class ECBCurveTimeSeriesFileLoader implements CurveTimeSeriesLoader {
	public static final String FIELD_SEPARATOR = ",";
	public static final String TENOR_CODE_PREFIX = "YC.B.U2.EUR.4F.G_N_A.SV_C_YM.IF_";
	public static final String DATE_POINT_FORMAT = "dd-MMM-yyyy";
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
		if (!code.startsWith(TENOR_CODE_PREFIX))
			throw new IllegalArgumentException("Unrecognised tenor code: " + code);
		String tenor = code.replace(TENOR_CODE_PREFIX, "");
		Pattern pattern = Pattern.compile("(([0-9]+)Y)?(([0-9]+)M)?");
		Matcher matcher = pattern.matcher(tenor);
		int months = 0, years = 0;
		if (matcher.matches()) {
			if (matcher.group(2) != null) {
				years = Integer.valueOf(matcher.group(2));
			}
			if (matcher.group(4) != null) {
				months = Integer.valueOf(matcher.group(4));
			}
		}
		return (double)(years * 360 + months * 30) / 360;
	}

	public CurveTimeSeries<DayTimePoint> load(File inputFile, double timeStep) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		
		String line;

		// skip lines
		do {
			line = reader.readLine();
		} while (!line.startsWith("Name:"));
		
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
			for (int i = 1; i < tenors.length; i++) {
				if ("NA".equals(tokens[i].trim())) {
					line = reader.readLine();
					tokens = line.split(FIELD_SEPARATOR);
					continue POINT_DATE;
				}
				points[i - 1] = Double.valueOf(tokens[i]) / 100;
			}
			curveTimeSeries.put(new DayTimePoint(time), new Curve(points, termStructure));
			
			line = reader.readLine();
			tokens = line.split(FIELD_SEPARATOR);
		} while (tokens.length - 1 == tenors.length && parsePointDate(tokens[0]) != null);
		
		return curveTimeSeries;
	}	
}
