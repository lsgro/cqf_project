package com.luigisgro.cqf.util;

import java.io.IOException;
import java.io.Writer;

public interface CSVExportable {
	int writeLinesToCSV(Writer writer, String separator) throws IOException;
}
