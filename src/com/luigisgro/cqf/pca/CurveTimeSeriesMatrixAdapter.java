package com.luigisgro.cqf.pca;

import com.luigisgro.cqf.curve.Curve;
import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.TimePoint;
import com.luigisgro.cqf.linalg.Matrix;

/**
 * A wrapper of {@link com.luigisgro.cqf.curve.CurveTimeSeries} that exposes the
 * {@link com.luigisgro.cqf.linalg.Matrix} interface, to enable matrix arithmetic
 * on curve time series
 * @author Luigi Sgro
 *
 * @param <T>
 */
public class CurveTimeSeriesMatrixAdapter<T extends TimePoint> extends Matrix {
	double[][] values;
	CurveTimeSeries<T> curveTimeSeries;
	
	/**
	 * Creates a new curve time series wrapper
	 * @param curveTimeSeries The curve time series to be wrapped
	 */
	public CurveTimeSeriesMatrixAdapter(CurveTimeSeries<T> curveTimeSeries) {
		this.curveTimeSeries = curveTimeSeries;
		load();
	}

	private void load() {
		int numOfRows = curveTimeSeries.getCurves().size();
		values = new double[numOfRows][];
		int i = 0;
		for (Curve curve : curveTimeSeries.getCurves().values())
			values[i++] = curve.getPoints();
	}
	
	@Override
	public int numOfRows() {
		return values.length;
	}

	@Override
	public int numOfColumns() {
		return values[0].length;
	}

	@Override
	public double get(int row, int col) {
		return values[row][col];
	}
}
