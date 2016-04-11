package com.luigisgro.cqf.test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.loaders.BoECurveTimeSeriesFileLoader;
import com.luigisgro.cqf.curve.loaders.CurveTimeSeriesLoader;


public class TestBoECurveTimeSeriesFileLoader {
	@Test
	public void testSplitEmptyLine() throws IOException, ParseException {
		String empty = "25 Mar 05;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;";
		Assert.assertEquals(1, empty.split(";").length); // a bug?
	}
	
	@Test
	public void testLoadFile() throws IOException, ParseException {
		CurveTimeSeriesLoader loader = new BoECurveTimeSeriesFileLoader();
		CurveTimeSeries<DayTimePoint> curveTimeSeries = loader.load(new File("data/UKBLC05.csv"), 1.0/252);
		Assert.assertEquals(51, curveTimeSeries.getTermStructure().getTenors().length);
		Assert.assertEquals(1642, curveTimeSeries.getCurves().size());
	}
}
