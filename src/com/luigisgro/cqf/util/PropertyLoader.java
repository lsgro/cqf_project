package com.luigisgro.cqf.util;

import java.util.Map;

/**
 * Utility interface for property loader objects
 * @author Luigi Sgro
 *
 */
public interface PropertyLoader {
	/**
	 * Loads the properties contained in the map passed as parameter.
	 * Note that {@link java.util.Properties} implements Map<Object, Object>
	 * @param properties A map of key-value pairs
	 * @return true if the load was successful, false otherwise
	 */
	public boolean loadProperties(Map<Object, Object> properties);
}
