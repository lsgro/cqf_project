package com.luigisgro.cqf.curve.loaders;

import java.io.File;
import java.io.IOException;

import com.luigisgro.cqf.curve.CurveTimeSeries;
import com.luigisgro.cqf.curve.DayTimePoint;

/**
 * Interface exposed classes that can load a {@link com.luigisgro.cqf.curve.CurveTimeSeries}
 * from a formatted file
 * @author Luigi Sgro
 *
 */
public interface CurveTimeSeriesLoader {
/**
 * Loads formatted data from a file and returns it in a {@link com.luigisgro.cqf.curve.CurveTimeSeries}
 * @param inputFile A file containing curve time series information
 * @param timeStep The time step between the curves
 * @return A {@link com.luigisgro.cqf.curve.CurveTimeSeries} object
 * @throws IOException
 */
CurveTimeSeries<DayTimePoint> load(File inputFile, double timeStep) throws IOException;
}