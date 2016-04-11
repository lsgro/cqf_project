package com.luigisgro.cqf.job;

import java.util.Map;

import org.apache.commons.math.MathException;

/**
 * Interface for batch jobs
 * @author Luigi Sgro
 *
 */
public interface Job {
	/**
	 * Execution of the job
	 * @return True if the job was successfully executed, false otherwise
	 * @throws MathException 
	 */
	boolean execute() throws MathException;
	/**
	 * Accessor method for the job results
	 * @return A map of the job results by keyword
	 */
	Map<String, Object> getResults();
}
