/**
 * This package contains only the {@link com.luigisgro.cqf.Main} class, which is responsible
 * for handling the user input
 */
package com.luigisgro.cqf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.math.MathException;

import com.luigisgro.cqf.job.FDMJob;
import com.luigisgro.cqf.job.HJMJob;
import com.luigisgro.cqf.job.Job;
import com.luigisgro.cqf.job.PropertiesFDMJobConfiguration;
import com.luigisgro.cqf.job.PropertiesHJMJobConfiguration;

/**
 * Responsible for reading user input, reading configuration file
 * and running the relevant jobs
 * @author Luigi Sgro
 *
 */
public class Main {
	private static Options options;
	
	private static void usage() {
		HelpFormatter formatter = new HelpFormatter();
		PrintWriter writer = new PrintWriter(System.out);
		formatter.printHelp(writer, 80, "java " + Main.class.getCanonicalName() + " <jobSpecFile>", "", options, 4, 1, "");
		writer.flush();
	}

	public static void main(String[] args) {
		options = new Options();

		CommandLineParser parser = new PosixParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			usage();
			return;
		}
		InputStream specs = null;
		if (cmd.getArgs().length != 0) {
			try {
				File specFile = new File(cmd.getArgs()[0]);
				if (!specFile.exists()) {
					System.err.println("File not found: " + cmd.getArgs()[0]);
					usage();
					return;
				}
				specs = new FileInputStream(specFile);
			} catch (FileNotFoundException e) {
				System.err.println("Can't read file: " + cmd.getArgs()[0]);
				usage();
				return;
			}
		} else {
			usage();
			return;
		}
		Properties jobProperties = new Properties();
		try {
			jobProperties.load(specs);
		} catch (Exception e) {
			System.err.println("A problem occurred while parsing file: " + cmd.getOptionValue("specsFile") + " [" + e.getMessage() + "]");
			return;
		}
		
		String jobType = jobProperties.getProperty("job.type");
		if (jobType == null) {
			System.err.println("job.type property not specified");
		}
		if ("hjm".equals(jobType)) {
			System.out.println("HJM evaluation of interest rate derivatives with Monte Carlo simulation\n");
			PropertiesHJMJobConfiguration configuration = new PropertiesHJMJobConfiguration();
			boolean success = configuration.loadProperties(jobProperties);
			if (!success) {
				System.err.println("A problem occurred while reading HJM job properties. Aborting job.");
				return;
			}
			Job hjm = new HJMJob(configuration);
			try {
				hjm.execute();
			} catch (MathException e) {
				System.err.println("An error occurred while performing calculations: " + e.getMessage());
			}
		}
		else if ("fdm".equals(jobType)) {
			System.out.println("Option evaluation with uncertain volatility and static hedge (worst case)\n");
			PropertiesFDMJobConfiguration configuration = new PropertiesFDMJobConfiguration();
			boolean success = configuration.loadProperties(jobProperties);
			if (!success) {
				System.err.println("A problem occurred while reading FDM job properties. Aborting job.");
				return;
			}
			Job fdm = new FDMJob(configuration);
			try {
				fdm.execute();
			} catch (MathException e) {
				System.err.println("An error occurred while performing calculations: " + e.getMessage());
			}
		} else {
			System.err.println("job type: " + jobType + " unknown");
		}
	}
}
