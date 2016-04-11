package com.luigisgro.cqf.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to load parameter values from a property file and converting them to objects
 * @author Luigi Sgro
 *
 */
public class PropertiesAdapter {
	public static enum Type { STRING, REAL, INT, CLASS, FILE, DATE }
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	private String message;
	private Map<Object, Object> properties;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private int numberOfGroups = 0;
	
	public PropertiesAdapter(Map<Object, Object> properties) {
		this.properties = properties;
	}
	
	public boolean loadMultiParameters(Object[][] parameterConfiguration) {
		String primaryKey = (String)parameterConfiguration[0][0];
		while (properties.containsKey(primaryKey + "." + (numberOfGroups + 1))) {
			numberOfGroups++;
		}
		for (int group = 1; group <= numberOfGroups; group++) {
			for (Object[] parameterInfo : parameterConfiguration) {
				Object[] numberedParameterInfo = parameterInfo.clone();
				numberedParameterInfo[0] = ((String)numberedParameterInfo[0]) + "." + group;
				if (!loadParameter(numberedParameterInfo))
					return false;
			}
		}
		return true;
	}
	
	public boolean loadParameter(Object[] parameterInfo) {
		String key = (String)parameterInfo[0];
		Type type = (Type)parameterInfo[1];
		Boolean mandatory = (Boolean)parameterInfo[2];
		switch (type) {
		case STRING:
			if (!loadAndValidateString(key, mandatory))
				return false;
			break;
		case REAL:
			if (!loadAndValidateDouble(key, mandatory))
				return false;
			break;
		case INT:
			if (!loadAndValidateInteger(key, mandatory))
				return false;
			break;
		case CLASS:
			if (!loadAndValidateClass(key, mandatory))
				return false;
			break;
		case FILE:
			if (!loadAndValidateFile(key, mandatory))
				return false;
			break;
		case DATE:
			if (!loadAndValidateDate(key, mandatory))
				return false;
			break;
		default:
			message = "Parameter configuration not valid for key: " + key;
			return false;
		}
		return true;
	}
	
	public boolean loadParameters(Object[][] parameterConfiguration) {
		for (Object[] parameterInfo : parameterConfiguration) {
			if (!loadParameter(parameterInfo))
				return false;
		}
		return true;
	}
	
	public String getMessage() {
		return message;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	private boolean loadAndValidateString(String key, boolean mandatory) {
		String tmp = (String) properties.get(key);
		if (mandatory && (tmp == null || "".equals(key.trim()))) {
			message = "property " + key + " not set";
			return false;
		}
		if (tmp != null && !"".equals(tmp.trim()))
			parameters.put(key, tmp);
		return true;
	}
	
	private boolean loadAndValidateInteger(String key, boolean mandatory) {
		String tmp = (String) properties.get(key);
		if (mandatory && (tmp == null || "".equals(key.trim()))) {
			message = key + " property not set";
			return false;
		}
		if (tmp != null && !"".equals(tmp.trim()))
			try {
				parameters.put(key, Integer.valueOf(tmp));
			} catch (NumberFormatException e) {
				message = "property " + key + " can't be parsed as int";
				return false;
			}
		return true;
	}

	private boolean loadAndValidateDouble(String key, boolean mandatory) {
		String tmp = (String) properties.get(key);
		if (mandatory && (tmp == null || "".equals(key.trim()))) {
			message = "property " + key + " not set";
			return false;
		}
		if (tmp != null && !"".equals(tmp.trim()))
			try {
				parameters.put(key, Double.valueOf(tmp));
			} catch (NumberFormatException e) {
				message = "property " + key + " can't be parsed as double";
				return false;
			}
		return true;
	}

	private boolean loadAndValidateClass(String key, boolean mandatory) {
		String tmp = (String) properties.get(key);
		if (mandatory && (tmp == null || "".equals(key.trim()))) {
			message = "property " + key + " not set";
			return false;
		}
		if (tmp != null && !"".equals(tmp.trim()))
			try {
				Class<?> clazz = (Class<?>) Class.forName(tmp);
				parameters.put(key, clazz);
			} catch (ClassNotFoundException e) {
				message = "could not find loader class: " + tmp + " [" + e.getMessage() + "]";
				return false;
			}
		return true;
	}

	private boolean loadAndValidateFile(String key, boolean mandatory) {
		String tmp = (String) properties.get(key);
		if (mandatory && (tmp == null || "".equals(key.trim()))) {
			message = "property " + key + " not set";
			return false;
		}
		if (tmp != null && !"".equals(tmp.trim())) {
			File inputFile = new File(tmp);
			parameters.put(key, inputFile);
		}
		return true;
	}
	
	private boolean loadAndValidateDate(String key, boolean mandatory) {
		String tmp = (String) properties.get(key);
		if (mandatory && (tmp == null || "".equals(key.trim()))) {
			message = "property " + key + " not set";
			return false;
		}
		if (tmp != null && !"".equals(tmp.trim()))
			try {
				parameters.put(key, sdf.parse(tmp));
			} catch (ParseException e) {
				message = "property " + key + " can't be parsed as date (required format: 'yyyyMMdd')";
				return false;
			}
		return true;
	}

	public int getNumberOfGroups() {
		return numberOfGroups;
	}
}
