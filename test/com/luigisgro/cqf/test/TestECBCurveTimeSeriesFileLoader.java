package com.luigisgro.cqf.test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Test;

import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.DayTimePoint;
import com.luigisgro.cqf.curve.loaders.ECBCurveTimeSeriesFileLoader;


public class TestECBCurveTimeSeriesFileLoader {
	@Test
	public void testParseTenorWithYearAndMonth1() {
		ECBCurveTimeSeriesFileLoader loader = new ECBCurveTimeSeriesFileLoader();
		double tenor = loader.parseTenor("YC.B.U2.EUR.4F.G_N_A.SV_C_YM.IF_9Y7M");
		Assert.assertEquals((double)(9 * 360 + 7 * 30) / 360, tenor, 0.0001);
	}
	@Test
	public void testParseTenorWithYearAndMonth2() {
		ECBCurveTimeSeriesFileLoader loader = new ECBCurveTimeSeriesFileLoader();
		double tenor = loader.parseTenor("YC.B.U2.EUR.4F.G_N_A.SV_C_YM.IF_14Y11M");
		Assert.assertEquals((double)(14 * 360 + 11 * 30) / 360, tenor, 0.0001);
	}
	@Test
	public void testParseTenorWithMonth() {
		ECBCurveTimeSeriesFileLoader loader = new ECBCurveTimeSeriesFileLoader();
		double tenor = loader.parseTenor("YC.B.U2.EUR.4F.G_N_A.SV_C_YM.IF_3M");
		Assert.assertEquals((double)(3 * 30) / 360, tenor, 0.0001);
	}
	@Test
	public void testLoadFile() throws IOException, ParseException {
		ECBCurveTimeSeriesFileLoader loader = new ECBCurveTimeSeriesFileLoader();
		CurveTimeSeries<DayTimePoint> curveTimeSeries = loader.load(new File("data/yc_AAA_if.csv"), 0);
		Assert.assertEquals(177, curveTimeSeries.getTermStructure().getTenors().length);
		Assert.assertEquals(1736, curveTimeSeries.getCurves().size());
	}
}
